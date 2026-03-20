(function () {
  var CART_KEY = 'cart';

  function getCart() {
    try {
      var raw = localStorage.getItem(CART_KEY);
      return raw ? JSON.parse(raw) : [];
    } catch (e) {
      console.error('讀取購物車失敗', e);
      return [];
    }
  }

  function saveCart(cart) {
    localStorage.setItem(CART_KEY, JSON.stringify(cart));
  }

  function getCartTypeCount() {
	var cart = getCart();
	var total = 0;

	for (var i = 0; i < cart.length; i++) {
	  total += Number(cart[i].qty || 0);
	}

	return total;
  }

  function updateCartBadge() {
    var badge = document.getElementById('cartBadge');
    if (!badge) {
      console.warn('找不到 #cartBadge');
      return;
    }

    var count = getCartTypeCount();
    badge.textContent = count;
    badge.style.display = count > 0 ? 'inline-flex' : 'none';
  }

  function addToCart(product) {
    if (!product || !product.sku) return;

    var cart = getCart();
    var found = null;

    for (var i = 0; i < cart.length; i++) {
      if (cart[i].sku === product.sku) {
        found = cart[i];
        break;
      }
    }

    var qtyToAdd = Number(product.qty || 1);
    if (!Number.isFinite(qtyToAdd) || qtyToAdd <= 0) {
      qtyToAdd = 1;
    }

    if (found) {
      found.qty += qtyToAdd;
    } else {
      cart.push({
        sku: product.sku,
        name: product.name || '',
        price: Number(product.price || 0),
        qty: qtyToAdd,
        image: product.image || ''
      });
    }

    saveCart(cart);
    updateCartBadge();
    console.log('購物車內容', cart);
  }

  window.CartStore = {
    getCart: getCart,
    saveCart: saveCart,
    addToCart: addToCart,
    updateCartBadge: updateCartBadge
  };

  document.addEventListener('DOMContentLoaded', function () {
    updateCartBadge();
  });
  
})();