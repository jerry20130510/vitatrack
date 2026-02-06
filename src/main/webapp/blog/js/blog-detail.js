// blog-detail.js - Article detail page logic

// Constants
const CONSTANTS = {
    HTTP_STATUS: {
        NOT_FOUND: 404
    },
    CSS_CLASSES: {
        LIKE: {
            BTN_ACTIVE: 'btn-danger',
            BTN_INACTIVE: 'btn-outline-danger',
            ICON_ACTIVE: 'ri-heart-fill',
            ICON_INACTIVE: 'ri-heart-line'
        },
        SHARE: {
            BTN_ACTIVE: 'btn-primary',
            BTN_INACTIVE: 'btn-outline-primary',
            ICON_ACTIVE: 'ri-share-fill',
            ICON_INACTIVE: 'ri-share-line'
        }
    },
    STORAGE_KEYS: {
        LIKED_ARTICLES: 'likedArticles',
        SHARED_ARTICLES: 'sharedArticles'
    },
    MESSAGES: {
        NOT_FOUND: '文章不存在',
        NOT_FOUND_DESC: '此文章可能被移除或不存在',
        LOAD_ERROR: '無法載入文章',
        LOAD_ERROR_DESC: '請檢查網路連線再試一次',
        ALREADY_LIKED: '已按讚',
        ALREADY_SHARED: '已分享',
        COPY_SUCCESS: '文章連結已複製到剪貼簿！',
        SHARE_SUCCESS: '感謝分享！文章連結已複製到剪貼簿！'
    }
};

// localStorage convenience wrappers
const getLikedArticles = () => Storage.getArray(CONSTANTS.STORAGE_KEYS.LIKED_ARTICLES);
const getSharedArticles = () => Storage.getArray(CONSTANTS.STORAGE_KEYS.SHARED_ARTICLES);
const addLikedArticle = slug => Storage.addToArray(CONSTANTS.STORAGE_KEYS.LIKED_ARTICLES, slug);
const addSharedArticle = slug => Storage.addToArray(CONSTANTS.STORAGE_KEYS.SHARED_ARTICLES, slug);

// API Helper Functions
const API = {
    getArticle: (slug) => {
        return fetch(`${API_BASE}/articles/${slug}`)
            .then(response => {
                if (!response.ok) {
                    if (response.status === CONSTANTS.HTTP_STATUS.NOT_FOUND) {
                        throw new Error('NOT_FOUND');
                    }
                    throw new Error('LOAD_ERROR');
                }
                return response.json();
            })
            .then(result => {
                if (!result.success) {
                    throw new Error(result.errMsg || 'LOAD_ERROR');
                }
                return result.data;
            });
    },
    
    incrementViews: (slug) => {
        return fetch(`${API_BASE}/articles/view?slug=${slug}`, { method: 'POST' })
            .catch(() => {}); // Silent fail
    },
    
    incrementLikes: (slug) => {
        return fetch(`${API_BASE}/articles/like?slug=${slug}`, { method: 'POST' })
            .then(response => response.json())
            .then(result => {
                if (!result.success) {
                    throw new Error(result.errMsg);
                }
                return result.data;
            });
    },
    
    incrementShares: (slug) => {
        return fetch(`${API_BASE}/articles/share?slug=${slug}`, { method: 'POST' })
            .then(response => response.json())
            .then(result => {
                if (!result.success) {
                    throw new Error(result.errMsg);
                }
                return result.data;
            });
    }
};

const showError = (msg, isNotFound = false) => {
    const container = el('article-content') || document.querySelector('.mn-main-content');
    if (!container) return;
    
    const icon = isNotFound ? 'ri-file-unknow-line' : 'ri-wifi-off-line';
    const title = isNotFound ? CONSTANTS.MESSAGES.NOT_FOUND : CONSTANTS.MESSAGES.LOAD_ERROR;
    const desc = isNotFound ? CONSTANTS.MESSAGES.NOT_FOUND_DESC : CONSTANTS.MESSAGES.LOAD_ERROR_DESC;
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
        showError(CONSTANTS.MESSAGES.NOT_FOUND, true);
        return;
    }

    toggle(el('mn-overlay'), true);
    toggle(el('article-content'), false);
    
    API.getArticle(slug)
        .then(article => {
            API.incrementViews(slug);
            
            updatePageMetadata(article);
            updateFeaturedImage(article);
            updateAuthorSection(article);
            updateArticleContent(article);
            updateSocialButtons(slug);
            
            toggle(el('mn-overlay'), false);
            toggle(el('article-content'), true);
        })
        .catch(error => {
            console.error('[Load] error:', error);
            const isNotFound = error.message === 'NOT_FOUND';
            showError(isNotFound ? CONSTANTS.MESSAGES.NOT_FOUND : CONSTANTS.MESSAGES.LOAD_ERROR, isNotFound);
        });
}

document.addEventListener('DOMContentLoaded', loadArticleDetail);

function toggleLike() {
    const slug = new URLSearchParams(location.search).get('slug');
    
    if (getLikedArticles().includes(slug)) {
        return;
    }
    
    const btn = el('like-btn');
    
    API.incrementLikes(slug)
        .then(data => {
            updateButtonState('like-btn', 'like-text', CONSTANTS.MESSAGES.ALREADY_LIKED, true);
            el('like-count').textContent = data.totalLikes;
            addLikedArticle(slug);
        })
        .catch(error => console.error('[Like] error:', error));
}

function shareArticle() {
    const slug = new URLSearchParams(location.search).get('slug');
    const alreadyShared = getSharedArticles().includes(slug);
    
    const copyAndNotify = () => {
        navigator.clipboard.writeText(location.href)
            .then(() => {
                const msg = alreadyShared ? CONSTANTS.MESSAGES.COPY_SUCCESS : CONSTANTS.MESSAGES.SHARE_SUCCESS;
                showToast(msg, { type: 'success' });
            })
            .catch(error => console.error('[Copy] error:', error));
    };
    
    if (alreadyShared) {
        copyAndNotify();
        return;
    }
    
    API.incrementShares(slug)
        .then(data => {
            updateButtonState('share-btn', 'share-text', CONSTANTS.MESSAGES.ALREADY_SHARED, false);
            el('share-count').textContent = data.totalShares;
            addSharedArticle(slug);
            copyAndNotify();
        })
        .catch(error => console.error('[Share] error:', error));
}

// ============================================================================
// DOM Update Helper Functions
// ============================================================================

function updatePageMetadata(article) {
    el('page-title').textContent = article.titleDisplay;
    el('breadcrumb-title').textContent = article.titleDisplay;
    el('breadcrumb-category').textContent = article.category;
    el('article-title').textContent = article.titleDisplay;
    el('article-date').textContent = formatDate(article.createdAt);
}

function updateFeaturedImage(article) {
    const img = el('featured-image');
    img.src = article.imageUrl;
    img.alt = article.titleDisplay;
    img.onerror = function() {
        this.onerror = null;
        this.src = PLACEHOLDER_IMAGE;
    };
}

function updateAuthorSection(article) {
    const authorName = article.authorDisplayName || '作者';
    setupAuthorAvatar(article.authorProfileImage, authorName);
    setupAuthorLink(article.authorSlug, authorName);
    setupCategoryLink(article.category);
}

function setupAuthorAvatar(profileImage, authorName) {
    const avatarImg = el('author-avatar');
    const fallback = el('author-avatar-fallback');
    
    const showFallback = () => {
        avatarImg.style.display = 'none';
        fallback.style.display = 'flex';
        fallback.textContent = authorName.charAt(0).toUpperCase();
        fallback.style.background = `hsl(${authorName.charCodeAt(0) * 5 % 360}, 50%, 45%)`;
    };
    
    if (profileImage) {
        avatarImg.crossOrigin = 'anonymous';
        avatarImg.referrerPolicy = 'no-referrer';
        avatarImg.onerror = showFallback;
        avatarImg.onload = () => {
            avatarImg.style.display = 'block';
            fallback.style.display = 'none';
        };
        avatarImg.src = profileImage;
    } else {
        showFallback();
    }
    avatarImg.alt = authorName;
}

function setupAuthorLink(authorSlug, authorName) {
    if (authorSlug) {
        el('author-name').innerHTML = `<a href="blog-list.html?authorSlug=${encodeURIComponent(authorSlug)}" class="author-link">${authorName}</a>`;
    } else {
        el('author-name').textContent = authorName;
    }
}

function setupCategoryLink(category) {
    el('category-link').textContent = category;
    el('category-link').href = `blog-list.html?category=${encodeURIComponent(category)}`;
    el('category-link').className = 'category-link';
}

function updateArticleContent(article) {
    el('article-content-text').innerHTML = article.content;
    el('like-count').textContent = article.totalLikes;
    el('share-count').textContent = article.totalShares;
}

function updateSocialButtons(slug) {
    if (getLikedArticles().includes(slug)) {
        updateButtonState('like-btn', 'like-text', CONSTANTS.MESSAGES.ALREADY_LIKED, true);
    }
    
    if (getSharedArticles().includes(slug)) {
        updateButtonState('share-btn', 'share-text', CONSTANTS.MESSAGES.ALREADY_SHARED, false);
    }
}

function updateButtonState(btnId, textId, text, isLike) {
    const btn = el(btnId);
    const classes = isLike ? CONSTANTS.CSS_CLASSES.LIKE : CONSTANTS.CSS_CLASSES.SHARE;
    
    btn.classList.replace(classes.BTN_INACTIVE, classes.BTN_ACTIVE);
    if (isLike) btn.classList.add('liked');
    btn.querySelector('i').classList.replace(classes.ICON_INACTIVE, classes.ICON_ACTIVE);
    el(textId).textContent = text;
}
