document.addEventListener('DOMContentLoaded', function() {
    const likeBtn = document.getElementById('likeBtn');
    const followBtn = document.getElementById('followBtn');

    if (likeBtn) {
        const postId = likeBtn.getAttribute('data-post-id');
        checkLikeStatus(postId);
    }

    if (followBtn) {
        const username = followBtn.getAttribute('data-username');
        checkFollowStatus(username);

        followBtn.addEventListener("click", function() {
            toggleFollow(username);
        });
    }
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

function checkFollowStatus(username) {
    fetch(`/vlog/follow/status?username=${username}`, {
        method: 'GET',
        credentials: 'include'
    })
        .then(response => response.json())
        .then(data => {
            const followButton = document.getElementById("followBtn");
            followButton.textContent = data.following ? 'Unfollow' : 'Follow';
        })
        .catch(error => console.error('Error:', error));
}

function toggleFollow(username) {
    fetch(`/vlog/follow?username=${username}`, {
        method: 'POST',
        credentials: 'include'
    })
        .then(response => response.json())
        .then(data => {
            const followButton = document.getElementById("followBtn");
            followButton.textContent = data.status === 'Following' ? 'Unfollow' : 'Follow';
        })
        .catch(error => console.error('Error:', error));
}
