package com.example.flowershoptr.service.serviceImpl;

import com.example.flowershoptr.dto.Order.CreateOrderDTO;
import com.example.flowershoptr.dto.Order.OrderDetailsDTO;
import com.example.flowershoptr.dto.Order.OrderListDTO;
import com.example.flowershoptr.enums.OrderStatus;
import com.example.flowershoptr.enums.PaymentStatus;

import com.example.flowershoptr.maper.OrderMapper;
import com.example.flowershoptr.model.*;
import com.example.flowershoptr.repository.OrderRepository;
import com.example.flowershoptr.repository.UserRepository;
import com.example.flowershoptr.service.CartService;
import com.example.flowershoptr.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private static final String ORDER_NUMBER_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int ORDER_NUMBER_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;



    @Override
    @Transactional
    public Order createOrder(CreateOrderDTO createOrderDTO, HttpSession session) {
        // Получаем корзину из сессии
        Cart cart = cartService.getOrCreateCartFromSession(session);

        if (cart == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Корзина пуста или не найдена");
        }
        // Создаем заказ с помощью выделенного метода
        Order order = createOrderFromCart(createOrderDTO, cart);

        // Получаем текущего пользователя, если авторизован
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            // Связываем заказ с пользователем
            associateOrderWithUser(order, auth);
        }

        // Сохраняем заказ
        Order savedOrder = orderRepository.save(order);

        return savedOrder;
    }

    // Выделенный метод для создания заказа из корзины
    private Order createOrderFromCart(CreateOrderDTO createOrderDTO, Cart cart) {
        Order order = new Order();
        order.setClientName(createOrderDTO.getClientName());
        order.setClientPhone(createOrderDTO.getClientPhone());
        order.setClientEmail(createOrderDTO.getClientEmail());
        order.setDeliveryAddress(createOrderDTO.getDeliveryAddress());
        order.setDeliveryDate(createOrderDTO.getDeliveryDate());
        order.setNotes(createOrderDTO.getNotes());
        order.setPaymentMethod(createOrderDTO.getPaymentMethod());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderStatus(OrderStatus.PENDING);

        // Копируем товары из корзины
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setFlower(cartItem.getFlower());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setItemTotal(cartItem.getItemTotal());

            order.getItems().add(orderItem);
            total = total.add(orderItem.getItemTotal());
        }

        order.setTotal(total);
        order.setOrderNumber(generateOrderNumber());

        return order;
    }

    // Метод для связывания заказа с авторизованным пользователем
    private void associateOrderWithUser(Order order, Authentication auth) {
        String email = null;

        if (auth instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) auth;
            OAuth2User oauth2User = oauth2Token.getPrincipal();
            email = oauth2User.getAttribute("email");
        } else {
            email = auth.getName();
        }

        if (email != null) {
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                order.setUser(user);
            }
        }
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<Order> getOrdersByClientPhone(String phone) {
        return orderRepository.findByClientPhone(phone);
    }

    @Override
    public List<Order> getOrdersByOrderStatus(OrderStatus status) {
        return orderRepository.findByOrderStatus(status);
    }

    @Override
    public List<Order> getOrdersByPaymentStatus(PaymentStatus status) {
        return orderRepository.findByPaymentStatus(status);
    }

    @Override
    public List<Order> getOrdersByDateRange(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByCreatedAtBetween(start, end);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        order.setOrderStatus(status);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updatePaymentStatus(Long orderId, PaymentStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        order.setPaymentStatus(status);

        // Если заказ оплачен, обновляем его статус
        if (status == PaymentStatus.COMPLETED) {
            order.setOrderStatus(OrderStatus.PAID);
        }

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        order.setOrderStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    @Override
    public OrderDetailsDTO getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        return orderMapper.orderToOrderDetailsDTO(order);
    }

    @Override
    public List<OrderListDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orderMapper.ordersToOrderListDTOs(orders);
    }

    @Override
    public List<OrderListDTO> getOrdersByStatus(OrderStatus status) {
        List<Order> orders = orderRepository.findByOrderStatus(status);
        return orderMapper.ordersToOrderListDTOs(orders);
    }

    @Override
    public List<OrderDetailsDTO> getOrdersByPhone(String phone) {
        List<Order> orders = orderRepository.findByClientPhone(phone);
        return orders.stream()
                .map(orderMapper::orderToOrderDetailsDTO)
                .collect(Collectors.toList());
    }



    @Override
    public Optional<OrderDetailsDTO> getOrderByNumber(String orderNumber) {
        Optional<Order> order = orderRepository.findByOrderNumber(orderNumber);
        return order.map(orderMapper::orderToOrderDetailsDTO);
    }

    @Override
    public void updateOrderEmail(Long orderId, String email) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Заказ с ID " + orderId + " не найден"));

        order.setClientEmail(email);
        orderRepository.save(order);

        log.info("Email для заказа ID {} обновлен на {}", orderId, email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderListDTO> getAllOrdersByUserIdOrEmail(Long userId, String email) {
        List<Order> orders = orderRepository.findAllByUserIdOrEmail(userId, email);
        return orders.stream()
                .map(orderMapper::orderToOrderListDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailsDTO getOrderDetailsByIdAndUserIdOrEmail(Long orderId, Long userId, String email) {
        Optional<Order> orderOpt = orderRepository.findByIdAndUserIdOrEmail(orderId, userId, email);
        return orderOpt.map(orderMapper::orderToOrderDetailsDTO).orElse(null);
    }

    @Override
    public List<OrderListDTO> getPageOrderClientByMail(String email, Long id) {
        // Получаем все заказы пользователя по email или id
        List<Order> orders = orderRepository.findByUserEmailOrUserId(email, id);

        // Сортируем по дате создания (от новых к старым) и берём только 5
        return orders.stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .limit(5)
                .map(orderMapper::orderToOrderListDTO)
                .collect(Collectors.toList());
    }


    private String generateOrderNumber() {
        StringBuilder sb = new StringBuilder("ORD-");
        for (int i = 0; i < ORDER_NUMBER_LENGTH; i++) {
            int index = random.nextInt(ORDER_NUMBER_CHARS.length());
            sb.append(ORDER_NUMBER_CHARS.charAt(index));
        }
        return sb.toString();
    }


}