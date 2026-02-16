function showPopup(content) {
    document.getElementById("popupDetails").innerHTML = content;
    document.getElementById("modalDim").style.display = "block";
    document.getElementById("popupContainer").style.display = "block";
}

function closePopup() {
    document.getElementById("modalDim").style.display = "none";
    document.getElementById("popupContainer").style.display = "none";
}

function moveEventDetail(el) {
    const row = el.closest("tr");
    const tds = row.querySelectorAll("td");
    const id = tds[0].innerText;

    const detailHtml = `
        <p><strong>ID:</strong> ${id}</p>
        <p>상세 내용 넣을 부분</p>
    `;

    showPopup(detailHtml);
}

window.moveEventDetail = moveEventDetail;
window.closePopup = closePopup;
