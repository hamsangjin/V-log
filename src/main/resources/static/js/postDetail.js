document.addEventListener('DOMContentLoaded', function() {
    const postId = document.getElementById('likeBtn').getAttribute('data-post-id');
    checkLikeStatus(postId);
});

function checkLikeStatus(postId) {
    fetch(`/vlog/like/status?postId=${postId}`, {
        method: 'GET',
        credentials: 'include'
    })
        .then(response => response.json())
        .then(data => {
            const likeButton = document.getElementById('likeBtn');
            likeButton.textContent = data.liked ? 'Unlike' : 'Like';
        })
        .catch(error => console.error('Error:', error));
}

function toggleLike(postId) {
    fetch(`/vlog/like?postId=${postId}`, {
        method: 'POST',
        credentials: 'include'
    })
        .then(response => response.json())
        .then(data => {
            const likeButton = document.getElementById('likeBtn');
            likeButton.textContent = data.status === 'Liked' ? 'Unlike' : 'Like';
        })
        .catch(error => console.error('Error:', error));
}
