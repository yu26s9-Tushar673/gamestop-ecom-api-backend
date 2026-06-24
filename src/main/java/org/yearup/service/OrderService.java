package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.*;
import org.yearup.repository.OrderLineItemRepository;
import org.yearup.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderLineItemRepository orderLineItemRepository;
    private final ShoppingCartService shoppingCartService;
    private final ProfileService profileService;

    public OrderService(OrderRepository orderRepository,
                        OrderLineItemRepository orderLineItemRepository,
                        ShoppingCartService shoppingCartService,
                        ProfileService profileService) {
        this.orderRepository = orderRepository;
        this.orderLineItemRepository = orderLineItemRepository;
        this.shoppingCartService = shoppingCartService;
        this.profileService = profileService;
    }

    public Order checkout(int userId) {
        ShoppingCart cart = shoppingCartService.getByUserId(userId);
        Profile profile = profileService.getByUserId(userId);

        if (cart.getItems().isEmpty()){
            throw new IllegalArgumentException("Cannot checkout an empty cart. Add item(s) to cart to checkout.");
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setDate(LocalDateTime.now());
        order.setAddress(profile.getAddress());
        order.setCity(profile.getCity());
        order.setState(profile.getState());
        order.setZip(profile.getZip());
        order.setShippingAmount(0.0);
        order = orderRepository.save(order);

        for (Map.Entry<Integer, ShoppingCartItem> entry : cart.getItems().entrySet()) {
            ShoppingCartItem cartItem = entry.getValue();

            OrderLineItem lineItem = new OrderLineItem();
            lineItem.setOrderId(order.getOrderId());
            lineItem.setProductId(cartItem.getProductId());
            lineItem.setSalesPrice(cartItem.getProduct().getPrice());
            lineItem.setQuantity(cartItem.getQuantity());
            lineItem.setDiscount(cartItem.getDiscountPercent());
            orderLineItemRepository.save(lineItem);
        }

        shoppingCartService.clearCart(userId);
        return order;
    }
}