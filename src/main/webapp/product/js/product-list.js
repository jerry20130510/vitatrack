document.addEventListener("DOMContentLoaded", function () {

    fetch("/vitatrack/product-list")
        .then(function (response) {
            return response.json();
        })
        .then(function (data) {

            var tbody = document.getElementById("productTbody");
            tbody.innerHTML = "";

            for (var i = 0; i < data.length; i++) {

                var product = data[i];

                var tr = document.createElement("tr");

                // SKU
                var tdSku = document.createElement("td");
                tdSku.textContent = product.sku;

                // 商品名稱
                var tdName = document.createElement("td");
                tdName.textContent = product.productName;

                // 價格
                var tdPrice = document.createElement("td");
                tdPrice.textContent = product.price;

                // 規格 / 尺寸
                var tdSize = document.createElement("td");
                tdSize.textContent = product.size;

                // 庫存
                var tdStock = document.createElement("td");
                tdStock.textContent = product.stockQuantity;

                // 商品詳細內容
                var tdDescription = document.createElement("td");
                tdDescription.textContent = product.description;

                // 商品簡介
                var tdShort = document.createElement("td");
                tdShort.textContent = product.shortDescription;

                // 狀態
                var tdStatus = document.createElement("td");
                tdStatus.textContent = product.status;

                // 建立者
                var tdCreatedBy = document.createElement("td");
                tdCreatedBy.textContent = product.createdByAdminId;

                // 最後更新者
                var tdUpdatedBy = document.createElement("td");
                tdUpdatedBy.textContent = product.updatedByAdminId;

                // 加入 row
                tr.appendChild(tdSku);
                tr.appendChild(tdName);
                tr.appendChild(tdPrice);
                tr.appendChild(tdSize);
                tr.appendChild(tdStock);
                tr.appendChild(tdDescription);
                tr.appendChild(tdShort);
                tr.appendChild(tdStatus);
                tr.appendChild(tdCreatedBy);
                tr.appendChild(tdUpdatedBy);

                tbody.appendChild(tr);
            }

        })
        .catch(function (error) {
            console.error("Error:", error);
            alert("載入商品清單失敗");
        });

});
