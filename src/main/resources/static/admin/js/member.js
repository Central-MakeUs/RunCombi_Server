function toggleSection(selector) {
    const section = document.querySelector(selector);
    const isHidden = window.getComputedStyle(section).display === "none";
    section.style.display = isHidden ? "block" : "none";
}

function deleteMember(btn) {
    const row = btn.closest("tr");
    const tds = row.querySelectorAll("td");

    const id = tds[0].innerText;
    const email = tds[2].innerText;
    const nickname = tds[3].innerText;

    if (confirm("ID: " + id + "\n닉네임: " + nickname + "\n이메일: " + email + "\n회원 정보를 삭제하시겠습니까?")) {
        const data = {
            memberId: id
        };

        adminPostJson("/admin/deleteMember", data, {
            onSuccess: function () {
                alert("삭제 성공");
                location.reload();
            },
            onError: function () {
                alert("에러 발생");
                location.reload();
            },
            onNetworkError: function () {
                alert("에러 발생");
                location.reload();
            }
        });
    }
}

function formatValue(value, suffix) {
    if (value === null || value === undefined || value === "") {
        return "-";
    }

    return suffix ? String(value) + suffix : String(value);
}

function renderMemberDetail(detail) {
    const member = detail.member || {};
    const petList = Array.isArray(detail.petList) ? detail.petList : [];
    const petImageUrlSet = new Set(
        petList
            .map(pet => (pet && pet.petImageUrl ? pet.petImageUrl : ""))
            .filter(Boolean)
    );

    document.getElementById("memberDetailMemberId").textContent = formatValue(member.memberId);
    document.getElementById("memberDetailEmail").textContent = formatValue(member.email);
    document.getElementById("memberDetailNickname").textContent = formatValue(member.nickname);
    document.getElementById("memberDetailProvider").textContent = formatValue(member.provider);
    document.getElementById("memberDetailGender").textContent = formatValue(member.gender);
    document.getElementById("memberDetailHeight").textContent = formatValue(member.height, "cm");
    document.getElementById("memberDetailWeight").textContent = formatValue(member.weight, "kg");
    document.getElementById("memberDetailStatus").textContent = formatValue(detail.memberStatus || member.isActive);
    document.getElementById("memberDetailTerms").textContent =
        Array.isArray(member.memberTerms) && member.memberTerms.length > 0 ? member.memberTerms.join(", ") : "-";

    const memberImage = document.getElementById("memberDetailImage");
    const memberImageEmpty = document.getElementById("memberDetailImageEmpty");
    const hasMemberProfileKey = member.profileImgKey ? String(member.profileImgKey).startsWith("member/") : true;
    const isPetImageUrl = member.profileImgUrl ? petImageUrlSet.has(member.profileImgUrl) : false;
    const shouldShowMemberImage = Boolean(member.profileImgUrl) && hasMemberProfileKey && !isPetImageUrl;

    if (shouldShowMemberImage) {
        memberImage.src = member.profileImgUrl;
        memberImage.classList.remove("hidden-modal");
        memberImageEmpty.classList.add("hidden-modal");
    } else {
        memberImage.src = "";
        memberImage.classList.add("hidden-modal");
        memberImageEmpty.classList.remove("hidden-modal");
    }

    const petListEl = document.getElementById("memberPetList");
    petListEl.innerHTML = "";

    if (petList.length === 0) {
        const empty = document.createElement("div");
        empty.className = "member-pet-card";
        empty.textContent = "등록된 반려견 정보가 없습니다.";
        petListEl.appendChild(empty);
        return;
    }

    petList.forEach((pet, index) => {
        const card = document.createElement("div");
        card.className = "member-pet-card";

        const title = document.createElement("h5");
        title.textContent = "반려견 " + (index + 1);
        card.appendChild(title);

        if (pet.petImageUrl) {
            const image = document.createElement("img");
            image.className = "member-pet-image";
            image.alt = "반려견 이미지";
            image.src = pet.petImageUrl;
            card.appendChild(image);
        } else {
            const emptyImage = document.createElement("div");
            emptyImage.className = "member-pet-image-empty";
            emptyImage.textContent = "이미지 없음";
            card.appendChild(emptyImage);
        }

        const meta = document.createElement("div");
        meta.className = "member-pet-meta";

        const rows = [
            { label: "이름", value: formatValue(pet.name) },
            { label: "나이", value: formatValue(pet.age, "살") },
            { label: "몸무게", value: formatValue(pet.weight, "kg") },
            { label: "산책 스타일", value: formatValue(pet.runStyle) }
        ];

        rows.forEach(item => {
            const labelEl = document.createElement("div");
            labelEl.className = "label";
            labelEl.textContent = item.label;

            const valueEl = document.createElement("div");
            valueEl.textContent = item.value;

            meta.appendChild(labelEl);
            meta.appendChild(valueEl);
        });

        card.appendChild(meta);
        petListEl.appendChild(card);
    });
}

function openMemberDetailModal(memberId) {
    adminGetJson("/admin/member/detail?memberId=" + encodeURIComponent(memberId), {
        onSuccess: function (body) {
            if (!body || !body.isSuccess || !body.result) {
                const msg = body && body.message ? body.message : "회원 상세 조회 중 오류가 발생했습니다.";
                alert(msg);
                return;
            }

            renderMemberDetail(body.result);
            document.getElementById("memberDetailModalDim").classList.remove("hidden-modal");
            document.getElementById("memberDetailModal").classList.remove("hidden-modal");
        },
        onError: function (body) {
            const msg = body && body.message ? body.message : "회원 상세 조회 중 오류가 발생했습니다.";
            alert(msg);
        },
        onNetworkError: function () {
            alert("회원 상세 조회 중 네트워크 오류가 발생했습니다.");
        }
    });
}

function closeMemberDetailModal() {
    document.getElementById("memberDetailModalDim").classList.add("hidden-modal");
    document.getElementById("memberDetailModal").classList.add("hidden-modal");
}

function filterMemberRows() {
    const keywordInput = document.getElementById("memberSearchKeyword");
    const keyword = keywordInput ? keywordInput.value.trim().toLowerCase() : "";
    const rows = document.querySelectorAll(".member-row");

    let visibleCount = 0;
    rows.forEach(row => {
        const searchableText = row.textContent.toLowerCase();
        const isMatch = keyword === "" || searchableText.includes(keyword);
        row.style.display = isMatch ? "" : "none";
        if (isMatch) {
            visibleCount++;
        }
    });

    const resultEl = document.getElementById("memberSearchResult");
    if (resultEl) {
        resultEl.textContent = "검색 결과: " + visibleCount + "명";
    }
}

const viewMemberBtn = document.querySelector(".view-member");
if (viewMemberBtn) {
    viewMemberBtn.addEventListener("click", function () {
        toggleSection(".member-list-section");
    });
}

const searchBtn = document.getElementById("memberSearchBtn");
if (searchBtn) {
    searchBtn.addEventListener("click", filterMemberRows);
}

const searchResetBtn = document.getElementById("memberSearchResetBtn");
if (searchResetBtn) {
    searchResetBtn.addEventListener("click", function () {
        const input = document.getElementById("memberSearchKeyword");
        if (input) {
            input.value = "";
        }
        filterMemberRows();
    });
}

const searchInput = document.getElementById("memberSearchKeyword");
if (searchInput) {
    searchInput.addEventListener("keydown", function (event) {
        if (event.key === "Enter") {
            event.preventDefault();
            filterMemberRows();
        }
    });
}

const memberTableBody = document.querySelector(".member-list-section tbody");
if (memberTableBody) {
    memberTableBody.addEventListener("click", function (event) {
        if (event.target.closest(".delete-member-btn")) {
            return;
        }

        const row = event.target.closest(".member-row");
        if (!row) {
            return;
        }

        const memberId = row.dataset.memberId;
        if (memberId) {
            openMemberDetailModal(memberId);
        }
    });
}

const memberModalDim = document.getElementById("memberDetailModalDim");
if (memberModalDim) {
    memberModalDim.addEventListener("click", closeMemberDetailModal);
}

document.addEventListener("keydown", function (event) {
    if (event.key === "Escape") {
        closeMemberDetailModal();
    }
});

filterMemberRows();

window.deleteMember = deleteMember;
window.closeMemberDetailModal = closeMemberDetailModal;
