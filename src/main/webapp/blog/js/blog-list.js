// blog-list.js - Article list page logic

let currentPage = 0;
let currentCategory = '';
let currentAuthor = '';

document.querySelectorAll('.nav-link').forEach(link => {
    link.addEventListener('click', (e) => {
        e.preventDefault();
        document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
        link.classList.add('active');
        currentCategory = link.dataset.category || '';
        currentPage = 0;
        loadArticles();
    });
});

function loadArticles() {
    toggle(el('mn-overlay'), true);
    el('articles-container').style.display = 'none';
    toggle(el('pagination'), false);
    
    const params = new URLSearchParams({ page: currentPage, size: 6 });
    if (currentCategory) params.append('category', currentCategory);
    if (currentAuthor) params.append('authorSlug', currentAuthor);

    fetch(`${API_BASE}/articles?${params}`)
        .then(response => {
            if (!response.ok) throw new Error('載入失敗');
            return response.json();
        })
        .then(result => {
            if (!result.success) {
                throw new Error(result.errMsg);
            }
            const data = result.data;
            renderArticles(data.articles);
            renderPagination(data);
            el('articles-container').style.display = 'flex';
        })
        .catch(error => {
            console.error('[Load] error:', error);
            const container = el('articles-container');
            if (container) {
                container.style.display = 'block';
                container.innerHTML = `
                    <div class="col-12">
                        <div class="error-display">
                            <i class="ri-wifi-off-line error-icon"></i>
                            <h3 class="mn-title error-title">無法載入文章</h3>
                            <p class="error-message">請檢查網路連線再試一次</p>
                            <button onclick="loadArticles()" class="mn-btn-2"><span>重新載入</span></button>
                        </div>
                    </div>
                `;
            }
        })
        .finally(() => {
            toggle(el('mn-overlay'), false);
        });
}

function renderArticles(articles) {
    const html = articles.map(a => `
        <div class="col-md-4 col-sm-6 col-12 mn-blog-block m-b-24">
            <div class="mn-blog-card">
                <div class="blog-info blog-card-clickable" onclick="location.href='blog-detail.html?slug=${a.titleSlug}'">
                    <figure class="blog-img blog-card-image">
                        <a href="blog-detail.html?slug=${a.titleSlug}">
                            <img src="${a.imageUrl || PLACEHOLDER_IMAGE}" 
                                 onerror="this.onerror=null; this.src='${PLACEHOLDER_IMAGE}';"
                                 alt="${escapeHtml(a.titleDisplay)}" />
                        </a>
                    </figure>
                    <div class="detail blog-card-detail">
                        <label>${formatDate(a.createdAt)} - <a href="blog-list.html?category=${encodeURIComponent(a.category)}" onclick="event.stopPropagation()">${escapeHtml(a.category)}</a></label>
                        <h3><a href="blog-detail.html?slug=${a.titleSlug}">${escapeHtml(a.titleDisplay)}</a></h3>
                        <div class="more-info">
                            <a href="blog-detail.html?slug=${a.titleSlug}">閱讀更多<i class="ri-arrow-right-double-line"></i></a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
    el('articles-container').innerHTML = html;
}

function renderPagination(data) {
    if (data.totalPages <= 1) {
        toggle(el('pagination'), false);
        return;
    }

    const isFirst = data.page === 0;
    const isLast = data.page >= data.totalPages - 1;

    el('pagination').style.display = 'flex';
    el('pagination-info').textContent = `顯示第 ${data.page * data.size + 1}-${Math.min((data.page + 1) * data.size, data.totalElements)} 項，共 ${data.totalElements} 項`;

    const start = Math.max(0, data.page - 2);
    const end = Math.min(data.totalPages - 1, start + 4);
    const adjustedStart = Math.max(0, end - 4);
    const pages = Array.from({ length: end - adjustedStart + 1 }, (_, i) => adjustedStart + i);
    
    const buttons = [];
    if (!isFirst) buttons.push({ page: data.page - 1, html: '<li><a class="prev"><i class="ri-arrow-left-double-line"></i></a></li>' });
    pages.forEach(p => buttons.push({ page: p, html: `<li><a class="${p === data.page ? 'active' : ''}">${p + 1}</a></li>` }));
    if (!isLast) buttons.push({ page: data.page + 1, html: '<li><a class="next"><i class="ri-arrow-right-double-line"></i></a></li>' });
    
    el('pagination-buttons').innerHTML = buttons.map(b => b.html).join('');
    el('pagination-buttons').querySelectorAll('a').forEach((btn, i) => {
        btn.onclick = () => goToPage(buttons[i].page);
    });
}

function goToPage(page) {
    currentPage = page;
    loadArticles();
}

document.addEventListener('DOMContentLoaded', () => {
    // Check for category parameter in URL
    const urlParams = new URLSearchParams(window.location.search);
    const categoryParam = urlParams.get('category');
    const authorParam = urlParams.get('authorSlug');
    
    if (categoryParam) {
        currentCategory = categoryParam;
    }
    if (authorParam) {
        currentAuthor = authorParam;
    }
    loadArticles();
});
