document.addEventListener("DOMContentLoaded", function() {
    const editIntroBtns = document.querySelectorAll(".edit-intro-btn");
    const introFormContainer = document.querySelector(".intro-form-container");
    const introText = document.querySelector(".intro-text");
    const noIntro = document.querySelector(".no-intro");
    const followBtn = document.getElementById("followBtn");

    editIntroBtns.forEach(function(editIntroBtn) {
        editIntroBtn.addEventListener("click", function() {
            introFormContainer.style.display = "block";
            if (introText) introText.style.display = "none";
            if (noIntro) noIntro.style.display = "none";
            editIntroBtn.style.display = "none";
        });
    });

    if (followBtn) {
        const username = followBtn.getAttribute("data-username");
        checkFollowStatus(username);

        followBtn.addEventListener("click", function() {
            toggleFollow(username);
        });
    }
});

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
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => response.json())
        .then(data => {
            const followButton = document.getElementById("followBtn");
            followButton.textContent = data.status === 'Following' ? 'Unfollow' : 'Follow';

            // Fetch updated follower and following counts
            fetch(`/vlog/follow/counts?username=${username}`, {
                method: 'GET',
                credentials: 'include'
            })
                .then(response => response.json())
                .then(counts => {
                    const followerCountElement = document.querySelector(".follow-info .follower-count");
                    const followingCountElement = document.querySelector(".follow-info .following-count");

                    followerCountElement.textContent = `${counts.followerCount} 팔로워`;
                    followingCountElement.textContent = `${counts.followingCount} 팔로잉`;
                });
        })
        .catch(error => console.error('Error:', error));
}