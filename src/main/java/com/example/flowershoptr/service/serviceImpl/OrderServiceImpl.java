package com.example.flowershoptr.service.serviceImpl;

import com.example.flowershoptr.dto.Order.CreateOrderDTO;
import com.example.flowershoptr.dto.Order.OrderDetailsDTO;
import com.example.flowershoptr.dto.Order.OrderListDTO;
import com.example.flowershoptr.enums.OrderStatus;
import com.example.flowershoptr.enums.PaymentStatus;

import com.example.flowershoptr.maper.OrderMapper;
import com.example.flowershoptr.model.Cart;
import com.example.flowershoptr.model.CartItem;
import com.example.flowershoptr.model.Order;
import com.example.flowershoptr.model.OrderItem;
import com.example.flowershoptr.repository.OrderRepository;
import com.example.flowershoptr.service.CartService;
import com.example.flowershoptr.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private static final String ORDER_NUMBER_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int ORDER_NUMBER_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    private final OrderRepository orderRepository;
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

        // Создаем заказ
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
        // Сохраняем заказ
        Order savedOrder = orderRepository.save(order);

        // НЕ очищаем корзину из сессии здесь
        // cartService.clearCart(session);

        return savedOrder;
    }

    @Override
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
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


    private String generateOrderNumber() {
        StringBuilder sb = new StringBuilder("ORD-");
        for (int i = 0; i < ORDER_NUMBER_LENGTH; i++) {
            int index = random.nextInt(ORDER_NUMBER_CHARS.length());
            sb.append(ORDER_NUMBER_CHARS.charAt(index));
        }
        return sb.toString();
    }


}