let simplemde = new SimpleMDE({ element: document.getElementById("editor") });

document.getElementById("submitBtn").addEventListener("click", function() {
    document.getElementById("content").value = simplemde.value();

    // 태그 캡처
    let tagElements = document.querySelectorAll('#tagsContainer .tag');
    let tags = Array.from(tagElements).map(tag => tag.textContent).join(',');
    let tagsInputHidden = document.createElement('input');
    tagsInputHidden.type = 'hidden';
    tagsInputHidden.name = 'tags';
    tagsInputHidden.value = tags;
    document.getElementById("postForm").appendChild(tagsInputHidden);

    document.getElementById("postForm").submit();
});

document.getElementById('tagsInput').addEventListener('keydown', function(event) {
    let key = event.key;
    let input = document.getElementById('tagsInput');
    let container = document.getElementById('tagsContainer');

    function createTagElement(text) {
        let span = document.createElement('span');
        span.className = 'tag';
        span.textContent = text;
        span.onclick = function() { container.removeChild(span); };
        return span;
    }

    if (key === ',' || key === 'Enter') {
        event.preventDefault();
        let tagText = input.value.trim().replace(/,/, '');
        if (tagText !== "") {
            let tagElement = createTagElement(tagText);
            container.insertBefore(tagElement, input);
            input.value = '';
        }
    }
});

document.getElementById('tagsContainer').addEventListener('click', function() {
    document.getElementById('tagsInput').focus();
});


document.getElementById("createSeriesBtn").addEventListener("click", function() {
    let newSeries = document.getElementById("newSeries").value.trim();
    if (newSeries !== "") {
        fetch('/vlog/createseries', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ title: newSeries })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('시리즈가 생성되었습니다.');
                    let seriesSelect = document.getElementById("series");
                    let option = document.createElement("option");
                    option.value = data.series.id;
                    option.text = data.series.title;
                    seriesSelect.add(option);
                    seriesSelect.value = data.series.id;
                    document.getElementById("newSeries").value = '';
                } else {
                    alert('시리즈 생성에 실패했습니다.');
                }
            });
    }
});