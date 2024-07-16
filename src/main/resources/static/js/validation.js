let isUsernameChecked = false;
let isEmailChecked = false;
let isPasswordChecked = false;

// 아이디 중복 확인 함수
function checkUsername() {
    const username = document.getElementById("username").value;
    return fetch(`/vlog/api/users/check-username?username=${username}`)
        .then(response => response.json())
        .then(data => {
            isUsernameChecked = !data; // data가 true면 중복, false면 사용 가능
        });
}

// 이메일 중복 확인 함수
function checkEmail() {
    const email = document.getElementById("email").value;
    return fetch(`/vlog/api/users/check-email?email=${email}`)
        .then(response => response.json())
        .then(data => {
            isEmailChecked = !data; // data가 true면 중복, false면 사용 가능
        });
}

// 비밀번호 확인 함수
function validatePassword() {
    const password = document.getElementById("password").value;
    const passwordConfirm = document.getElementById("passwordConfirm").value;
    isPasswordChecked = (password === passwordConfirm);
}

// 폼 제출 함수
function submitForm(event) {
    event.preventDefault(); // 폼 기본 제출 동작 방지

    Promise.all([checkUsername(), checkEmail()]).then(() => {
        if (!isUsernameChecked) {
            alert("아이디가 중복되었습니다.");
            return;
        }

        if (!isEmailChecked) {
            alert("이메일이 중복되었습니다.");
            return;
        }

        validatePassword();
        if (!isPasswordChecked) {
            alert("비밀번호가 일치하지 않습니다.");
            return;
        }

        const formData = {
            username: document.getElementById("username").value,
            password: document.getElementById("password").value,
            name: document.getElementById("name").value,
            email: document.getElementById("email").value
        };

        fetch('/vlog/userreg', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        })
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    alert(data.error);
                } else {
                    window.location.href = '/vlog/loginform'; // 회원가입 완료 후 로그인 페이지로 이동
                }
            })
            .catch(error => {
                alert("회원가입 중 오류가 발생했습니다.");
                console.error('Error:', error);
            });
    });
}