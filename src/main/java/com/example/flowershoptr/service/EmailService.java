package com.example.flowershoptr.service;

import java.util.List;
import java.util.Map;

/**
 * Интерфейс для сервиса отправки электронных писем.
 * Позволяет легко заменить реализацию в будущем.
 */
public interface EmailService {

    /**
     * Отправляет электронное письмо одному получателю
     *
     * @param to Email получателя
     * @param subject Тема письма
     * @param templateName Имя шаблона для использования
     * @param context Контекст данных для шаблона
     */
    void sendEmail(String to, String subject, String templateName, Map<String, Object> context);

    /**
     * Отправляет электронное письмо с вложениями
     *
     * @param to Email получателя
     * @param subject Тема письма
     * @param templateName Имя шаблона для использования
     * @param context Контекст данных для шаблона
     * @param attachments Карта вложений (имя файла -> содержимое)
     */
    void sendEmailWithAttachments(String to, String subject, String templateName,
                                  Map<String, Object> context, Map<String, byte[]> attachments);

    /**
     * Отправляет массовую рассылку нескольким получателям
     *
     * @param recipients Список email-адресов получателей
     * @param subject Тема письма
     * @param templateName Имя шаблона для использования
     * @param context Общий контекст данных для шаблона
     */
    void sendBulkEmail(List<String> recipients, String subject, String templateName, Map<String, Object> context);

    /**
     * Проверяет статус сервиса отправки писем
     *
     * @return true, если сервис доступен и готов к работе
     */
    boolean isServiceAvailable();
}