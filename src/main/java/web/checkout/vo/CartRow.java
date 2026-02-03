package web.checkout.vo;

public class CartRow {

	private int cartItemId;
	private String sku;
	private String productName;
	private int unitPrice;
	private int quantity;

	public CartRow(int cartItemId, String sku, String productName, int unitPrice, int quantity) {
		this.cartItemId = cartItemId;
		this.sku = sku;
		this.productName = productName;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
	}

	public int getCartItemId() {
		return cartItemId;
	}

	public String getSku() {
		return sku;
	}

	public String getProductName() {
		return productName;
	}

	public int getUnitPrice() {
		return unitPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	@Override
	public String toString() {
		return "CartRow{" + "cartItemId=" + cartItemId + ", sku='" + sku + '\'' + ", productName='" + productName + '\''
				+ ", unitPrice=" + unitPrice + ", quantity=" + quantity + '}';
	}

}
