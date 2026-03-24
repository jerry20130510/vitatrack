(function () {
  var CART_KEY = 'cart';

  function getCart() {
    try {
      var raw = localStorage.getItem(CART_KEY);
      var cart = raw ? JSON.parse(raw) : [];
      var changed = false;

      cart = cart.map(function (item) {
        if (item.qty != null) {
          changed = true;
        }

        return {
          sku: item.sku,
          name: item.name || item.productName || '',
          price: Number(item.price || 0),
          quantity: Number(item.quantity != null ? item.quantity : item.qty || 1),
          image: item.image || ''
        };
      });

      if (changed) {
        localStorage.setItem(CART_KEY, JSON.stringify(cart));
      }

      return cart;
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
      total += Number(cart[i].quantity || 0);
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

    var quantityToAdd = Number(product.quantity || 1);
    if (!Number.isFinite(quantityToAdd) || quantityToAdd <= 0) {
      quantityToAdd = 1;
    }

    if (found) {
      found.quantity = Number(found.quantity || 0) + quantityToAdd;
    } else {
      cart.push({
        sku: product.sku,
        name: product.name || '',
        price: Number(product.price || 0),
        quantity: quantityToAdd,
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