document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("resetForm");
    const resetBtn = document.getElementById("resetBtn");
    const newPassword = document.getElementById("newPassword");
    const confirmPassword = document.getElementById("confirmPassword");


    let isSubmitting = false;

    // ===== 從網址取得 token =====
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get("token");


    if (!token || token.trim() === "") {
        showAlert("連結無效或已失效");
        resetBtn.disabled = true;
        return;
    }
    //驗證新密碼格式
    newPassword.addEventListener("blur", function () {
        const pwd = newPassword.value.trim();
        const hint = document.getElementById("newPasswordHint");
        console.log(hint);
        const rule = /^(?=.*[A-Za-z])(?=.*\d).{8,}$/;
        if (!pwd.match(rule)) {
            hint.textContent = "密碼至少為8個字元，且至少包含1個英文字母(大小寫皆可)與1個數字";
            hint.style.color = "red";
        } else {
            hint.textContent = "格式正確";
            hint.style.color = "green";
        }
    });
    //驗證確認密碼
    confirmPassword.addEventListener("blur", function () {
        const confirmPwd = confirmPassword.value.trim();
        const hint = document.getElementById("confirmPasswordHint");
        console.log(hint);

        if (confirmPwd !== pwd || confirmPwd === "") {
            hint.textContent = "與設定密碼不一致";
            hint.style.color = "red";
        } else {
            hint.textContent = "與設定密碼一致";
            hint.style.color = "green";
        }
    });

    // ===== 點擊重設密碼 =====
    resetBtn.addEventListener("click", function () {

        if (isSubmitting) return;
        const pwd = newPassword.value.trim();
        const confirmPwd = confirmPassword.value.trim();
        if (!pwd || !pwd.match(/^(?=.*[A-Za-z])(?=.*\d).{8,}$/)) {
            showAlert("密碼格式錯誤或未填寫!");
            return;
        }

        if (confirmPwd !== pwd || confirmPwd === "") {
            showAlert("與設定密碼不一致，請重新輸入!");
            return;
        }

        isSubmitting = true;
        resetBtn.disabled = true;

        fetch('resetPassword', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                token: token,
                newPassword: pwd
            })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("HTTP 錯誤");
                }
                return response.json();
            })
            .then(result => {

                if (result.success) {
                    showAlert("密碼重設成功<br>請重新登入", true);
                    setTimeout(() => {
                        window.location.href = "login.html";
                    }, 2000);
                } else {
                    showAlert(result.message || "重設失敗");
                    isSubmitting = false;
                    resetBtn.disabled = false;
                }

            })
            .catch(error => {
                console.error(error);
                showAlert("系統發生錯誤，請稍後再試");
                isSubmitting = false;
                resetBtn.disabled = false;
            });

    });

});