function toggleSection(selector) {
    const section = document.querySelector(selector);
    const isHidden = window.getComputedStyle(section).display === "none";
    section.style.display = isHidden ? "block" : "none";
}

function submitForm() {
    const type = document.querySelector('input[name="type"]:checked').value;
    const display = document.querySelector('input[name="display"]:checked').value;
    const title = document.getElementById("title").value;
    const content = document.getElementById("content").value;
    const startDate = document.getElementById("startDate").value;
    const endDate = document.getElementById("endDate").value;
    const announcementImageUrl = document.getElementById("announcementImageUrl").value;
    const eventApplyUrl = document.getElementById("eventApplyUrl").value;

    if (title === "" || content === "") {
        alert("제목 및 내용은 필수 입력입니다.");
        return false;
    }

    let data = {};

    if (type === "NOTICE") {
        if (announcementImageUrl === "") {
            data = {
                announcementType: type,
                display: display,
                title: title,
                content: content,
                startDate: "1000-01-01",
                endDate: "9999-12-31"
            };
        } else {
            data = {
                announcementType: type,
                display: display,
                title: title,
                content: content,
                announcementImageUrl: announcementImageUrl,
                startDate: "1000-01-01",
                endDate: "9999-12-31"
            };
        }
    } else if (type === "EVENT") {
        if (startDate === "" || endDate === "") {
            alert("이벤트 날짜 선택은 필수입니다.");
            return false;
        }

        if (announcementImageUrl === "" && eventApplyUrl === "") {
            data = {
                announcementType: type,
                display: display,
                title: title,
                content: content,
                startDate: startDate,
                endDate: endDate
            };
        } else if (announcementImageUrl === "") {
            data = {
                announcementType: type,
                display: display,
                title: title,
                content: content,
                startDate: startDate,
                endDate: endDate,
                eventApplyUrl: eventApplyUrl
            };
        } else if (eventApplyUrl === "") {
            data = {
                announcementType: type,
                display: display,
                title: title,
                content: content,
                startDate: startDate,
                endDate: endDate,
                announcementImageUrl: announcementImageUrl
            };
        }
    }

    adminPostJson("/admin/addAnnouncement", data, {
        onSuccess: function () {
            alert("등록 성공");
            location.reload();
        },
        onError: function () {
            alert("에러 발생");
        },
        onNetworkError: function () {
            alert("에러 발생");
        }
    });

    return false;
}

function toggleFields() {
    const selectedType = document.querySelector('input[name="type"]:checked').value;
    const commonFields = document.querySelectorAll(".common-group");
    const eventFields = document.querySelectorAll(".event-group");

    if (selectedType === "NOTICE") {
        commonFields.forEach(el => (el.style.display = ""));
        eventFields.forEach(el => (el.style.display = "none"));
    } else if (selectedType === "EVENT") {
        commonFields.forEach(el => (el.style.display = ""));
        eventFields.forEach(el => (el.style.display = "block"));
    }
}

function deleteAnnouncement(btn) {
    const row = btn.closest("tr");
    const tds = row.querySelectorAll("td");

    const id = tds[0].innerText;
    const type = tds[1].innerText;
    const title = tds[2].innerText;

    if (confirm("ID: " + id + "\n제목: " + title + "\n" + type + "을(를) 삭제하시겠습니까?")) {
        const data = {
            announcementId: id
        };

        adminPostJson("/admin/deleteAnnouncement", data, {
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

function updateAnnouncement(btn) {
    const row = btn.closest("tr");
    const tds = row.querySelectorAll("td");
    const id = tds[0].innerText;

    location.href = "/admin/updateAnnouncement/" + id;
}

const addAnnouncementBtn = document.querySelector(".add-announcement");
const deleteAnnouncementBtn = document.querySelector(".delete-announcement");

if (addAnnouncementBtn) {
    addAnnouncementBtn.addEventListener("click", function () {
        toggleSection(".form-section");
    });
}

if (deleteAnnouncementBtn) {
    deleteAnnouncementBtn.addEventListener("click", function () {
        toggleSection(".event-list-section");
    });
}

document.querySelectorAll('input[name="type"]').forEach(function (radio) {
    radio.addEventListener("change", toggleFields);
});

toggleFields();

window.submitForm = submitForm;
window.deleteAnnouncement = deleteAnnouncement;
window.updateAnnouncement = updateAnnouncement;
