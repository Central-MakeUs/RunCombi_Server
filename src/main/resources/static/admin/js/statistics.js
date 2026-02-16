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

let currentBucket = "WEEK";

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

function renderChart(series) {
    chartEl.innerHTML = "";
    tableBodyEl.innerHTML = "";

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
        barItem.innerHTML = `
            <span class="bar-count">${item.count}</span>
            <div class="bar" data-height="${barHeight}"></div>
            <span class="bar-label">${item.label}</span>
        `;
        chartEl.appendChild(barItem);
        const barEl = barItem.querySelector(".bar");
        barEl.style.height = `${barHeight}px`;

        const tr = document.createElement("tr");
        tr.innerHTML = `<td>${item.label}</td><td>${item.count}</td>`;
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

setDefaultRange(currentBucket);
loadStats(true);
