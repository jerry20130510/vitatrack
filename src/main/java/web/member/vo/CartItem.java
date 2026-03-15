//package web.member.vo;
//
//import java.sql.Timestamp;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.Table;
//
//
//import org.hibernate.annotations.DynamicUpdate;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//
//
//@Entity
//@Table(name = "cart_item")
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@DynamicUpdate
//public class CartItem {
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "cart_item_id")
//	private Integer cartItemId;
//	@Column(name = "member_id")
//	private Integer memberId;
//	@Column(name = "sku")
//	private String sku;
//	@Column(name = "order_id")
//	private Integer orderId;
//	@Column(name = "quantity")
//	private Integer quantity;
//	@Column(name = "created_date")
//	private Timestamp createDate;
//	@Column(name = "updated_date")
//	private Timestamp updatedDate;
//}
