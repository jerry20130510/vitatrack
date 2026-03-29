document.addEventListener("DOMContentLoaded", function () {
    const guestHeader = document.getElementById("header-guest");
    const memberHeader = document.getElementById("header-member");
    const memberInfo = document.getElementById('memberInfo');
    const orderInfoBtn = document.getElementById('orderInfo');
    const logoutBtn = document.getElementById('logoutBtn');

    memberInfo.addEventListener("click", info);
    orderInfoBtn.addEventListener("click", (e) => orderInfo(e));

    //auth-check
    //------------驗證是否會員決定首頁Header------------
    if (!guestHeader || !memberHeader) {
        console.error("Header element not found");
        return;
    }

    // 加一個判斷，確保抓到元素才執行
    const token = localStorage.getItem("token");

    if (token) {
        guestHeader.style.display = "none";
        memberHeader.style.display = "block";
    } else {
        guestHeader.style.display = "block";
        memberHeader.style.display = "none";
    }
    //------------查看會員資料------------
    function info(e) {
        e.preventDefault();
        window.location.href="memberCenter.html";

    };

    //------------查看訂單------------
    window.orderInfo = function (e, page = 1) {
        e.preventDefault();
        window.location.href="memberCenter.html";

    }

    //------------會員登出------------
    logoutBtn.addEventListener('click', function (e) {
        e.preventDefault();
        fetch('logout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        })
            .then(result => result.json())
            .then(result => {
                if (result.success) {
                    localStorage.removeItem("token");
                    alert(result.message);
                    window.location.href = 'index.html';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert("登出過程中發生錯誤，請稍後再試。");
            });
    });

});