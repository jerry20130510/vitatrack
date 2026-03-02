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

    var elName = qs('pName');
    var elPrice = qs('pPrice');
    var elSku = qs('pSku');
    var elStock = qs('pStockText');
    var elShort = qs('pShortDesc');
    var elDesc = qs('pDesc');

    try {
      var resp = await fetch('product-list', {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
      });

      if (!resp.ok) throw new Error('HTTP ' + resp.status);

      var list = await resp.json();

      if (!Array.isArray(list) || list.length === 0) {
        setText(elName, '查無商品資料');
        setText(elStock, '無資料');
        setHtml(elDesc, '<p>後端未回傳商品清單。</p>');
        return;
      }

      var raw = null;

      if (skuParam) {
        for (var i = 0; i < list.length; i++) {
          var itemSku = pick(list[i], ['sku', 'SKU']);
          if (String(itemSku) === String(skuParam)) {
            raw = list[i];
            break;
          }
        }
      }

      if (!raw) raw = list[0];

      var p = mapProduct(raw);

      setText(elName, p.name || '未命名商品');
      setText(elSku, p.sku || '');
      setText(elPrice, formatPrice(p.price));
      setText(elStock, stockLabel(p.stock, p.status));
      setText(elShort, p.shortDesc || '');

      var safe = escapeHtml(p.desc || '').replaceAll('\n', '<br>');
      setHtml(elDesc, '<p>' + (safe || '') + '</p>');

      document.title = (p.name ? p.name + ' | VitaTrack' : 'VitaTrack');
    } catch (e) {
      console.error(e);
      setText(elName, '載入失敗');
      setText(elStock, '請確認 /product-list 是否可用');
      setHtml(elDesc, '<p>無法取得商品資料。</p>');
    }
  }

  document.addEventListener('DOMContentLoaded', load);
})();