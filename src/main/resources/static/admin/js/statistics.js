const bucketLabelMap = {
    DAY: "1일 단위",
    WEEK: "1주일 단위",
    MONTH: "1달 단위",
    YEAR: "1년 단위"
};

const startDateInput = document.getElementById("startDate");
const endDateInput = document.getElementById("endDate");
const chartEl = document.getElementById("barChart");
const chartEmptyEl = document.getElementById("chartEmpty");
const tableBodyEl = document.getElementById("statsTableBody");
const summaryBucketEl = document.getElementById("summaryBucket");
const summaryRangeEl = document.getElementById("summaryRange");
const summaryTotalEl = document.getElementById("summaryTotal");
const customSearchBtn = document.getElementById("customSearchBtn");
const bucketButtons = document.querySelectorAll(".bucket-btn");

const statsModalDim = document.getElementById("statsModalDim");
const statsModal = document.getElementById("statsModal");
const statsModalTitle = document.getElementById("statsModalTitle");
const statsModalCloseBtn = document.getElementById("statsModalCloseBtn");

const statsMembersView = document.getElementById("statsMembersView");
const statsRunsView = document.getElementById("statsRunsView");
const statsSelectedRangeText = document.getElementById("statsSelectedRangeText");
const statsMembersSummaryText = document.getElementById("statsMembersSummaryText");
const statsMembersTableBody = document.getElementById("statsMembersTableBody");
const statsBackToMembersBtn = document.getElementById("statsBackToMembersBtn");
const statsSelectedMemberText = document.getElementById("statsSelectedMemberText");
const statsRunsTableBody = document.getElementById("statsRunsTableBody");

let currentBucket = "WEEK";
let selectedBucketLabel = "";
let selectedBucketStartDate = "";
let selectedBucketEndDate = "";

function toIsoDate(date) {
    return date.toISOString().slice(0, 10);
}

function setDefaultRange(bucketType) {
    const end = new Date();
    const start = new Date(end);

    if (bucketType === "DAY") {
        start.setDate(end.getDate() - 6);
    } else if (bucketType === "WEEK") {
        start.setDate(end.getDate() - (11 * 7));
    } else if (bucketType === "MONTH") {
        start.setMonth(end.getMonth() - 11);
    } else if (bucketType === "YEAR") {
        start.setFullYear(end.getFullYear() - 4);
    }

    startDateInput.value = toIsoDate(start);
    endDateInput.value = toIsoDate(end);
}

function renderSummary(data) {
    summaryBucketEl.textContent = bucketLabelMap[data.bucketType] || data.bucketType;
    summaryRangeEl.textContent = data.startDate + " ~ " + data.endDate;
    summaryTotalEl.textContent = data.totalCount + "건";
}

function clearTableBody(tableBody) {
    tableBody.innerHTML = "";
}

function createCell(text) {
    const td = document.createElement("td");
    td.textContent = text;
    return td;
}

function formatDateTime(value) {
    if (!value) {
        return "-";
    }
    return String(value).replace("T", " ").slice(0, 19);
}

function formatDistance(value) {
    if (value === null || value === undefined) {
        return "-";
    }
    return Number(value).toFixed(2);
}

function renderMembersTable(result) {
    clearTableBody(statsMembersTableBody);

    const members = Array.isArray(result.members) ? result.members : [];
    if (members.length === 0) {
        const tr = document.createElement("tr");
        const td = document.createElement("td");
        td.colSpan = 8;
        td.textContent = "해당 기간에 이용 회원이 없습니다.";
        tr.appendChild(td);
        statsMembersTableBody.appendChild(tr);
        return;
    }

    members.forEach(member => {
        const tr = document.createElement("tr");
        tr.dataset.memberId = member.memberId;
        tr.dataset.nickname = member.nickname || "";

        tr.appendChild(createCell(String(member.memberId)));
        tr.appendChild(createCell(member.nickname || "-"));
        tr.appendChild(createCell(member.email || "-"));
        tr.appendChild(createCell(member.provider || "-"));
        tr.appendChild(createCell(String(member.runCount ?? 0)));
        tr.appendChild(createCell(String(member.totalRunTime ?? 0)));
        tr.appendChild(createCell(formatDistance(member.totalRunDistance)));
        tr.appendChild(createCell(formatDateTime(member.lastRunDate)));

        statsMembersTableBody.appendChild(tr);
    });
}

function renderRunsTable(result) {
    clearTableBody(statsRunsTableBody);

    const runs = Array.isArray(result.runs) ? result.runs : [];
    if (runs.length === 0) {
        const tr = document.createElement("tr");
        const td = document.createElement("td");
        td.colSpan = 9;
        td.textContent = "해당 회원의 런 정보가 없습니다.";
        tr.appendChild(td);
        statsRunsTableBody.appendChild(tr);
        return;
    }

    runs.forEach(run => {
        const petText = Array.isArray(run.pets) && run.pets.length > 0
            ? run.pets.map(pet => {
                const petName = pet.petName || "이름없음";
                const petId = pet.petId ?? "-";
                const petCal = pet.petCal ?? 0;
                return petName + " (ID:" + petId + ", " + petCal + "kcal)";
            }).join(", ")
            : "-";

        const tr = document.createElement("tr");
        tr.appendChild(createCell(String(run.runId ?? "-")));
        tr.appendChild(createCell(formatDateTime(run.regDate)));
        tr.appendChild(createCell(String(run.runTime ?? "-")));
        tr.appendChild(createCell(formatDistance(run.runDistance)));
        tr.appendChild(createCell(String(run.memberCal ?? "-")));
        tr.appendChild(createCell(run.memberRunStyle || "-"));
        tr.appendChild(createCell(run.runEvaluating || "-"));
        tr.appendChild(createCell(petText));
        tr.appendChild(createCell(run.memo || "-"));

        statsRunsTableBody.appendChild(tr);
    });
}

function openStatsModal() {
    statsModalDim.classList.remove("hidden");
    statsModal.classList.remove("hidden");
}

function closeStatsModal() {
    statsModalDim.classList.add("hidden");
    statsModal.classList.add("hidden");
    statsRunsView.classList.add("hidden");
    statsMembersView.classList.remove("hidden");
    statsSelectedMemberText.textContent = "-";
    clearTableBody(statsRunsTableBody);
}

function showMembersView() {
    statsRunsView.classList.add("hidden");
    statsMembersView.classList.remove("hidden");
}

function showRunsView() {
    statsMembersView.classList.add("hidden");
    statsRunsView.classList.remove("hidden");
}

function loadMemberRuns(memberId, nickname) {
    const params = new URLSearchParams({
        memberId: memberId,
        startDate: selectedBucketStartDate,
        endDate: selectedBucketEndDate
    });

    adminGetJson("/admin/usage-stats/member-runs?" + params.toString(), {
        onSuccess: function (body) {
            if (!body || !body.isSuccess || !body.result) {
                const msg = body && body.message ? body.message : "런 상세 조회 중 오류가 발생했습니다.";
                alert(msg);
                return;
            }

            renderRunsTable(body.result);
            statsSelectedMemberText.textContent =
                "회원: " + (nickname || body.result.nickname || "-") + " / 런 " + (body.result.totalRunCount ?? 0) + "건";
            statsModalTitle.textContent = selectedBucketLabel + " 런 상세";
            showRunsView();
        },
        onError: function (body) {
            const msg = body && body.message ? body.message : "런 상세 조회 중 오류가 발생했습니다.";
            alert(msg);
        },
        onNetworkError: function () {
            alert("런 상세 조회 중 네트워크 오류가 발생했습니다.");
        }
    });
}

function loadUsageMembers(bucketPoint) {
    const bucketStartDate = bucketPoint.bucketStartDate;
    const bucketEndDate = bucketPoint.bucketEndDate;

    if (!bucketStartDate || !bucketEndDate) {
        alert("선택 구간 정보를 찾을 수 없습니다.");
        return;
    }

    selectedBucketLabel = bucketPoint.label;
    selectedBucketStartDate = bucketStartDate;
    selectedBucketEndDate = bucketEndDate;

    const params = new URLSearchParams({
        startDate: bucketStartDate,
        endDate: bucketEndDate
    });

    adminGetJson("/admin/usage-stats/members?" + params.toString(), {
        onSuccess: function (body) {
            if (!body || !body.isSuccess || !body.result) {
                const msg = body && body.message ? body.message : "이용 회원 조회 중 오류가 발생했습니다.";
                alert(msg);
                return;
            }

            const result = body.result;
            renderMembersTable(result);
            statsSelectedRangeText.textContent = "선택 구간: " + bucketPoint.label + " (" + result.startDate + " ~ " + result.endDate + ")";
            statsMembersSummaryText.textContent =
                "총 산책 " + (result.totalRunCount ?? 0) + "건 / 이용 회원 " + (result.totalMemberCount ?? 0) + "명";
            statsModalTitle.textContent = bucketPoint.label + " 이용 회원";
            showMembersView();
            openStatsModal();
        },
        onError: function (body) {
            const msg = body && body.message ? body.message : "이용 회원 조회 중 오류가 발생했습니다.";
            alert(msg);
        },
        onNetworkError: function () {
            alert("이용 회원 조회 중 네트워크 오류가 발생했습니다.");
        }
    });
}

function renderChart(series) {
    chartEl.innerHTML = "";
    clearTableBody(tableBodyEl);

    if (!series || series.length === 0) {
        chartEmptyEl.style.display = "block";
        return;
    }

    chartEmptyEl.style.display = "none";
    const maxCount = Math.max(...series.map(item => item.count), 1);

    series.forEach(item => {
        const barHeight = Math.max(2, Math.round((item.count / maxCount) * 220));

        const barItem = document.createElement("div");
        barItem.className = "bar-item";

        const barCount = document.createElement("span");
        barCount.className = "bar-count";
        barCount.textContent = String(item.count);

        const bar = document.createElement("div");
        bar.className = "bar";
        bar.style.height = barHeight + "px";

        const barLabel = document.createElement("span");
        barLabel.className = "bar-label";
        barLabel.textContent = item.label;

        barItem.appendChild(barCount);
        barItem.appendChild(bar);
        barItem.appendChild(barLabel);
        barItem.addEventListener("click", function () {
            loadUsageMembers(item);
        });
        chartEl.appendChild(barItem);

        const tr = document.createElement("tr");
        tr.appendChild(createCell(item.label));
        tr.appendChild(createCell(String(item.count)));
        tableBodyEl.appendChild(tr);
    });
}

function loadStats(useCustomRange) {
    if (useCustomRange) {
        if (!startDateInput.value || !endDateInput.value) {
            alert("커스텀 조회 시 시작일과 종료일을 모두 선택해주세요.");
            return;
        }
    } else {
        setDefaultRange(currentBucket);
    }

    const params = new URLSearchParams({
        bucketType: currentBucket,
        startDate: startDateInput.value,
        endDate: endDateInput.value
    });

    adminGetJson("/admin/usage-stats?" + params.toString(), {
        onSuccess: function (body) {
            if (!body || !body.isSuccess) {
                const msg = body && body.message ? body.message : "통계 조회 중 오류가 발생했습니다.";
                alert(msg);
                return;
            }

            renderSummary(body.result);
            renderChart(body.result.series);
        },
        onError: function (body) {
            const msg = body && body.message ? body.message : "통계 조회 중 오류가 발생했습니다.";
            alert(msg);
        },
        onNetworkError: function () {
            alert("통계 조회 중 네트워크 오류가 발생했습니다.");
        }
    });
}

bucketButtons.forEach(btn => {
    btn.addEventListener("click", function () {
        bucketButtons.forEach(b => b.classList.remove("active"));
        this.classList.add("active");
        currentBucket = this.dataset.bucket;
        loadStats(false);
    });
});

customSearchBtn.addEventListener("click", function () {
    loadStats(true);
});

statsModalCloseBtn.addEventListener("click", closeStatsModal);
statsModalDim.addEventListener("click", closeStatsModal);

statsBackToMembersBtn.addEventListener("click", function () {
    statsModalTitle.textContent = selectedBucketLabel + " 이용 회원";
    showMembersView();
});

statsMembersTableBody.addEventListener("click", function (event) {
    const row = event.target.closest("tr[data-member-id]");
    if (!row) {
        return;
    }
    loadMemberRuns(row.dataset.memberId, row.dataset.nickname);
});

document.addEventListener("keydown", function (event) {
    if (event.key === "Escape" && !statsModal.classList.contains("hidden")) {
        closeStatsModal();
    }
});

setDefaultRange(currentBucket);
loadStats(true);
