package ru.viktorgezz.pizza_resource_service.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.viktorgezz.pizza_resource_service.model.CartItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private static final String CART_SESSION_KEY = "cart";

    public BigDecimal getTotalPrice() {
        List<CartItem> cart = getCart();
        return cart.stream()
                .map(item -> item.getPrice()
                        .multiply(BigDecimal
                                .valueOf(item
                                        .getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<CartItem> getCart() {
        HttpSession session = getSession();
        Object cartObj = session.getAttribute(CART_SESSION_KEY);
        List<CartItem> cart;
        if (cartObj instanceof List) {
            cart = (List<CartItem>) cartObj;
        } else {
            cart = new ArrayList<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    public void addToCart(String title, BigDecimal price) {
        List<CartItem> cart = getCart();
        cart.stream()
                .filter(item -> item.getTitle().equals(title))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + 1),
                        () -> cart.add(new CartItem(title, price, 1))
                );
        getSession().setAttribute(CART_SESSION_KEY, cart);
    }

    public void decreaseQuantity(String title) {
        List<CartItem> cart = getCart();
        cart.stream()
                .filter(item -> item.getTitle().equals(title))
                .findFirst()
                .ifPresent(item -> {
                    int newQuantity = item.getQuantity() - 1;
                    if (newQuantity <= 0) {
                        cart.remove(item); // Удаляем, если количество становится 0
                    } else {
                        item.setQuantity(newQuantity);
                    }
                });
        getSession().setAttribute(CART_SESSION_KEY, cart);
    }

    public void removeFromCart(String title) {
        List<CartItem> cart = getCart();
        cart.removeIf(item -> item.getTitle().equals(title));
        getSession().setAttribute(CART_SESSION_KEY, cart);
    }

    public void clearCart() {
        getSession().removeAttribute(CART_SESSION_KEY);
    }

    private HttpSession getSession() {
        ServletRequestAttributes attr =
                (ServletRequestAttributes) RequestContextHolder
                        .currentRequestAttributes();
        return attr.getRequest().getSession(true);
    }

}
