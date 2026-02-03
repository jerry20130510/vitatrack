// blog-detail.js - Article detail page logic

// localStorage helpers
const getLikedArticles = () => JSON.parse(localStorage.getItem('likedArticles') || '[]');
const getSharedArticles = () => JSON.parse(localStorage.getItem('sharedArticles') || '[]');
const addLikedArticle = slug => localStorage.setItem('likedArticles', JSON.stringify([...getLikedArticles(), slug]));
const addSharedArticle = slug => localStorage.setItem('sharedArticles', JSON.stringify([...getSharedArticles(), slug]));


const showError = (msg, isNotFound = false) => {
    const container = el('article-content') || document.querySelector('.mn-main-content');
    if (!container) return;
    
    const icon = isNotFound ? 'ri-file-unknow-line' : 'ri-wifi-off-line';
    const title = isNotFound ? '找不到文章' : '無法載入文章';
    const desc = isNotFound ? '此文章可能被移除或不存在' : '請檢查網路連線再試一次';
    const btnText = isNotFound ? '返回文章列表' : '重新載入';
    const btnAction = isNotFound ? 'href="blog-list.html"' : 'href="javascript:location.reload()"';
    
    container.innerHTML = `
        <div class="col-12">
            <div class="error-display">
                <i class="${icon} error-icon"></i>
                <h3 class="mn-title error-title">${title}</h3>
                <p class="error-message">${desc}</p>
                <a ${btnAction} class="mn-btn-2 error-button"><span>${btnText}</span></a>
            </div>
        </div>
    `;
    container.style.display = 'block';
};

function loadArticleDetail() {
    const slug = new URLSearchParams(location.search).get('slug');
    
    if (!slug) {
        showError('文章不存在', true);
        return;
    }

    toggle(el('mn-overlay'), true);
    toggle(el('article-content'), false);
    
    fetch(`${API_BASE}/articles/${slug}`)
        .then(response => {
            if (!response.ok) {
                if (response.status === 404) {
                    throw new Error('NOT_FOUND');
                }
                throw new Error('LOAD_ERROR');
            }
            return response.json();
        })
        .then(result => {
            // Check success wrapper
            if (!result.success) {
                throw new Error(result.errMsg || 'LOAD_ERROR');
            }
            
            const a = result.data;  // Extract data from wrapper
            
            // Increment view count silently (no UI update)
            fetch(`${API_BASE}/articles/view?slug=${slug}`, { 
                method: 'POST'
            }).catch(() => {});
            
            el('page-title').textContent = a.titleDisplay;
            el('breadcrumb-title').textContent = a.titleDisplay;
            el('breadcrumb-category').textContent = a.category;
            el('featured-image').src = a.imageUrl;
            el('featured-image').onerror = function() {
                this.onerror = null;
                this.src = PLACEHOLDER_IMAGE;
            };
            el('featured-image').alt = a.titleDisplay;
            el('article-title').textContent = a.titleDisplay;
            
            // Author avatar with fallback
            const authorName = a.authorDisplayName || '作者';
            const avatarImg = el('author-avatar');
            const fallback = el('author-avatar-fallback');
            
            const showFallback = () => {
                avatarImg.style.display = 'none';
                fallback.style.display = 'flex';
                fallback.textContent = authorName.charAt(0).toUpperCase();
                fallback.style.background = `hsl(${authorName.charCodeAt(0) * 5 % 360}, 50%, 45%)`;
            };
            
            if (a.authorProfileImage) {
                avatarImg.crossOrigin = 'anonymous';
                avatarImg.referrerPolicy = 'no-referrer';
                avatarImg.onerror = () => showFallback();
                avatarImg.onload = () => {
                    avatarImg.style.display = 'block';
                    fallback.style.display = 'none';
                };
                avatarImg.src = a.authorProfileImage;
            } else {
                showFallback();
            }
            avatarImg.alt = authorName;
            if (a.authorSlug) {
                el('author-name').innerHTML = `<a href="blog-list.html?authorSlug=${encodeURIComponent(a.authorSlug)}" class="author-link">${authorName}</a>`;
            } else {
                el('author-name').textContent = authorName;
            }
            el('article-date').textContent = formatDate(a.createdAt);
            el('category-link').textContent = a.category;
            el('category-link').href = `blog-list.html?category=${encodeURIComponent(a.category)}`;
            el('category-link').className = 'category-link';
            
            el('article-content-text').innerHTML = a.content;
            el('like-count').textContent = a.totalLikes;
            el('share-count').textContent = a.totalShares;
            
            // Check localStorage and update button states
            const likeBtn = el('like-btn');
            const shareBtn = el('share-btn');
            
            if (getLikedArticles().includes(slug)) {
                likeBtn.classList.replace('btn-outline-danger', 'btn-danger');
                likeBtn.classList.add('liked');
                likeBtn.querySelector('i').classList.replace('ri-heart-line', 'ri-heart-fill');
                el('like-text').textContent = '已按讚';
            }
            
            if (getSharedArticles().includes(slug)) {
                shareBtn.classList.replace('btn-outline-primary', 'btn-primary');
                shareBtn.querySelector('i').classList.replace('ri-share-line', 'ri-share-fill');
                el('share-text').textContent = '已分享';
            }
            
            toggle(el('mn-overlay'), false);
            toggle(el('article-content'), true);
        })
        .catch(error => {
            console.error('[Load] error:', error);
            if (error.message === 'NOT_FOUND') {
                showError('文章不存在', true);
            } else {
                showError('載入文章失敗，請稍後再試');
            }
        });
}

document.addEventListener('DOMContentLoaded', loadArticleDetail);

function toggleLike() {
    const slug = new URLSearchParams(location.search).get('slug');
    
    // Check if already liked
    if (getLikedArticles().includes(slug)) {
        return;
    }
    
    const btn = el('like-btn');
    
    fetch(`${API_BASE}/articles/like?slug=${slug}`, { 
        method: 'POST'
    })
        .then(response => response.json())
        .then(result => {
            if (!result.success) {
                console.error('[Like] error:', result.errMsg);
                return;
            }
            // Update UI
            btn.classList.replace('btn-outline-danger', 'btn-danger');
            btn.classList.add('liked');
            btn.querySelector('i').classList.replace('ri-heart-line', 'ri-heart-fill');
            el('like-text').textContent = '已按讚';
            el('like-count').textContent = result.data.totalLikes;
            
            // Save to localStorage
            addLikedArticle(slug);
        })
        .catch(error => console.error('[Like] error:', error));
}

function shareArticle() {
    const slug = new URLSearchParams(location.search).get('slug');
    const btn = el('share-btn');
    const alreadyShared = getSharedArticles().includes(slug);
    
    const copyAndNotify = () => {
        navigator.clipboard.writeText(location.href)
            .then(() => showToast(alreadyShared ? '文章連結已複製到剪貼簿！' : '感謝分享！文章連結已複製到剪貼簿！', { type: 'success' }))
            .catch(error => console.error('[Copy] error:', error));
    };
    
    if (alreadyShared) {
        copyAndNotify();
        return;
    }
    
    fetch(`${API_BASE}/articles/share?slug=${slug}`, { 
        method: 'POST'
    })
        .then(response => response.json())
        .then(result => {
            if (!result.success) {
                console.error('[Share] error:', result.errMsg);
                return;
            }
            // Update UI
            btn.classList.replace('btn-outline-primary', 'btn-primary');
            btn.querySelector('i').classList.replace('ri-share-line', 'ri-share-fill');
            el('share-text').textContent = '已分享';
            el('share-count').textContent = result.data.totalShares;
            
            // Save to localStorage
            addSharedArticle(slug);
            copyAndNotify();
        })
        .catch(error => console.error('[Share] error:', error));
}
