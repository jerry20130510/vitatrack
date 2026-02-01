package web.checkout;

import web.checkout.vo.CartRow;

public class TestCartRow {
    public static void main(String[] args) {
        CartRow row = new CartRow(1, "SKU-IPHONE15", "iPhone 15", 29900, 1);

        if (!"SKU-IPHONE15".equals(row.getSku())) throw new RuntimeException("sku mismatch");
        if (row.getUnitPrice() != 29900) throw new RuntimeException("price mismatch");
        if (row.getQuantity() != 1) throw new RuntimeException("qty mismatch");

        long lineTotal = (long) row.getUnitPrice() * row.getQuantity();
        System.out.println("✅ CartRow OK, lineTotal=" + lineTotal);
    }
}
