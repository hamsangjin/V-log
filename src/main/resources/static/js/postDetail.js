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

function toggleReplyForm(commentId) {
    const replyForm = document.getElementById('reply-form-' + commentId);
    if (replyForm) {
        replyForm.style.display = (replyForm.style.display === 'none' || replyForm.style.display === '') ? 'block' : 'none';
    } else {
        console.error('Reply form not found for commentId: ' + commentId);
    }
}

function hideReplyForm(commentId) {
    const replyForm = document.getElementById('reply-form-' + commentId);
    if (replyForm) {
        replyForm.style.display = 'none';
    } else {
        console.error('Reply form not found for commentId: ' + commentId);
    }
}

function toggleReplies(commentId) {
    const replies = document.getElementById('replies-' + commentId);
    if (replies) {
        replies.style.display = (replies.style.display === 'none' || replies.style.display === '') ? 'block' : 'none';
    } else {
        console.error('Replies not found for commentId: ' + commentId);
    }
}