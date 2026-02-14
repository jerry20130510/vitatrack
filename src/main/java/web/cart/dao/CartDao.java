package web.cart.dao;

public interface CartDao {
    void addItem(int userId, int productId, int quantity);
}
