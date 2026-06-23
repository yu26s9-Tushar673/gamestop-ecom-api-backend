package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.CartItem;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.repository.ShoppingCartRepository;

import java.util.List;

@Service
public class ShoppingCartService
{
    // a shopping cart is built from cart rows plus a product lookup for each row
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductService productService;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, ProductService productService)
    {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productService = productService;
    }

    public ShoppingCart getByUserId(int userId)
    {
        // load the user's cart rows, look up each product, and build the ShoppingCart
        ShoppingCart cart = new ShoppingCart();
        List<CartItem> cartItems = shoppingCartRepository.findByUserId(userId);

        for (CartItem cartItem : cartItems) {
            Product product = productService.getById(cartItem.getProductId());
            ShoppingCartItem item = new ShoppingCartItem();
            item.setProduct(product);
            item.setQuantity(cartItem.getQuantity());
            cart.add(item);
        }
        return cart;
    }
    // add additional methods here

    public ShoppingCart addProduct(int userId, int productId) {
        CartItem existing = shoppingCartRepository.findByUserIdAndProductId(userId, productId);

        if (existing == null) {
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProductId(productId);
            newItem.setQuantity(1);
            shoppingCartRepository.save(newItem);
        } else {
            existing.setQuantity(existing.getQuantity() + 1);
            shoppingCartRepository.save(existing);
        }
        return getByUserId(userId);
    }
}
