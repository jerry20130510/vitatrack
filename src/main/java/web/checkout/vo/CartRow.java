package web.checkout.vo;

import java.math.BigDecimal;

public class CartRow {

    private int cartItemId;
    private String sku;
    private String productName;
    private BigDecimal unitPrice; 
    private int quantity;

    // 建構子（對齊 CartDaoImpl 查詢）
    public CartRow(int cartItemId, String sku, String productName, BigDecimal unitPrice, int quantity) {
        this.cartItemId = cartItemId;
        this.sku = sku;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    // ===== getters =====
    public int getCartItemId() {
        return cartItemId;
    }

    public String getSku() {
        return sku;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    // ===== setters（需要時可用）=====
    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
