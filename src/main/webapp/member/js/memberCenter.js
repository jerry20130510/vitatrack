// 監聽選單點擊
// 根據點擊的分頁，抓取資料渲染對應html

document.addEventListener("DOMContentLoaded", function () {
    const contentArea = document.getElementById('content-area');
    const menuLinks = document.querySelectorAll('.menu-link');
    const profile = menuLinks[0];
    const logoutBtn = document.getElementById('logoutBtn');
    const memberInfo = document.getElementById('memberInfo');
    const privacy = menuLinks[1];

    loadMember();

    memberInfo.addEventListener("click", info);
    profile.addEventListener("click", info);
    privacy.addEventListener("click", showDeleteBtn);

    function loadMember() {
    fetch('profile')
        .then(res => res.json())
        .then(member => {
            currentMember = member;
            console.log("這是會員中心拿到的會員資料：", member);
            const welcomeText = document.getElementById('welcomeText');
            if (welcomeText) {
                welcomeText.textContent = `${member.name}，歡迎回來`;
            }
        });
}
    function info(e) {
        e.preventDefault();

        fetch('profile')
            .then(res => res.json())
            .then(member => {
                console.log(member);
                contentArea.innerHTML = `
                 <div class="member-card">
                    <header class="member-card-header">會員資訊</header>
                    <div class="member-card-body">
                        <div class="member-form">
                            <div class="form-row" data-editable="false"><label>使用者帳號</label><p class="readonly">${member.email}</p></div>
                            <div class="form-row" data-field="name"><label>姓名</label><p class="readonly">${member.name}</p></div>
                            <div class="form-row" data-editable="false"><label>Email</label><p class="readonly">${member.email}</p></div>
                            <div class="form-row" data-field="password"><label>密碼</label><p class="readonly">${member.password}</p></div>
                            <div class="form-row" data-field="address"><label>地址</label><p class="readonly">${member.address}</p></div>
                            <div class="form-row" data-field="phone"><label>手機號碼</label><p class="readonly">${member.phone}</p></div>

                            <div class="form-actions" style="margin-top: 20px; text-align: center;">
                                <button  class="mn-btn-1 " id="editBtn" type="button"><span>編輯</span></button>
                                <button class="mn-btn-1" id="saveBtn" type="button">
                                     <span>儲存變更</span>
                                </button>
                            </div>
                        </div>                      
                    </div>
                 </div>`;
            });


    };

    contentArea.addEventListener('click', function (e) {
        const btn = e.target.closest('button');
        if (!btn) {
            return;
        }
        if (btn.id === 'saveBtn') {
            saveChanges();
        }
        if (btn && btn.id === 'editBtn') {
            console.dir(btn);
            enableEdit('editBtn');
        }
        if (btn && btn.id === 'deleteBtn') {
            deleteAccount();
        }
    });




    function enableEdit() {
        const rows = document.querySelectorAll('.form-row');
        rows.forEach(row => {
            if (row.dataset.editable === "false") return;
            const p = row.querySelector('p.readonly');
            if (!p) return; //如果找不到 <p> 元素，直接返回
            const input = document.createElement('input');
            input.type = 'text';
            input.value = p.textContent; //用當前顯示的值作為預設值
            input.className = 'edit-input';
            row.replaceChild(input, p);
        });
    }

    function saveChanges() {

        const updatedData = {};
        //取得所有有 class="form-row" 的元素
        const rows = document.querySelectorAll('.form-row');

        rows.forEach(row => {
            const field = row.dataset.field;
            //找到裡面的 input
            const input = row.querySelector('input.edit-input');
            //如果這列有 field，且找到 input，就把值放進 updatedData
            if (field && input) {
                updatedData[field] = input.value;
            }
        });

        fetch('profile', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(updatedData)
        })
            .then(result => result.json())
            .then(result => {
                console.log();

                if (result.success) {
                    alert("資料已更新");
                    location.reload();
                } else {
                    alert("更新失敗：" + result.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert("更新過程中發生錯誤，請稍後再試。");
            });
    }


    function showDeleteBtn(){
        contentArea.innerHTML = `
        <main class="member-main">
						<div class="member-card">
							<header class="member-card-header">
								隱私設定
							</header>
							<div class="member-card-body">
								<!-- 左：表單 -->
								<div class="member-form">

									<div class="form-row">
										<label>刪除帳號</label>
										<p class="readonly"></p>
										<button class="mn-btn-1 btn" id="deleteBtn"
											type="button"><span>刪除</span></button>
									</div>
								</div>
							</div>
						</div>

					</main>

                `;
    }

    function deleteAccount() {
        if (!confirm("確定要刪除帳號嗎？此操作無法復原！")) {
            return;
        }

        fetch('deleteAccount',{
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({                                  
              email: currentMember.email
            })
        })
        .then(result => result.json())
        .then(result =>{
            if(result.success) {
                alert(result.message);
                window.location.href = 'index.html';
            } else {
                alert("刪除帳號失敗：" + result.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("刪除帳號過程中發生錯誤，請稍後再試。");
        });
    }





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