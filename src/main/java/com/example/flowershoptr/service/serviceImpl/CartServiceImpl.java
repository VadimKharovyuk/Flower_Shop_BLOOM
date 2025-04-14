package com.example.flowershoptr.service.serviceImpl;

import com.example.flowershoptr.dto.cart.CartDto;
import com.example.flowershoptr.dto.flower.PopularFlowerDto;
import com.example.flowershoptr.maper.CartMapper;
import com.example.flowershoptr.model.Cart;
import com.example.flowershoptr.model.CartItem;
import com.example.flowershoptr.model.Flower;
import com.example.flowershoptr.model.Order;
import com.example.flowershoptr.model.OrderItem;
import com.example.flowershoptr.repository.CartRepository;
import com.example.flowershoptr.repository.FlowerRepository;
import com.example.flowershoptr.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CartServiceImpl implements CartService {

    private static final String CART_SESSION_KEY = "user_cart";

    private final CartRepository cartRepository;
    private final FlowerRepository flowerRepository;
    private final CartMapper cartMapper;


    @Override
    public Cart getOrCreateCartFromSession(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_SESSION_KEY);

        if (cart == null) {
            cart = new Cart();
            session.setAttribute(CART_SESSION_KEY, cart);
        }

        return cart;
    }

    @Override
    public CartDto getCartDto(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            // Возвращаем пустую корзину DTO, но не создаем сессию
            return new CartDto();
        }
        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public CartDto addFlowerToCart(HttpSession session, Long flowerId, Integer quantity) {
        Cart cart = (Cart) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            // Создаем корзину только при добавлении товара
            cart = new Cart();
            session.setAttribute(CART_SESSION_KEY, cart);
        }

        // Проверяем наличие цветка
        Flower flower = flowerRepository.findById(flowerId)
                .orElseThrow(() -> new RuntimeException("Flower not found with ID: " + flowerId));

        // Проверяем, достаточно ли цветков в наличии
        if (flower.getCount() < quantity) {
            throw new RuntimeException("Not enough flowers in stock. Available: " + flower.getCount());
        }
       incrementPopularity(flower);
        incrementFlowerFavorites(flowerId, quantity);

        // Проверяем, есть ли уже такой цветок в корзине
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getFlower().getId().equals(flowerId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Обновляем количество
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + quantity;

            // Проверяем снова на общее количество
            if (flower.getCount() < newQuantity) {
                throw new RuntimeException("Not enough flowers in stock. Available: " + flower.getCount());
            }

            updateCartItem(item, newQuantity);
        } else {
            // Создаем новый элемент
            CartItem newItem = createCartItem(cart, flower, quantity);
            cart.getItems().add(newItem);
        }

        // Пересчитываем общую стоимость
        recalculateTotalPrice(cart);

        // Обновляем сессию
        session.setAttribute(CART_SESSION_KEY, cart);

        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public CartDto updateCartItemQuantity(HttpSession session, Long flowerId, Integer quantity) {
        if (quantity <= 0) {
            return removeFlowerFromCart(session, flowerId);
        }

        Cart cart = getOrCreateCartFromSession(session);

        // Находим элемент корзины
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getFlower().getId().equals(flowerId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            Flower flower = item.getFlower();

            // Проверяем, достаточно ли цветков в наличии
            if (flower.getCount() < quantity) {
                throw new RuntimeException("Not enough flowers in stock. Available: " + flower.getCount());
            }

            // Обновляем количество
            updateCartItem(item, quantity);

            // Пересчитываем общую стоимость
            recalculateTotalPrice(cart);

            // Обновляем сессию
            session.setAttribute(CART_SESSION_KEY, cart);
        }

        return cartMapper.toDto(cart);
    }

    @Override
    @Transactional
    public CartDto removeFlowerFromCart(HttpSession session, Long flowerId) {
        Cart cart = getOrCreateCartFromSession(session);

        // Удаляем элемент из корзины
        cart.getItems().removeIf(item -> item.getFlower().getId().equals(flowerId));

        // Пересчитываем общую стоимость
        recalculateTotalPrice(cart);

        // Обновляем сессию
        session.setAttribute(CART_SESSION_KEY, cart);

        return cartMapper.toDto(cart);
    }

    @Override
    public void clearCart(HttpSession session) {
        Cart cart = getOrCreateCartFromSession(session);

        // Очищаем корзину
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);

        // Обновляем сессию
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    @Override
    @Transactional
    public Order createOrderFromCart(HttpSession session, String clientName, String clientPhone,
                                     String clientEmail, String deliveryAddress, String paymentMethod) {
        Cart cart = getOrCreateCartFromSession(session);

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot create order from empty cart");
        }

        // Создаем новый заказ
        Order order = new Order();
        order.setClientName(clientName);
        order.setClientPhone(clientPhone);
        order.setClientEmail(clientEmail);
        order.setDeliveryAddress(deliveryAddress);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("NEW");
        order.setPaymentStatus("PENDING");
        order.setTotal(cart.getTotalPrice());

        // Копируем товары из корзины в заказ
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setFlower(cartItem.getFlower());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setItemTotal(cartItem.getItemTotal());
            order.getItems().add(orderItem);
        }

        // Очищаем корзину после создания заказа
        clearCart(session);

        return order;
    }

    @Override
    @Transactional
    public CartDto assignCartToUser(HttpSession session, Long userId) {
        Cart sessionCart = getOrCreateCartFromSession(session);

        // Проверяем, есть ли у пользователя сохраненная корзина в БД
        Optional<Cart> existingUserCart = cartRepository.findByUserId(userId);

        if (existingUserCart.isPresent()) {
            Cart userCart = existingUserCart.get();

            // Если в сессии есть товары, переносим их в корзину пользователя
            if (!sessionCart.getItems().isEmpty()) {
                for (CartItem sessionItem : sessionCart.getItems()) {
                    // Ищем такой же товар в корзине пользователя
                    Optional<CartItem> existingItem = userCart.getItems().stream()
                            .filter(item -> item.getFlower().getId().equals(sessionItem.getFlower().getId()))
                            .findFirst();

                    if (existingItem.isPresent()) {
                        // Обновляем количество
                        CartItem item = existingItem.get();
                        updateCartItem(item, item.getQuantity() + sessionItem.getQuantity());
                    } else {
                        // Создаем новый элемент
                        CartItem newItem = createCartItem(userCart, sessionItem.getFlower(), sessionItem.getQuantity());
                        userCart.getItems().add(newItem);
                    }
                }

                // Пересчитываем общую стоимость
                recalculateTotalPrice(userCart);
            }

            // Обновляем корзину в БД
            Cart savedCart = cartRepository.save(userCart);

            // Обновляем сессию
            session.setAttribute(CART_SESSION_KEY, savedCart);

            return cartMapper.toDto(savedCart);
        } else {
            // Привязываем текущую корзину к пользователю
            sessionCart.setUserId(userId);

            // Сохраняем корзину в БД
            Cart savedCart = cartRepository.save(sessionCart);

            // Обновляем сессию
            session.setAttribute(CART_SESSION_KEY, savedCart);

            return cartMapper.toDto(savedCart);
        }
    }


    @Override
    public Integer getCartItemCount(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            return 0;
        }
        return cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }


    // Вспомогательные методы

    /**
     * Создает новый CartItem из данных запроса
     */
    private CartItem createCartItem(Cart cart, Flower flower, Integer quantity) {
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setFlower(flower);
        item.setQuantity(quantity);
        item.setPrice(flower.getPrice());
        item.setItemTotal(flower.getPrice().multiply(new BigDecimal(quantity)));
        return item;
    }

    /**
     * Обновляет существующий CartItem
     */
    private void updateCartItem(CartItem item, Integer quantity) {
        item.setQuantity(quantity);
        item.setItemTotal(item.getPrice().multiply(new BigDecimal(quantity)));
    }

    /**
     * Пересчитывает общую стоимость корзины
     */
    private void recalculateTotalPrice(Cart cart) {
        BigDecimal totalPrice = cart.getItems().stream()
                .map(CartItem::getItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(totalPrice);
    }

    private void incrementPopularity(Flower flower) {
        if (flower != null) {
            // Увеличиваем счетчик популярности на 1
            flower.setPopularityCount(flower.getPopularityCount() + 1);

            // Сохраняем обновленный объект в базе данных
            flowerRepository.save(flower);
        }
    }
    private void incrementFlowerFavorites(Long flowerId, int incrementValue) {
        Flower flower = flowerRepository.findById(flowerId)
                .orElseThrow(() -> new RuntimeException("Flower not found with ID: " + flowerId));

        flower.setFavoritesCount(flower.getFavoritesCount() + incrementValue);
        flowerRepository.save(flower);
    }

}