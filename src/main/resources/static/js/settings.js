document.getElementById("uploadButton").addEventListener("click", function() {
    document.getElementById("imageInput").click();
});

document.getElementById("imageInput").addEventListener("change", function() {
    document.getElementById("uploadForm").submit();
});

document.getElementById("editButton").addEventListener("click", function() {
    document.getElementById("displayName").style.display = 'none';
    document.getElementById("displayInfo").style.display = 'none';
    document.getElementById("editButton").style.display = 'none';
    document.getElementById("editForm").style.display = 'block';
});

document.getElementById("editButton1").addEventListener("click", function() {
    document.getElementById("displayTitle").style.display = 'none';
    document.getElementById("editButton1").style.display = 'none';
    document.getElementById("editTitleForm").style.display = 'flex';
});

document.getElementById("editButton2").addEventListener("click", function() {
    document.getElementById("displayEmail").style.display = 'none';
    document.getElementById("editButton2").style.display = 'none';
    document.getElementById("editEmailForm").style.display = 'flex';
});