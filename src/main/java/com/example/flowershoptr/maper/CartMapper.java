package com.example.flowershoptr.maper;

import com.example.flowershoptr.dto.cart.CartDto;
import com.example.flowershoptr.dto.cart.CartItemDto;
import com.example.flowershoptr.model.Cart;
import com.example.flowershoptr.model.CartItem;
import com.example.flowershoptr.model.Flower;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartDto toDto(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartDto cartDto = new CartDto();
        cartDto.setId(cart.getId());
        cartDto.setTotalPrice(cart.getTotalPrice());
        cartDto.setCreatedAt(cart.getCreatedAt());
        cartDto.setUpdatedAt(cart.getUpdatedAt());



        if (cart.getItems() != null) {
            Set<CartItemDto> itemDtos = cart.getItems().stream()
                    .map(this::toDto)
                    .collect(Collectors.toSet());
            cartDto.setItems(itemDtos);
        }

        return cartDto;
    }

    public CartItemDto toDto(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }

        CartItemDto dto = new CartItemDto();
        dto.setId(cartItem.getId());

        if (cartItem.getFlower() != null) {
            dto.setFlowerId(cartItem.getFlower().getId());
            dto.setFlowerName(cartItem.getFlower().getName());
            dto.setFlowerImageUrl(cartItem.getFlower().getPreviewImageUrl());
        }

        dto.setQuantity(cartItem.getQuantity());
        dto.setPrice(cartItem.getPrice()); // обычная цена
        dto.setDiscountPrice(cartItem.getDiscountPrice()); // цена со скидкой (если есть)
        dto.setHasDiscount(cartItem.isHasDiscount());
        dto.setDiscountExpiryDate(cartItem.getDiscountExpiryDate());
        dto.setItemTotal(cartItem.getItemTotal());

        return dto;
    }


    // Этот метод будет использоваться только когда все объекты уже загружены
    public Cart toEntity(CartDto cartDto, Set<Flower> flowers) {
        if (cartDto == null) {
            return null;
        }

        Cart cart = new Cart();
        cart.setId(cartDto.getId());
        cart.setTotalPrice(cartDto.getTotalPrice());

        if (cartDto.getItems() != null) {
            Set<CartItem> items = cartDto.getItems().stream()
                    .map(itemDto -> {
                        // Находим цветок по ID
                        Flower flower = flowers.stream()
                                .filter(f -> f.getId().equals(itemDto.getFlowerId()))
                                .findFirst()
                                .orElse(null);

                        return toEntity(itemDto, cart, flower);
                    })
                    .collect(Collectors.toSet());
            cart.setItems(items);
        }

        return cart;
    }

    public CartItem toEntity(CartItemDto itemDto, Cart cart, Flower flower) {
        if (itemDto == null) {
            return null;
        }

        CartItem item = new CartItem();
        item.setId(itemDto.getId());
        item.setCart(cart);
        item.setFlower(flower);
        item.setQuantity(itemDto.getQuantity());
        item.setPrice(itemDto.getPrice());
        item.setItemTotal(itemDto.getItemTotal());

        return item;
    }
}