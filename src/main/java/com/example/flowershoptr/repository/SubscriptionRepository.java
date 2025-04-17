package com.example.flowershoptr.repository;

import com.example.flowershoptr.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    /**
     * Находит подписку по email
     *
     * @param email Email подписчика
     * @return Optional с подпиской, если найдена
     */
    Optional<Subscription> findByEmail(String email);

    /**
     * Находит все активные подписки
     *
     * @return Список активных подписок
     */
    List<Subscription> findByActiveTrue();

    /**
     * Проверяет существование подписки по email
     *
     * @param email Email для проверки
     * @return true, если подписка существует
     */
    boolean existsByEmail(String email);

    /**
     * Получает список всех email-адресов активных подписчиков
     *
     * @return Список email-адресов
     */
    @Query("SELECT s.email FROM Subscription s WHERE s.active = true")
    List<String> findAllActiveEmails();
}