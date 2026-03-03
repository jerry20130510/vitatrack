document.addEventListener("DOMContentLoaded", function () {
    const contentArea = document.getElementById('contentArea');
    const memberList = document.getElementById('memberList');
    const memberSearch = document.getElementById('memberSearch');

    memberList.addEventListener("click", memberInfo);
    memberSearch.addEventListener("click", function () {

        contentArea.innerHTML = `
            <div class="search-container">
                <h5>會員查詢</h5>
                <input type="text" id="keyword" placeholder="請輸入姓名、電話、地址..." />
                <button id="searchBtn" class="btn btn-sm btn-primary">搜尋</button>
                <div id="searchResult"></div>
            </div>
        `;

    //綁定搜尋事件
        document.getElementById("searchBtn").addEventListener("click", searchMember);
    });
    

});
// 會員列表功能
function memberInfo(e, page = 1) {
        e.preventDefault();

        fetch(`memberList?page=${page}`)
            .then(response => response.json())
            .then(pageResult => {
                console.log("後端回傳：", pageResult);
                const members = pageResult.content;
                const totalPages = pageResult.totalPages;
                const currentPage = pageResult.currentPage;
                
                let tableHtml = `
                <table id="memberTable" class="table table-hover align-middle">
                    <thead>
                        <tr>
                             <th>ID</th>
                            <th>姓名</th>
                            <th>Email</th>
                            <th>電話</th>
                            <th>地址</th>
                            <th>狀態</th>
                            <th>註冊日期</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                <tbody>`;
                members.forEach(m => {
                    tableHtml += `
                        <tr>
                            <td>${m.memberId}</td>
                            <td>${m.name}</td>
                            <td>${m.email}</td>
                            <td>${m.phone}</td>
                            <td>${m.address}</td>
                            <td>${m.memberStatus}</td>
                            <td>${m.registrationTime}</td>
                            <td>
                                <button class="btn btn-sm btn-primary">編輯</button>
                            </td>
                        </tr>`;

                });

                tableHtml += `</tbody></table>`;

               //動態產生分頁按鈕
            let paginationHtml = `
                <nav class="d-flex justify-content-center m-t-30">
                    <ul class="pagination">
                        <li class="page-item ${currentPage === 1 ? 'disabled' : ''}">
                            <a class="page-link" href="#" onclick="memberInfo(event, ${currentPage - 1})">上一頁</a>
                        </li>`;

            for (let i = 1; i <= totalPages; i++) {
                paginationHtml += `
                    <li class="page-item ${i === currentPage ? 'active' : ''}">
                        <a class="page-link" href="#" onclick="memberInfo(event, ${i})">${i}</a>
                    </li>`;
            }

            paginationHtml += `
                        <li class="page-item ${currentPage === totalPages ? 'disabled' : ''}">
                            <a class="page-link" href="#" onclick="memberInfo(event, ${currentPage + 1})">下一頁</a>
                        </li>
                    </ul>
                </nav>`;

                
                contentArea.innerHTML = tableHtml + paginationHtml;
            })
            .catch(error => {
                console.error('Error fetching member list:', error);
            });

    }

    //點擊會員查詢
    

    // 會員關鍵字查詢功能
    function searchMember(e) {
    e.preventDefault();
    const keyword = document.getElementById('keyword').value.trim();      

    if (!keyword || keyword === "" ) {
        alert("請輸入搜尋關鍵字");
        return;
    }

    fetch(`memberSearch?keyword=${encodeURIComponent(keyword)}`)
        .then(response => response.json())
        .then(members => {
            console.log(members);
            renderResult(members.content);
        })
        .catch(error => {
            console.error("錯誤:", error);
        });;
    }
            

    function renderResult(members) {
        const searchResult = document.getElementById('searchResult');
        if (!members || members.length === 0) {
            searchResult.innerHTML = "<p>沒有找到符合條件的會員。</p>";
            return;
        }
        let tableHtml = `
            <table id="searchResultTable" class="table table-hover align-middle">
                  <thead>
                        <tr>
                            <th>ID</th>
                            <th>姓名</th>
                            <th>Email</th>
                            <th>電話</th>
                            <th>地址</th>
                            <th>狀態</th>
                            <th>註冊日期</th>
                        </tr>
                    </thead>
                <tbody>`;       
        members.forEach(m => {
            tableHtml += `
                        <tr>
                            <td>${m.memberId}</td>
                            <td>${m.name}</td>
                            <td>${m.email}</td>
                            <td>${m.phone}</td>
                            <td>${m.address}</td>
                            <td>${m.memberStatus}</td>
                            <td>${m.registrationTime}</td>
                            
                        </tr>`;
        });
        tableHtml += `</tbody></table>`;
        searchResult.innerHTML = tableHtml;
    }