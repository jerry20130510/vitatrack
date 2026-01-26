document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("registerForm"); // 注意抓 form 的 id
    //STEP 1
    // 取得所有需要驗證的輸入欄位
    // 這裡用 querySelectorAll 一次抓取，方便統一管理
    const inputs = document.querySelectorAll("input[required], input[pattern]");
    const nameInput = document.getElementById("name");
    const email = document.getElementById("email");
    const phonenumber = document.getElementById("phonenumber");
    const addressInput = document.getElementById("address");
    const password = document.getElementById("password");
    const repassword = document.getElementById("re-password");
    const customAlert = document.getElementById("customAlert"); // 註冊成功視窗
    const goLoginBtn = document.getElementById("goLoginBtn"); // 登入按鈕


    // STEP2. 通用邏輯：監聽所有欄位的 Blur 事件
    // ==========================================
    // 這裡我們只做一件事：給欄位加上 "touched" 標籤
    // 這樣 CSS 就會知道「可以顯示紅框了」
    [phonenumber, email, password, repassword].forEach(input => {
        input.addEventListener("blur", function() {
            this.classList.add("touched"); 
            
            // ★ 關鍵：這裡絕對不要寫 reportValidity()，這樣就不會卡住游標
        });
    });


    // ===== Email 即時驗證 =====
    email.addEventListener("input", function () {
        email.setCustomValidity("");
        if (email.value && !email.validity.valid) {
            email.setCustomValidity("Email格式輸入不符，請重新輸入!\n" + email.title);
        }
    });
    

    //手機號碼輸入即時檢查
    phonenumber.addEventListener("input", function () {
        // 步驟 1: 先清除之前的自定義錯誤訊息
        // 這一步非常重要！這會讓 validity.valid 變回「僅根據 HTML pattern 判斷」的狀態
        // 若格式錯誤 → 自訂錯誤訊息
        phonenumber.setCustomValidity("");

        // 步驟 2: 檢查 HTML 原生的驗證結果 (pattern 是否符合)
        if (!phonenumber.validity.valid) {
            // 自己的錯誤訊息
            let customMsg = "手機格式輸入不符，請重新輸入!";
            // 取出 HTML title 的內容
            let titleMsg = phonenumber.title;

            phonenumber.setCustomValidity(customMsg + "\n" + titleMsg);
        }
        // 步驟 3: 顯示驗證結果
        // 注意：在 input 事件一直呼叫 reportValidity 會導致打字時一直跳視窗，建議斟酌使用
        // 當滑鼠離開欄位時才跳出提示框
        //  phonenumber.reportValidity();

    });
  


    // ===== 密碼即時驗證 =====
    password.addEventListener("input", function () {
        password.setCustomValidity("");
        if (password.value && !password.validity.valid) {
            password.setCustomValidity("密碼格式不符，請重新輸入!");
        }
    });

    // 確認密碼即時驗證

    repassword.addEventListener("input", function () {
        repassword.setCustomValidity("");
        if (repassword.value !== password.value) {
            repassword.setCustomValidity("與設定密碼不一致，請重新輸入!");
        }
        // repassword.reportValidity();
    });


    // 表單送出
    form.addEventListener("submit", function (event) {
        event.preventDefault();

        // 為了讓所有錯誤欄位都顯示紅框，我們在送出時把所有欄位都標記為 touched
        inputs.forEach(input => input.classList.add("touched"));
        // 檢查 A: 瀏覽器原生檢查 (包含 required, pattern, 以及上面 input 設定的 setCustomValidity)
        if (!form.checkValidity()) {
            form.reportValidity(); // 這時候才跳出第一個錯誤提示框
            return; // 停止送出
        }
        // 檢查 B: 雙重確認密碼 (雖然 input 寫了，但在 submit 再擋一次比較保險)
        if (repassword.value !== password.value) {
            repassword.setCustomValidity("兩次密碼輸入不一致!");
            repassword.reportValidity();
            return;
        }

        // ===== 模擬向後端發送資料 =====
        // const formData = {
        //     name: document.getElementById("name").value,
        //     email: email.value,
        //     phone: phonenumber.value,
        //     address: document.getElementById("address").value,
        //     password: password.value
        // };
        // // 使用 Fetch API 發送 POST 請求
        //  fetch("/register", {
        //     method: "POST",
        //     headers: { "Content-Type": "application/json" },
        //     body: JSON.stringify(formData)
        // })
        //     .then(res => res.json())
        //     .then(data => {
        //         if (data.success) {
        //             // 顯示成功提示視窗
        //             customAlert.style.display = "flex";
        //             setTimeout(() => window.location.href = "/login.html", 2000);
        //         } else {
        //             alert(data.message || "註冊失敗，請重試！");
        //         }
        //     })
        //     .catch(err => {
        //         console.error(err);
        //         alert("網路錯誤，請稍後再試！");
        //     });

        // 全部通過顯示自訂彈窗
        customAlert.style.display = "flex";
        
    });




    // 登入會員按鈕 → 跳轉登入頁
    goLoginBtn.addEventListener("click", function () {
        window.location.href = "login.html";
    });

});
