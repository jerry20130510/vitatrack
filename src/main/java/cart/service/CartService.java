package cart.service;

public interface CartService {
    void addItem(int userId, int productId, int quantity);
}
