package web.product.vo;

public class Product {
	
	private String sku;
	private int categoryId;
	private String productName;
	private String size;
	private int price;
	private int stockQuantity;
	private int status;
	private String shortDescription;
	private String description;
	private long CreatedByAdminId;
	public Product(String sku, int categoryId, String productName, String size, int price, int stockQuantity,
			int status, String shortDescription, String description, long createdByAdminId) {
		super();
		this.sku = sku;
		this.categoryId = categoryId;
		this.productName = productName;
		this.size = size;
		this.price = price;
		this.stockQuantity = stockQuantity;
		this.status = status;
		this.shortDescription = shortDescription;
		this.description = description;
		CreatedByAdminId = createdByAdminId;
	}
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public int getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getStockQuantity() {
		return stockQuantity;
	}
	public void setStockQuantity(int stockQuantity) {
		this.stockQuantity = stockQuantity;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getCreatedByAdminId() {
		return CreatedByAdminId;
	}
	public void setCreatedByAdminId(long createdByAdminId) {
		CreatedByAdminId = createdByAdminId;
	}

}
