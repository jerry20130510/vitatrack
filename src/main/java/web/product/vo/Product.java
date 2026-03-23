package web.product.vo;

import javax.persistence.Column;
import javax.persistence.Entity;

import java.math.BigDecimal;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "product")
public class Product {
	@Id
	private String sku;
	@Column (name = "category_id")
	private Integer categoryId;
	@Column (name = "product_name")
	private String productName;
	@Column (name = "size")
	private String size;
	@Column (name = "price")
	private BigDecimal price;
	@Column (name = "stock_quantity")
	private Integer stockQuantity;
	@Column (name = "status")
	private String status;
	@Column (name = "short_description")
	private String shortDescription;
	@Column (name = "description")
	private String description;
	@Column (name = "created_by_admin_id")
	private Long createdByAdminId;
	@Column (name = "updated_by_admin_id")
	private Long updatedByAdminId;

	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
	}
	public Integer getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Integer categoryId) {
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
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Integer getStockQuantity() {
		return stockQuantity;
	}
	public void setStockQuantity(Integer stockQuantity) {
		this.stockQuantity = stockQuantity;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
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
	public Long getCreatedByAdminId() {
		return createdByAdminId;
	}
	public void setCreatedByAdminId(Long createdByAdminId) {
		this.createdByAdminId = createdByAdminId;
	}
	public Long getUpdatedByAdminId() {
		return updatedByAdminId;
	}
	public void setUpdatedByAdminId(Long updatedByAdminId) {
		this.updatedByAdminId = updatedByAdminId;
	}

}
