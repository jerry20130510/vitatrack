window.addEventListener("load", function() {
    const btn = document.getElementById("submitBtn");
    btn.addEventListener("click", addproduct);
});

function addproduct() {
    const productName = document.getElementById("productName").value;
    const price = document.getElementById("price").value;
    const size = document.getElementById("size").value;
    const stockQuantity = document.getElementById("stockQuantity").value;
    const description = document.getElementById("description").value;
    const shortDescription = document.getElementById("shortDescription").value;
    const status = document.getElementById("status").value;

    const productAdd = {
        productName: productName,
        price: price,
        size: size,
        stockQuantity: stockQuantity,
        description: description,
        shortDescription: shortDescription,
        status: status
    };

    fetch("/vitatrack/product-add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(productAdd)
    })

    .then(response => response.json())
	.then(data => {
	  if (data === true) {
	    alert("商品新增成功！");
	    document.getElementById("productForm").reset();
	  } else {
	    alert("商品新增失敗");
	  }
	})
}