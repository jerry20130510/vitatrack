(function () {
  function qs(id) {
    return document.getElementById(id);
  }

  function getParam(key) {
    var params = new URLSearchParams(window.location.search);
    return params.get(key);
  }

  function pick(obj, keys) {
    for (var i = 0; i < keys.length; i++) {
      var k = keys[i];
      if (obj && obj[k] !== undefined && obj[k] !== null) return obj[k];
    }
    return null;
  }

  function escapeHtml(str) {
    return String(str)
      .replaceAll('&', '&amp;')
      .replaceAll('<', '&lt;')
      .replaceAll('>', '&gt;')
      .replaceAll('"', '&quot;')
      .replaceAll("'", '&#39;');
  }

  function formatPrice(value) {
    var n = Number(value);
    if (Number.isNaN(n)) return '$0';
    return '$' + n.toLocaleString('en-US');
  }

  function setText(el, text) {
    if (!el) return;
    el.textContent = (text === null || text === undefined) ? '' : String(text);
  }

  function setHtml(el, html) {
    if (!el) return;
    el.innerHTML = html;
  }

  function normalizeStatus(raw) {
    if (raw === null || raw === undefined) return '';
    var s = String(raw).toUpperCase();
    if (s === '1') return 'ON_SALE';
    if (s === '0') return 'OFF_SALE';
    return s;
  }

  function stockLabel(stockQty, status) {
    var qty = Number(stockQty);
    var st = normalizeStatus(status);
    if (st && st !== 'ON_SALE') return '未上架';
    if (Number.isFinite(qty) && qty <= 0) return '缺貨';
    return '有庫存';
  }

  function mapProduct(p) {
    return {
      sku: pick(p, ['sku', 'SKU']),
      name: pick(p, ['productName', 'product_name', 'name']),
      price: pick(p, ['price', 'unitPrice', 'unit_price']),
      size: pick(p, ['size']),
      stock: pick(p, ['stockQuantity', 'stock_quantity', 'stock']),
      status: pick(p, ['status']),
      shortDesc: pick(p, ['shortDescription', 'short_description', 'brief', 'summary']),
      desc: pick(p, ['description', 'desc', 'detail', 'content'])
    };
  }

  async function load() {
    var skuParam = getParam('sku');

    console.log('目前網址 sku =', skuParam);

    var elName = qs('pName');
    var elPrice = qs('pPrice');
    var elSku = qs('pSku');
    var elStock = qs('pStockText');
    var elShort = qs('pShortDesc');
    var elDesc = qs('pDesc');

    // 沒有 SKU → 直接中止
    if (!skuParam) {
      setText(elName, '查無商品');
      setText(elStock, '缺少 sku');
      setHtml(elDesc, '<p>請使用 ?sku=商品編號</p>');
      return;
    }

    try {
      var url = 'product-detail?sku=' + encodeURIComponent(skuParam);
      console.log('呼叫 API =', url);

      var resp = await fetch(url, {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
      });

      console.log('HTTP 狀態 =', resp.status);

      if (!resp.ok) throw new Error('HTTP ' + resp.status);

      var raw = await resp.json();
      console.log('原始資料 raw =', raw);

      // 支援兩種後端格式
      var data = raw.data ? raw.data : raw;

      var p = mapProduct(data);
      console.log('轉換後 product =', p);

      if (!p || !p.sku) {
        throw new Error('資料格式錯誤或查無商品');
      }

      // === 填入畫面 ===
      setText(elName, p.name || '未命名商品');
      setText(elSku, p.sku || '');
      setText(elPrice, formatPrice(p.price));
      setText(elStock, stockLabel(p.stock, p.status));
      setText(elShort, p.shortDesc || '');

      var safe = escapeHtml(p.desc || '').replaceAll('\n', '<br>');
      setHtml(elDesc, '<p>' + safe + '</p>');

      document.title = (p.name ? p.name + ' | VitaTrack' : 'VitaTrack');

    } catch (e) {
      console.error('載入失敗 =', e);
      setText(elName, '載入失敗');
      setText(elStock, '請確認 API /product-detail');
      setHtml(elDesc, '<p>無法取得商品資料</p>');
    }
  }

  document.addEventListener('DOMContentLoaded', load);
})();