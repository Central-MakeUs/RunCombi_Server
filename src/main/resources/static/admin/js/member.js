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

const viewMemberBtn = document.querySelector(".view-member");
if (viewMemberBtn) {
    viewMemberBtn.addEventListener("click", function () {
        toggleSection(".member-list-section");
    });
}

window.deleteMember = deleteMember;
