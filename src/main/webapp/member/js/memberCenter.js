// 監聽選單點擊
// 根據點擊的分頁，抓取資料渲染對應html

document.addEventListener("DOMContentLoaded", function () {

    const contentArea = document.getElementById('content-area');
    const menuLinks = document.querySelectorAll('.menu-link');
    const profile = menuLinks[0];
    

    profile.addEventListener("click", function (e) {
        e.preventDefault();

        fetch('profile') // 你的 Servlet
            .then(res => res.json())
            .then(member => {
                console.log(member);

                contentArea.innerHTML = `
                        <div class="member-card">
                            <header class="member-card-header">會員資料</header>
                            <div class="member-card-body">
                                <div class="member-form">
                                    <div class="form-row"><label>使用者帳號</label><p class="readonly">${member.email}</p></div>
                                    <div class="form-row"><label>姓名</label><p class="readonly">${member.name}</p>
                                        <button class="mn-btn-1 btn" id="nameBtn"type="button" ><span>編輯</span></button>
                                    </div>

                                    <div class="form-row"><label>Email</label><p class="readonly">${member.email}</p></div>
                                    <div class="form-row"><label>密碼</label><p class="readonly">${member.password}</p>
                                        <button class="mn-btn-1 btn" id="passwordBtn"type="button"><span>編輯</span></button>
                                    </div>
                                    <div class="form-row"><label>地址</label><p class="readonly">${member.address}</p>
                                        <button class="mn-btn-1 btn" id="addressBtn"type="button"><span>編輯</span></button>
                                    </div>
                                    <div class="form-row"><label>手機號碼</label><p class="readonly">${member.phone}</p>
                                      <button class="mn-btn-1 btn" id="phoneBtn"type="button"><span>編輯</span></button>
                                    </div>
                                </div>
                                
                            </div>
                        </div>`;
            });




    });

    const nameBtn = document.getElementById('nameBtn');
    const emailBtn = document.getElementById('emailBtn');
    const passwordBtn = document.getElementById('passwordBtn');
    const addressBtn = document.getElementById('addressBtn');


    nameBtn.addEventListener('click',function(){
    //點擊按鈕要跳出姓名輸入框
     fetch('profile') // 你的 Servlet
            .then(res => res.json())
            .then(member => {
         contentArea.innerHTML = `
                        <div class="member-card">
                            <header class="member-card-header">會員資料</header>
                            <div class="member-card-body">
                                <div class="member-form">
                                    <div class="form-row"><label>姓名</label><input type="text">${member.name}</div>
                                        <button class="mn-btn-1 btn" id="nameBtn"type="button" ><span>儲存</span></button>
                                        <button class="mn-btn-1 btn" id="nameBtn"type="button" ><span>取消</span></button>
                                    </div>
                                </div>
                                
                            </div>
                        </div>`;
            });
    
    });



    
    emailBtn.addEventListener('click',function(){

        
    });

    passwordBtn.addEventListener('click',function(){

        
    });

    addressBtn.addEventListener('click',function(){

        
    });

    

});