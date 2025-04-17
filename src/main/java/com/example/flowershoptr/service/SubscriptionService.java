package com.example.flowershoptr.service;
import com.example.flowershoptr.dto.Subscrip.SubscriptionDto;
import com.example.flowershoptr.exception.AlreadySubscribedException;
import com.example.flowershoptr.model.Subscription;
import com.example.flowershoptr.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final EmailService emailService;

    /**
     * Получает все подписки
     *
     * @return Список всех подписок
     */
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    /**
     * Получает все активные подписки
     *
     * @return Список активных подписок
     */
    public List<Subscription> getActiveSubscriptions() {
        return subscriptionRepository.findByActiveTrue();
    }

    public List<String> getActiveSubscriberEmails() {
        return subscriptionRepository.findAllActiveEmails();
    }

    @Transactional
    public Subscription subscribe(SubscriptionDto subscriptionDto) {
        String email = subscriptionDto.getEmail();

        try {
            Optional<Subscription> existingSubscription = subscriptionRepository.findByEmail(email);

            if (existingSubscription.isPresent()) {
                Subscription subscription = existingSubscription.get();
                if (!subscription.isActive()) {
                    subscription.setActive(true);
                    subscriptionRepository.save(subscription);
                    sendWelcomeEmail(email);
                    log.info("Повторная активация подписки для: {}", email);
                    return subscription;
                } else {
                    log.info("Попытка подписки уже активного пользователя: {}", email);
                    throw new AlreadySubscribedException("Этот email уже подписан на рассылку");
                }
            } else {
                Subscription newSubscription = Subscription.builder()
                        .email(email)
                        .active(true)
                        .build();

                Subscription savedSubscription = subscriptionRepository.save(newSubscription);
                sendWelcomeEmail(email);
                log.info("Создана новая подписка для: {}", email);
                return savedSubscription;
            }
        } catch (AlreadySubscribedException e) {
            // Пробрасываем наше специальное исключение дальше
            throw e;
        } catch (Exception e) {
            log.error("Ошибка при создании подписки для {}: {}", email, e.getMessage());
            return null;
        }
    }

    @Transactional
    public boolean unsubscribe(String email) {
        Optional<Subscription> subscription = subscriptionRepository.findByEmail(email);

        if (subscription.isPresent()) {
            Subscription sub = subscription.get();
            sub.setActive(false);
            subscriptionRepository.save(sub);
            log.info("Пользователь отписался: {}", email);
            return true;
        }

        log.warn("Попытка отписки несуществующего пользователя: {}", email);
        return false;
    }

    private void sendWelcomeEmail(String email) {
        try {
            Map<String, Object> context = new HashMap<>();
            context.put("recipientEmail", email);

            emailService.sendEmail(
                    email,
                    "Спасибо за подписку на новости Flower Shop",
                    "subscription-confirmation",
                    context
            );
        } catch (Exception e) {
            log.error("Ошибка при отправке приветственного письма для {}: {}", email, e.getMessage());
        }
    }

    public long getActiveSubscriberCount() {
        return subscriptionRepository.findByActiveTrue().size();
    }
}