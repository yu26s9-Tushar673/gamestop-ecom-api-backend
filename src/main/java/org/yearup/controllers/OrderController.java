package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yearup.models.Order;
import org.yearup.models.User;
import org.yearup.service.OrderService;
import org.yearup.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("orders")
@CrossOrigin
@PreAuthorize("isAuthenticated()")
public class OrderController {
    private OrderService orderService;
    private UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<Order> checkout(Principal principal) {
        String userName = principal.getName();
        User user = userService.getByUserName(userName);
        int userId = user.getId();

        Order order = orderService.checkout(userId);

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}