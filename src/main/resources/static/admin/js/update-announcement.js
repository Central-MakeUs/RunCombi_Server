function changeImage(imageUrl) {
    const imgViewDiv = document.querySelector(".imgViewDiv");
    const imgTag = document.querySelector(".announcementImage");

    if (!imgViewDiv || !imgTag) {
        return;
    }

    if (imageUrl !== "") {
        imgViewDiv.hidden = false;
        imgTag.src = imageUrl;
    } else {
        imgViewDiv.hidden = true;
    }
}

function submitForm() {
    const announcementId = document.getElementById("announcementId").value;
    const type = document.getElementById("type").innerText;
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

    if (type === "공지사항") {
        if (announcementImageUrl === "") {
            data = {
                announcementId: announcementId,
                announcementType: type,
                display: display,
                title: title,
                content: content,
                startDate: "1000-01-01",
                endDate: "9999-12-31"
            };
        } else {
            data = {
                announcementId: announcementId,
                announcementType: type,
                display: display,
                title: title,
                content: content,
                announcementImageUrl: announcementImageUrl,
                startDate: "1000-01-01",
                endDate: "9999-12-31"
            };
        }
    } else if (type === "이벤트") {
        if (startDate === "" || endDate === "") {
            alert("이벤트 날짜 선택은 필수입니다.");
            return false;
        }

        if (announcementImageUrl === "" && eventApplyUrl === "") {
            data = {
                announcementId: announcementId,
                announcementType: type,
                display: display,
                title: title,
                content: content,
                startDate: startDate,
                endDate: endDate
            };
        } else if (announcementImageUrl === "") {
            data = {
                announcementId: announcementId,
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
                announcementId: announcementId,
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

    adminPostJson("/admin/updateAnnouncement", data, {
        onSuccess: function () {
            alert("수정 성공");
            location.reload();
        },
        onError: function () {
            alert("에러 발생");
            location.href = "/admin/announcement";
        },
        onNetworkError: function () {
            alert("에러 발생");
            location.href = "/admin/announcement";
        }
    });

    return false;
}

window.changeImage = changeImage;
window.submitForm = submitForm;
