document.addEventListener("DOMContentLoaded", function () {

    const form = document.querySelector("form");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    const customAlert = document.getElementById("customAlert");
    const goMemberBtn = document.getElementById("goMemberBtn");

        // 忘記密碼相關
    const fpBtn = document.querySelector(".mn-login-fp a");
    const fpAlert = document.getElementById("fpAlert");
    const fpEmailInput = document.getElementById("fpEmail");
    const fpSendBtn = document.getElementById("fpSendBtn");
    const fpCloseBtn = document.getElementById("fpCloseBtn");


    console.log("JS 有執行到這裡！");

    // Email 格式檢查
    function isValidEmail(email) {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailPattern.test(email);
    }

    // 密碼強度（至少8字、含大小寫與數字）
    // function isStrongPassword(password) {
    //     const pwPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
    //     return pwPattern.test(password);
    // }

    form.addEventListener("submit", function (event) {
        event.preventDefault(); // 防止 405

        console.log("submit 被觸發！");

        const email = emailInput.value.trim();
        const password = passwordInput.value.trim();

        // ① 基本 Email 格式驗證
        if (!isValidEmail(email)) {
            alert("請輸入有效的 Email 格式！");
            return;
        }

        // ② 密碼強度判斷
        // if (!isStrongPassword(password)) {
        //     alert("密碼需至少8碼，且包含大小寫英文字母與數字!");
        //     return;
        // }

        // ③ 模擬正確帳密（你可以改成後端回傳）
        const correctEmail = "test@gmail.com";
        const correctPassword = "Test1234";
      

        if (email === correctEmail && password === correctPassword) {
            console.log("登入成功！");
            
            // *** 這裡才顯示彈窗 ***
            customAlert.style.display = "flex";
        } else {
            alert("帳號或密碼錯誤，請重新輸入！");
        }
    });

    // 彈窗按鈕 → 前往會員頁面
    goMemberBtn.addEventListener("click", function () {
        console.log("前往會員頁按鈕被按！");

        // 換成你的會員頁
        window.location.href = "product-full-width.html";
    });



    // 打開忘記密碼視窗
    fpBtn.addEventListener("click", function (event) {
        event.preventDefault(); // 防止跳頁
        fpAlert.style.display = "flex";
    });

    // 送出忘記密碼
    fpSendBtn.addEventListener("click", function () {
        const email = fpEmailInput.value.trim();

        if (!isValidEmail(email)) {
            alert("請輸入有效的 Email");
            return;
        }

        // 這裡你之後可改成 AJAX 送後端
        alert("重設密碼連結已寄出到：" + email);

        fpAlert.style.display = "none"; // 關閉彈窗
    });

    // 關閉忘記密碼彈窗
    fpCloseBtn.addEventListener("click", function () {
        fpAlert.style.display = "none";
    });

});

