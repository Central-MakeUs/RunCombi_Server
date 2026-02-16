function updateVersion() {
    const os = document.getElementById("OS").value;
    const version = document.getElementById("version").value;
    const updateDetail = document.getElementById("updateDetail").value;

    if (version === "") {
        alert("버전 정보는 필수 입력 항목입니다.");
        return;
    }

    if (/[^0-9.]/.test(version)) {
        alert("올바른 버전 정보가 아닙니다.\n입력한 버전 정보 : '" + version + "'");
        return;
    }

    if (updateDetail === "") {
        alert("버전 변경 사항은 필수 입력 항목입니다.");
        return;
    }

    const data = {
        os: os,
        version: version,
        updateDetail: updateDetail
    };

    if (confirm("OS: " + os + "\nversion: " + version + "\n위 버전으로 변경하시겠습니까?")) {
        adminPostJson("/admin/updateVersion", data, {
            onSuccess: function () {
                alert("버전 수정 성공");
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

window.updateVersion = updateVersion;
