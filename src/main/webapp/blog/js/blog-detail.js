// blog-detail.js - Article detail page logic

function loadArticleDetail() {
    const slug = new URLSearchParams(location.search).get('slug');
    
    if (!slug) {
        showNotFoundError('article-content', '此文章可能被移除或不存在', '返回文章列表', 'blog-list.html');
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
            if (!result.success) {
                throw new Error(result.errMsg);
            }
            
            const a = result.data;
            
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
            
            const authorName = a.authorDisplayName;
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
                showNotFoundError('article-content', '此文章可能被移除或不存在', '返回文章列表', 'blog-list.html');
            } else {
                showLoadError('article-content');
            }
        });
}

function toggleLike() {
    const slug = new URLSearchParams(location.search).get('slug');
    
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
                throw new Error(result.errMsg);
            }
            btn.classList.replace('btn-outline-danger', 'btn-danger');
            btn.classList.add('liked');
            btn.querySelector('i').classList.replace('ri-heart-line', 'ri-heart-fill');
            el('like-text').textContent = '已按讚';
            el('like-count').textContent = result.data.totalLikes;
            
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
                throw new Error(result.errMsg);
            }
            btn.classList.replace('btn-outline-primary', 'btn-primary');
            btn.querySelector('i').classList.replace('ri-share-line', 'ri-share-fill');
            el('share-text').textContent = '已分享';
            el('share-count').textContent = result.data.totalShares;
            
            addSharedArticle(slug);
            copyAndNotify();
        })
        .catch(error => console.error('[Share] error:', error));
}

const getLikedArticles = () => {
    try {
        return JSON.parse(localStorage.getItem('likedArticles') || '[]');
    } catch (e) {
        console.error('[Storage] error:', e);
        return [];
    }
};

const getSharedArticles = () => {
    try {
        return JSON.parse(localStorage.getItem('sharedArticles') || '[]');
    } catch (e) {
        console.error('[Storage] error:', e);
        return [];
    }
};

const addLikedArticle = slug => {
    try {
        localStorage.setItem('likedArticles', JSON.stringify([...getLikedArticles(), slug]));
    } catch (e) {
        console.error('[Storage] error:', e);
    }
};

const addSharedArticle = slug => {
    try {
        localStorage.setItem('sharedArticles', JSON.stringify([...getSharedArticles(), slug]));
    } catch (e) {
        console.error('[Storage] error:', e);
    }
};

document.addEventListener('DOMContentLoaded', loadArticleDetail);
