// 監聽選單點擊
// 根據點擊的分頁，抓取資料渲染對應html

document.addEventListener("DOMContentLoaded", function () {

    const contentArea = document.getElementById('content-area');
    const menuLinks = document.querySelectorAll('.menu-link');
    const profile = menuLinks[0];
    const logoutBtn = document.getElementById('logoutBtn');
    const memberInfo = document.getElementById('memberInfo');

    memberInfo.addEventListener("click", info);
    profile.addEventListener("click", info);


  function info(e) {
        e.preventDefault();

        fetch('profile') // 你的 Servlet
            .then(res => res.json())
            .then(member => {
                console.log(member);
                contentArea.innerHTML = `
                        <div class="member-card">
                            <header class="member-card-header">會員資訊</header>
                            <div class="member-card-body">
                                <div class="member-form">
                                    <div class="form-row"><label>使用者帳號</label><p class="readonly">${member.email}</p></div>
                                    <div class="form-row"><label>姓名</label><p class="readonly">${member.name}</p>
                                        <button class="mn-btn-1" id="nameBtn"type="button" ><span>編輯</span></button>
                                    </div>
                                    <div class="form-row"><label>Email</label><p class="readonly">${member.email}</p></div>
                                    <div class="form-row"><label>密碼</label><p class="readonly">${member.password}</p>
                                        <button class="mn-btn-1 " id="passwordBtn"type="button"><span>編輯</span></button>
                                    </div>
                                    <div class="form-row"><label>地址</label><p class="readonly">${member.address}</p>
                                        <button class="mn-btn-1 " id="addressBtn"type="button"><span>編輯</span></button>
                                    </div>
                                    <div class="form-row"><label>手機號碼</label><p class="readonly">${member.phone}</p>
                                      <button class="mn-btn-1 " id="phoneBtn"type="button"><span>編輯</span></button>
                                    </div>
                                </div>
                                
                            </div>
                        </div>`;
            });
    };

    contentArea.addEventListener('click', function (e) {
        //點擊按鈕要跳出輸入框
        //找到編輯按鈕
        const btn = e.target.closest('.mn-btn-1');
        console.log(btn);
        if (!btn) return;

        //找到與按鈕同層級的p標籤
        const p = btn.parentElement.querySelector('.readonly');
        console.log(p);




    });

    logoutBtn.addEventListener('click', function (e) {
        e.preventDefault();
        fetch('logout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        })
            .then(result => result.json())
            .then(result => {
                        if (result.success) {
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