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
      desc: pick(p, ['description', 'desc', 'detail', 'content']),
      image: pick(p, ['imageUrl', 'image_url', 'image', 'img']),
      category: pick(p, ['categoryDesc', 'category_desc', 'categoryName', 'category_name'])
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

      var data = raw.data ? raw.data : raw;

      var p = mapProduct(data);
      console.log('轉換後 product =', p);

      if (!p || !p.sku) {
        throw new Error('資料格式錯誤或查無商品');
      }

      setText(elName, p.name || '未命名商品');
      setText(elSku, p.sku || '');
      setText(elPrice, formatPrice(p.price));
      setText(elStock, stockLabel(p.stock, p.status));
      setText(elShort, p.shortDesc || '');

      var safe = escapeHtml(p.desc || '').replaceAll('\n', '<br>');
      setHtml(elDesc, '<p>' + safe + '</p>');

      document.title = (p.name ? p.name + ' | VitaTrack' : 'VitaTrack');
	  bindAddToCart(p);
	  
    } catch (e) {
      console.error('載入失敗 =', e);
      setText(elName, '載入失敗');
      setText(elStock, '請確認 API /product-detail');
      setHtml(elDesc, '<p>無法取得商品資料</p>');
    }
  }

  function buildRelatedCard(p) {
	var img = p.image || 'assets/img/product/5.jpg';
	  var name = escapeHtml(p.name || '商品名稱');
	  var price = formatPrice(p.price);
	  var sku = encodeURIComponent(p.sku || '');

	  return `
	    <div class="mn-product-card">
	      <div class="mn-product-img">
	        <div class="mn-img">
	          <a href="product-detail.html?sku=${sku}" class="image">
	            <img class="main-img" src="${img}" alt="${name}">
	          </a>
	        </div>
	      </div>
	      <div class="mn-product-detail">
	        <h5>
	          <a href="product-detail.html?sku=${sku}">${name}</a>
	        </h5>
	        <div class="mn-price">
	          <div class="mn-price-new">${price}</div>
	        </div>
	      </div>
	    </div>
	  `;
  }

  function renderRelatedProducts(list) {
    var container = qs('relatedProductsContainer');
    if (!container) return;

    if (!Array.isArray(list) || list.length === 0) {
      container.innerHTML = '<p>目前沒有相關商品</p>';
      return;
    }

    var html = '';
    for (var i = 0; i < list.length; i++) {
      var p = mapProduct(list[i]);
      html += buildRelatedCard(p);
    }

    container.innerHTML = html;

    if (window.jQuery && jQuery.fn.owlCarousel) {
      var $container = jQuery(container);

      if ($container.hasClass('owl-loaded')) {
        $container.trigger('destroy.owl.carousel');
        $container.removeClass('owl-loaded');
        $container.find('.owl-stage-outer').children().unwrap();
      }

      $container.owlCarousel({
        loop: false,
        margin: 20,
        nav: true,
        dots: false,
        responsive: {
          0: { items: 1 },
          768: { items: 2 },
          992: { items: Number(container.dataset.count || 3) }
        }
      });
    }
  }

  async function loadRelatedProducts() {
	var skuParam = getParam('sku');
	 var container = qs('relatedProductsContainer');

	  if (!skuParam || !container) return;

	  var fixedSkus = [
	    '202603011284', // 維他命C 1000mg
	    '202603012843', // 維生素B群
	    '202603014519', // Omega-3 魚油
	    '202603016140'  // 益生菌 50億
	  ];

	  // 排除目前商品，剩下三個
	  var relatedSkus = fixedSkus.filter(function (sku) {
	    return sku !== skuParam;
	  });

	  if (relatedSkus.length === 0) {
	    container.innerHTML = '<p>目前沒有相關商品</p>';
	    return;
	  }

	  try {
	    var url = 'product-related?skus=' + encodeURIComponent(relatedSkus.join(','));
	    console.log('呼叫相關商品 API =', url);

	    var resp = await fetch(url, {
	      method: 'GET',
	      headers: { 'Accept': 'application/json' }
	    });

	    if (!resp.ok) throw new Error('HTTP ' + resp.status);

	    var raw = await resp.json();
	    console.log('相關商品 raw =', raw);

	    var list = Array.isArray(raw) ? raw : (raw.data || []);
	    renderRelatedProducts(list);

	  } catch (e) {
	    console.error('相關商品載入失敗 =', e);
	    container.innerHTML = '<p>無法取得相關商品</p>';
	  }
  }

  document.addEventListener('DOMContentLoaded', async function () {
    await load();
    await loadRelatedProducts();
  });
  
  function bindAddToCart(product) {
    var btn = document.querySelector('.mn-add-cart');
    var qtyInput = document.querySelector('.qty-input');

    if (!btn || !window.CartStore) return;

	btn.onclick = function (e) {
	  e.preventDefault();
	  e.stopImmediatePropagation();

	  var qty = 1;

	  if (qtyInput) {
	    qty = Number(qtyInput.value || 1);
	    if (!Number.isFinite(qty) || qty <= 0) {
	      qty = 1;
	    }
	  }

	  // === 強制寫入 localStorage（保險） ===
	  var cart = JSON.parse(localStorage.getItem("cart")) || [];

	  var existing = cart.find(function(item) {
	    return item.sku === product.sku;
	  });

	  if (existing) {
	    existing.qty += qty;
	  } else {
	    cart.push({
	      sku: product.sku,
	      name: product.name,
	      price: product.price,
	      qty: qty,
	      image: product.image || ''
	    });
	  }

	  localStorage.setItem("cart", JSON.stringify(cart));

	  // 更新 badge（如果有）
	  if (window.CartStore && typeof window.CartStore.updateCartBadge === "function") {
	    window.CartStore.updateCartBadge();
	  }
	};
  }
  
})();