let currentProductId;
let modalInstance;

function openAddToCart(productId) {
  currentProductId = productId;

  fetch("addToCart.jsp")
    .then(res => res.text())
    .then(html => {
      document.getElementById("modalContainer").innerHTML = html;

      // 顯示 Bootstrap modal
      const modalEl = document.getElementById("addToCartModal");
      modalInstance = new bootstrap.Modal(modalEl);
      modalInstance.show();

	  console.log("contextPath =", contextPath);
	  
      // 查庫存
      fetch(`${contextPath}/api/product/stock?productId=${productId}`)
	  .then(res => {
	     console.log("stock API response status:", res.status);
	     return res.json();
	   })
	   .then(data => {
	     console.log("stock API response data:", data);
	     document.getElementById("stockQty").innerText = data.stock;
	   })
	   .catch(err => {
	     console.error("fetch stock error", err);
	   });
    });
}

function changeQty(diff) {
  const input = document.getElementById("qtyInput");
  let val = parseInt(input.value) + diff;
  if (val < 1) val = 1;
  input.value = val;
}

function confirmAddToCart() {
  const qty = document.getElementById("qtyInput").value;

  fetch(`${contextPath}/cart/add`, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: `productId=${currentProductId}&quantity=${qty}`
  }).then(res => {
    if (res.ok) {
      alert("已加入購物車");
      modalInstance.hide();
    } else {
      alert("加入失敗");
    }
  });
}
