package com.example.flowershoptr.maper;

import com.example.flowershoptr.dto.Order.OrderDetailsDTO;
import com.example.flowershoptr.dto.Order.OrderItemDTO;
import com.example.flowershoptr.dto.Order.OrderListDTO;
import com.example.flowershoptr.model.Order;
import com.example.flowershoptr.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    // Преобразование Order в OrderDetailsDTO
    public OrderDetailsDTO orderToOrderDetailsDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderDetailsDTO dto = new OrderDetailsDTO();
        dto.setId(order.getId());
        dto.setClientName(order.getClientName());
        dto.setClientPhone(order.getClientPhone());
        dto.setClientEmail(order.getClientEmail());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setDeliveryDate(order.getDeliveryDate());
        dto.setNotes(order.getNotes());
        dto.setTotal(order.getTotal());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setOrder_number(order.getOrderNumber());

        // Преобразование элементов заказа
        if (order.getItems() != null) {
            List<OrderItemDTO> itemDTOs = order.getItems().stream()
                    .map(this::orderItemToOrderItemDTO)
                    .collect(Collectors.toList());
            dto.setItems(itemDTOs);
        }

        return dto;
    }

    // Преобразование OrderItem в OrderItemDTO
    public OrderItemDTO orderItemToOrderItemDTO(OrderItem item) {
        if (item == null) {
            return null;
        }

        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setFlowerId(item.getFlower().getId());
        dto.setFlowerName(item.getFlower().getName());
        dto.setFlowerImage(item.getFlower().getPreviewImageUrl());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setItemTotal(item.getItemTotal());

        return dto;
    }

    // Преобразование Order в OrderListDTO (для списка заказов)
    public OrderListDTO orderToOrderListDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderListDTO dto = new OrderListDTO();
        dto.setId(order.getId());
        dto.setClientName(order.getClientName());
        dto.setClientPhone(order.getClientPhone());
        dto.setDeliveryDate(order.getDeliveryDate());
        dto.setTotal(order.getTotal());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setCreatedAt(order.getCreatedAt());


        // Количество позиций в заказе
        if (order.getItems() != null) {
            dto.setItemCount(order.getItems().size());
        } else {
            dto.setItemCount(0);
        }

        return dto;
    }

    // Преобразование списка Order в список OrderListDTO
    public List<OrderListDTO> ordersToOrderListDTOs(List<Order> orders) {
        if (orders == null) {
            return null;
        }

        return orders.stream()
                .map(this::orderToOrderListDTO)
                .collect(Collectors.toList());
    }
}