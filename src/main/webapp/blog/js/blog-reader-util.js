// blog-reader-util.js - Shared utilities for public blog pages

// Constants
const CONSTANTS = {
    LOCALE: 'zh-TW',
    DATE_FORMAT: {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    },
    PLACEHOLDER_IMAGE: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTIwMCIgaGVpZ2h0PSI4MDAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdGg9IjEyMDAiIGhlaWdodD0iODAwIiBmaWxsPSIjZjVmNWY1Ii8+PHRleHQgeD0iNTAlIiB5PSI0NSUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSI0OCIgZmlsbD0iIzk5OTk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSI+PGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSI2NCI+8J+TtzwvZm9udC1mYW1pbHk+PC90ZXh0Pjx0ZXh0IHg9IjUwJSIgeT0iNTUlIiBmb250LWZhbWlseT0iQXJpYWwsIHNhbnMtc2VyaWYiIGZvbnQtc2l6ZT0iMjQiIGZpbGw9IiNiYmJiYmIiIHRleHQtYW5jaG9yPSJtaWRkbGUiPuWclueJh+i8ieWFpeWksei0pTwvdGV4dD48L3N2Zz4=',
    HTML_ENTITIES: {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    },
    TOAST: {
        DURATION: 3000,
        ICON_SIZE: '20px',
        BOTTOM_MARGIN: '30px'
    }
};

const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/', 1)) || '';
const API_BASE = `${window.location.origin}${contextPath}/api`;

// DOM helpers
const el = id => document.getElementById(id);
const toggle = (elem, show) => elem && (elem.style.display = show ? 'block' : 'none');

// Generic Storage Helper
const Storage = {
    getArray: (key) => {
        try {
            return JSON.parse(localStorage.getItem(key) || '[]');
        } catch (e) {
            console.error('[Storage] error:', e);
            return [];
        }
    },
    
    addToArray: (key, item) => {
        try {
            const arr = Storage.getArray(key);
            if (!arr.includes(item)) {
                arr.push(item);
                localStorage.setItem(key, JSON.stringify(arr));
            }
        } catch (e) {
            console.error('[Storage] error:', e);
        }
    },
    
    hasInArray: (key, item) => {
        return Storage.getArray(key).includes(item);
    }
};

// Date formatting
const formatDate = date => new Date(date).toLocaleDateString(CONSTANTS.LOCALE, CONSTANTS.DATE_FORMAT);

// Security
const escapeHtml = str => (str || '').replace(/[&<>"']/g, m => CONSTANTS.HTML_ENTITIES[m]);

// Unified toast
const showToast = (message, options = {}) => {
    const {
        type = 'info',        // 'success', 'error', 'info'
        duration = CONSTANTS.TOAST.DURATION,
        position = 'bottom'   // 'bottom' or 'top'
    } = options;
    
    // Color mapping
    const colors = {
        success: { bg: 'var(--primary)', color: 'var(--title-color)' },
        error: { bg: '#f90c4c', color: '#fff' },
        info: { bg: 'var(--primary)', color: 'var(--title-color)' }
    };
    
    // Icon mapping
    const icons = {
        success: `<i class="ri-check-line" style="font-size: ${CONSTANTS.TOAST.ICON_SIZE};"></i>`,
        error: `<i class="ri-error-warning-line" style="font-size: ${CONSTANTS.TOAST.ICON_SIZE};"></i>`,
        info: ''
    };
    
    const style = colors[type];
    const icon = icons[type];
    
    const toastHtml = `
        <div class="toast align-items-center border-0" role="alert" 
             style="background-color: ${style.bg}; color: ${style.color};">
            <div class="d-flex align-items-center">
                ${icon ? `<div class="ps-3">${icon}</div>` : ''}
                <div class="toast-body">${message}</div>
            </div>
        </div>`;
    
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        const positionClass = position === 'top' 
            ? 'top-0 end-0 p-3' 
            : 'bottom-0 start-50 translate-middle-x';
        container.className = `toast-container position-fixed ${positionClass}`;
        if (position === 'bottom') {
            container.style.marginBottom = CONSTANTS.TOAST.BOTTOM_MARGIN;
        }
        document.body.appendChild(container);
    }
    
    container.insertAdjacentHTML('beforeend', toastHtml);
    const toastEl = container.lastElementChild;
    const toast = new bootstrap.Toast(toastEl, { delay: duration });
    toast.show();
    toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
};

