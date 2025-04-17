document.addEventListener('DOMContentLoaded', function() {
    // Находим все формы подписки на странице
    const newsletterForms = document.querySelectorAll('.newsletter-form');

    // Добавляем обработчик отправки для каждой формы
    newsletterForms.forEach(form => {
        form.addEventListener('submit', function(event) {
            // Предотвращаем стандартную отправку формы
            event.preventDefault();

            // Находим поле ввода email в текущей форме
            const emailInput = form.querySelector('input[type="email"]');
            const email = emailInput.value.trim();

            // Проверяем, что email введен и имеет корректный формат
            if (!email) {
                showMessage(form, 'Пожалуйста, введите email', 'error');
                return;
            }

            if (!isValidEmail(email)) {
                showMessage(form, 'Пожалуйста, введите корректный email', 'error');
                return;
            }

            // Отправляем запрос на сервер
            subscribeUser(email, form);
        });
    });

    /**
     * Проверяет валидность email
     * @param {string} email - Email для проверки
     * @returns {boolean} - Результат проверки
     */
    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    /**
     * Отправляет запрос на подписку
     * @param {string} email - Email пользователя
     * @param {HTMLElement} form - Форма, из которой отправлен запрос
     */
    function subscribeUser(email, form) {
        // Показываем индикатор загрузки
        const submitButton = form.querySelector('button[type="submit"]');
        const originalButtonText = submitButton.textContent;
        submitButton.disabled = true;
        submitButton.textContent = 'Отправка...';

        // Подготавливаем данные для отправки
        const data = {
            email: email
        };

        // Отправляем запрос
        fetch('/api/subscriptions/subscribe', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                // Проверяем статус ответа
                if (response.ok) {
                    return response.json().then(data => {
                        // Очищаем поле ввода
                        form.querySelector('input[type="email"]').value = '';

                        // Проверяем, является ли это случаем уже существующей подписки
                        if (data.alreadySubscribed) {
                            showMessage(form, data.message, 'info');
                        } else {
                            // Показываем сообщение об успехе
                            showMessage(form, 'Спасибо за подписку! Проверьте вашу почту.', 'success');

                            // Если на странице есть счетчик подписчиков, обновляем его
                            const subscriberCountElement = document.querySelector('.subscriber-count span');
                            if (subscriberCountElement) {
                                const currentCount = parseInt(subscriberCountElement.textContent);
                                subscriberCountElement.textContent = currentCount + 1;
                            }
                        }

                        return data;
                    });
                } else {
                    // Обрабатываем ошибки
                    return response.text().then(errorText => {
                        throw new Error(errorText || 'Произошла ошибка при подписке');
                    });
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
                showMessage(form, error.message, 'error');
            })
            .finally(() => {
                // Восстанавливаем состояние кнопки
                submitButton.disabled = false;
                submitButton.textContent = originalButtonText;
            });
    }

    /**
     * Показывает сообщение под формой
     * @param {HTMLElement} form - Форма, для которой показывается сообщение
     * @param {string} text - Текст сообщения
     * @param {string} type - Тип сообщения ('success', 'error' или 'info')
     */
    function showMessage(form, text, type) {
        // Ищем существующий элемент сообщения или создаем новый
        let messageElement = form.nextElementSibling;
        if (!messageElement || !messageElement.classList.contains('newsletter-message')) {
            messageElement = document.createElement('div');
            messageElement.className = 'newsletter-message';
            form.parentNode.insertBefore(messageElement, form.nextSibling);
        }

        // Устанавливаем класс и текст сообщения
        messageElement.className = 'newsletter-message ' + type;
        messageElement.textContent = text;

        // Автоматически скрываем сообщение об успехе и информационное через 5 секунд
        if (type === 'success' || type === 'info') {
            setTimeout(() => {
                messageElement.style.opacity = '0';
                setTimeout(() => {
                    messageElement.remove();
                }, 500);
            }, 5000);
        }
    }
});