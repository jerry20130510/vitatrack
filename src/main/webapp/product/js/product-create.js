const API_URL = "/vitatrack/admin/product";

const form = document.getElementById("productForm");
const resultBox = document.getElementById("resultBox");
const fillDemoBtn = document.getElementById("fillDemoBtn");
const submitBtn = document.getElementById("submitBtn");

function showResult(text) {
  resultBox.textContent = text;
}

function formToPayload(formEl) {
  const fd = new FormData(formEl);

  // 注意：number 欄位要轉型，不然會是字串
  const payload = {
    sku: (fd.get("sku") || "").toString().trim(),
    categoryId: Number(fd.get("categoryId")),
    productName: (fd.get("productName") || "").toString().trim(),
    size: (fd.get("size") || "").toString().trim(),
    price: Number(fd.get("price")),
    stockQuantity: Number(fd.get("stockQuantity")),
    status: (fd.get("status") || "").toString().trim(), // enum 的話請填允許值
    shortDescription: (fd.get("shortDescription") || "").toString().trim(),
    description: (fd.get("description") || "").toString().trim()
  };

  // 如果 status 空字串就不要送（避免你後端/DB 因為 enum 值不合法出錯）
  if (!payload.status) delete payload.status;

  // size / shortDescription / description 空字串也可選擇不送
  if (!payload.size) delete payload.size;
  if (!payload.shortDescription) delete payload.shortDescription;
  if (!payload.description) delete payload.description;

  return payload;
}

fillDemoBtn.addEventListener("click", () => {
  form.elements.sku.value = "VITA-C-001";
  form.elements.categoryId.value = "1";
  form.elements.productName.value = "維他命C 1000mg（新包裝）";
  form.elements.size.value = "60顆";
  form.elements.price.value = "650";
  form.elements.stockQuantity.value = "80";
  form.elements.status.value = ""; // 
  form.elements.shortDescription.value = "新版";
  form.elements.description.value = "更新描述";
  showResult("已填入範例。提醒：categoryId 要填 DB 存在的值。");
});

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  submitBtn.disabled = true;
  showResult("送出中...");

  const payload = formToPayload(form);

  try {
    const res = await fetch(API_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    const text = await res.text();

    showResult(
      `HTTP ${res.status}\n\n` +
      `Request:\n${JSON.stringify(payload, null, 2)}\n\n` +
      `Response:\n${text}`
    );

  } catch (err) {
    showResult("發生錯誤：\n" + (err?.message || err));
  } finally {
    submitBtn.disabled = false;
  }
});
