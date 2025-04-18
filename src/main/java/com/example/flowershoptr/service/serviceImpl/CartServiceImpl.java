package com.example.flowershoptr.service.serviceImpl;

import com.example.flowershoptr.dto.cart.CartDto;
import com.example.flowershoptr.dto.flower.PopularFlowerDto;
import com.example.flowershoptr.maper.CartMapper;
import com.example.flowershoptr.model.*;
import com.example.flowershoptr.repository.CartRepository;
import com.example.flowershoptr.repository.FlowerRepository;
import com.example.flowershoptr.service.CartService;
import com.example.flowershoptr.service.SpecialOfferService;
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
    private final SpecialOfferService specialOfferService ;


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
        Cart cart = getOrCreateCart(session);

        Flower flower = flowerRepository.findById(flowerId)
                .orElseThrow(() -> new RuntimeException("Flower not found with ID: " + flowerId));

        validateStockAvailability(flower, quantity);

        incrementStats(flower, flowerId, quantity);

        // Получаем ценовую информацию
        PriceInfo priceInfo = getPriceInfo(flower);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getFlower().getId().equals(flowerId))
                .findFirst();

        if (existingItem.isPresent()) {
            updateExistingCartItem(existingItem.get(), quantity, flower.getCount(), priceInfo);
        } else {
            CartItem newItem = createNewCartItem(flower, quantity, priceInfo);
            cart.getItems().add(newItem);
        }

        recalculateTotalPrice(cart);

        session.setAttribute(CART_SESSION_KEY, cart);

        return cartMapper.toDto(cart);
    }


    private Cart getOrCreateCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    private void validateStockAvailability(Flower flower, int quantity) {
        if (flower.getCount() < quantity) {
            throw new RuntimeException("Not enough flowers in stock. Available: " + flower.getCount());
        }
    }

    private void incrementStats(Flower flower, Long flowerId, int quantity) {
        incrementPopularity(flower);
        incrementFlowerFavorites(flowerId, quantity);
        flower.setCartAddCount(flower.getCartAddCount() + 1);
        flowerRepository.save(flower);
    }
    private static class PriceInfo {
        BigDecimal regularPrice;
        BigDecimal finalPrice;
        boolean hasDiscount;
        LocalDateTime discountExpiry;

        public PriceInfo(BigDecimal regularPrice, BigDecimal finalPrice, boolean hasDiscount, LocalDateTime discountExpiry) {
            this.regularPrice = regularPrice;
            this.finalPrice = finalPrice;
            this.hasDiscount = hasDiscount;
            this.discountExpiry = discountExpiry;
        }
    }

    private PriceInfo getPriceInfo(Flower flower) {
        BigDecimal regularPrice = flower.getPrice();
        if (specialOfferService.hasActiveDiscount(flower)) {
            Optional<SpecialOffer> offer = specialOfferService.getBestOffer(flower);
            if (offer.isPresent()) {
                return new PriceInfo(
                        regularPrice,
                        specialOfferService.getDiscountedPrice(flower),
                        true,
                        offer.get().getEndDate()
                );
            }
        }
        return new PriceInfo(regularPrice, regularPrice, false, null);
    }

    private void updateExistingCartItem(CartItem item, int addQuantity, int stockAvailable, PriceInfo priceInfo) {
        int newQuantity = item.getQuantity() + addQuantity;

        if (stockAvailable < newQuantity) {
            throw new RuntimeException("Not enough flowers in stock. Available: " + stockAvailable);
        }

        item.setQuantity(newQuantity);
        item.setItemTotal(priceInfo.finalPrice.multiply(BigDecimal.valueOf(newQuantity)));

        item.setPrice(priceInfo.regularPrice); // обычная цена всегда

        if (priceInfo.hasDiscount) {
            item.setDiscountPrice(priceInfo.finalPrice); // скидочная
            item.setHasDiscount(true);
            item.setDiscountExpiryDate(priceInfo.discountExpiry);
        } else {
            item.setDiscountPrice(null);
            item.setHasDiscount(false);
            item.setDiscountExpiryDate(null);
        }// текущая цена, которая идёт в расчёт
    }

    private CartItem createNewCartItem(Flower flower, int quantity, PriceInfo priceInfo) {
        CartItem item = new CartItem();
        item.setFlower(flower);
        item.setQuantity(quantity);

        item.setPrice(priceInfo.regularPrice); // сохраняем обычную цену
        item.setDiscountPrice(priceInfo.finalPrice); // сохраняем цену со скидкой (если есть)
        item.setHasDiscount(priceInfo.hasDiscount);
        item.setDiscountExpiryDate(priceInfo.discountExpiry);
        item.setItemTotal(priceInfo.finalPrice.multiply(BigDecimal.valueOf(quantity)));

        return item;
    }


    @Override
    @Transactional
    public CartDto updateCartItemQuantity(HttpSession session, Long flowerId, Integer quantity) {
        if (quantity <= 0) {
            return removeFlowerFromCart(session, flowerId);
        }

        Cart cart = getOrCreateCartFromSession(session);

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getFlower().getId().equals(flowerId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            Flower flower = item.getFlower();

            if (flower.getCount() < quantity) {
                throw new RuntimeException("Not enough flowers in stock. Available: " + flower.getCount());
            }

            item.setQuantity(quantity);

            BigDecimal currentPrice = flower.getPrice();
            boolean hasDiscount = false;
            BigDecimal discountedPrice = null;
            LocalDateTime discountExpiry = null;

            if (specialOfferService.hasActiveDiscount(flower)) {
                Optional<SpecialOffer> bestOffer = specialOfferService.getBestOffer(flower);
                if (bestOffer.isPresent()) {
                    hasDiscount = true;
                    discountedPrice = specialOfferService.getDiscountedPrice(flower);
                    discountExpiry = bestOffer.get().getEndDate();
                }
            }

            if (hasDiscount) {
                item.setDiscountPrice(discountedPrice);  // сохраняем цену со скидкой
                item.setPrice(discountedPrice);          // используем в расчётах
                item.setHasDiscount(true);
                item.setDiscountExpiryDate(discountExpiry);
            } else {
                item.setDiscountPrice(null);
                item.setPrice(currentPrice);
                item.setHasDiscount(false);
                item.setDiscountExpiryDate(null);
            }

            item.setItemTotal(item.getPrice().multiply(BigDecimal.valueOf(quantity)));
        }

        recalculateTotalPrice(cart);
        session.setAttribute(CART_SESSION_KEY, cart);

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