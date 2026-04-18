function getCart() {
  var cart = JSON.parse(localStorage.getItem("cart")) || [];
  var changed = false;

  cart = cart.map(function(item) {
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
    localStorage.setItem("cart", JSON.stringify(cart));
  }

  return cart;
}

function saveCart(cart) {
  localStorage.setItem("cart", JSON.stringify(cart));
}

function formatPrice(value) {
  return "NT$" + Number(value || 0).toLocaleString("zh-TW");
}

function removeItem(sku) {
  var cart = getCart().filter(function(item) {
    return item.sku !== sku;
  });

  saveCart(cart);
  renderCartPage();

  if (window.CartStore && typeof window.CartStore.updateCartBadge === "function") {
    window.CartStore.updateCartBadge();
  }
}

function renderCartPage() {
  var cart = getCart();
  var container = document.getElementById("cartList");
  var totalEl = document.getElementById("cartTotal");

  if (!container) return;

  if (cart.length === 0) {
    container.innerHTML = `
      <tr>
        <td colspan="5" style="text-align:center; padding:30px;">購物車是空的</td>
      </tr>
    `;
    if (totalEl) totalEl.textContent = "NT$0";
    return;
  }

  var html = "";
  var total = 0;

  for (var i = 0; i < cart.length; i++) {
    var item = cart[i];
    var quantity = Number(item.quantity || 1);
    var price = Number(item.price || 0);
    var subtotal = quantity * price;
    total += subtotal;

    html += `
      <tr class="mn-cart-product">
        <td data-label="Product" class="mn-cart-pro-name">
          <a href="product-detail.html?sku=${item.sku || ''}">
            <img class="mn-cart-pro-img" src="${item.image || 'assets/img/product/default.jpg'}" alt="">
            ${item.name || item.productName || ''}
          </a>
        </td>
        <td data-label="Price" class="mn-cart-pro-price">
          <span class="amount">${formatPrice(price)}</span>
        </td>
        <td data-label="Quantity" class="mn-cart-pro-qty" style="text-align:center;">
          <div class="cart-qty-box">
            <button type="button" onclick="changeQty('${item.sku}', -1)">-</button>
            <input type="text" value="${quantity}" readonly style="width:40px; text-align:center;">
            <button type="button" onclick="changeQty('${item.sku}', 1)">+</button>
          </div>
        </td>
        <td data-label="Total" class="mn-cart-pro-subtotal">
          ${formatPrice(subtotal)}
        </td>
        <td data-label="Remove" class="mn-cart-pro-remove">
          <a href="javascript:void(0)" onclick="removeItem('${item.sku}')">
            <i class="ri-delete-bin-line"></i>
          </a>
        </td>
      </tr>
    `;
  }

  container.innerHTML = html;

  if (totalEl) {
    totalEl.textContent = formatPrice(total);
  }
}

document.addEventListener("DOMContentLoaded", function () {
  renderCartPage();
});

function changeQty(sku, delta) {
  var cart = getCart();

  for (var i = 0; i < cart.length; i++) {
    if (cart[i].sku === sku) {
      cart[i].quantity = Number(cart[i].quantity || 1) + delta;

      if (cart[i].quantity <= 0) {
        cart.splice(i, 1);
      }
      break;
    }
  }

  saveCart(cart);
  renderCartPage();

  if (window.CartStore && typeof window.CartStore.updateCartBadge === "function") {
    window.CartStore.updateCartBadge();
  }
}

function checkoutNow() {
  var cart = getCart();

  if (!cart.length) {
    alert("購物車是空的");
    return;
  }

  var payloadItems = cart.map(function(item) {
    return {
      sku: item.sku,
      productName: item.productName || item.name || "",
      price: Number(item.price || 0),
      quantity: Number(item.quantity || item.qty || 1)
    };
  });

  fetch("/vitatrack/cart-item/checkout", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({
      items: payloadItems
    })
  })
    .then(function(response) {
      return response.json();
    })
    .then(function(result) {
      if (result.success) {
        localStorage.removeItem("cart");

        if (window.CartStore && typeof window.CartStore.updateCartBadge === "function") {
          window.CartStore.updateCartBadge();
        }

        window.location.href = "/vitatrack/checkout.html";
      } else {
        alert(result.message || "結帳失敗");
      }
    })
    .catch(function(error) {
      console.error(error);
      alert("系統錯誤，請稍後再試");
    });
}