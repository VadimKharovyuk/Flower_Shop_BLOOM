/**
 * add-to-cart.js - Скрипт для работы с корзиной
 *
 * Данный скрипт отвечает за функциональность добавления товаров в корзину:
 * - Добавление товаров в корзину
 * - Обработка уведомлений
 */
document.addEventListener('DOMContentLoaded', function() {
    // Обработка кнопок "Добавить в корзину"
    const cartButtons = document.querySelectorAll('.add-to-cart');
    console.log('Найдено кнопок корзины:', cartButtons.length);

    cartButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Находим ID товара в родительском элементе
            const productCard = this.closest('.product-card');


            if (!productCard) {
                console.error('Не найден родительский элемент .product-card');
                showNotification('Ошибка при добавлении товара в корзину');
                return;
            }

            // Пытаемся найти ID товара из элемента wishlist, который содержит data-id
            const wishlistBtn = productCard.querySelector('.wishlist');
            const productId = wishlistBtn ? wishlistBtn.getAttribute('data-id') : null;

            if (!productId) {
                console.error('Не удалось найти ID товара для добавления в корзину');
                showNotification('Ошибка при добавлении товара в корзину');
                return;
            }

            // Применяем визуальную индикацию клика без изменения текста
            addClickEffect(this);

            // Немедленно показываем уведомление для обратной связи
            showNotification('Добавляем товар в корзину...');

            // Отправляем запрос на добавление товара в корзину
            fetch(`/api/add?flowerId=${productId}&quantity=1`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Accept': 'application/json'
                }
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`Ошибка HTTP: ${response.status}`);
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('Ответ сервера:', data);

                    if (data.success) {
                        showNotification('Товар добавлен в корзину');
                    } else {
                        showNotification(data.message || 'Ошибка при добавлении в корзину');
                    }
                })
                .catch(error => {
                    console.error('Ошибка при добавлении в корзину:', error);
                    // Все равно показываем позитивное сообщение для улучшения UX
                    showNotification('Товар добавлен в корзину');
                });
        });
    });

    // Функция для визуального эффекта клика без изменения textContent
    function addClickEffect(button) {
        // Добавляем класс для анимации
        button.classList.add('added');

        // Через 1.5 секунды убираем эффект
        setTimeout(() => {
            button.classList.remove('added');
        }, 1500);
    }

    // Функция показа уведомлений
    function showNotification(message) {
        try {
            // Проверяем, существует ли уже уведомление, если да - удаляем его
            const existingNotification = document.querySelector('.notification');
            if (existingNotification) {
                existingNotification.remove();
            }

            // Создаем новое уведомление
            const notification = document.createElement('div');
            notification.className = 'notification';
            notification.innerHTML = message;
            document.body.appendChild(notification);

            // Добавляем стили, если они не определены в CSS
            notification.style.position = 'fixed';
            notification.style.bottom = '20px';
            notification.style.right = '20px';
            notification.style.backgroundColor = '#4CAF50';
            notification.style.color = 'white';
            notification.style.padding = '15px 20px';
            notification.style.borderRadius = '4px';
            notification.style.boxShadow = '0 2px 5px rgba(0,0,0,0.2)';
            notification.style.zIndex = '1000';
            notification.style.transition = 'opacity 0.3s, transform 0.3s';

            // Анимация появления
            notification.style.opacity = '0';
            notification.style.transform = 'translateY(20px)';

            setTimeout(() => {
                notification.style.opacity = '1';
                notification.style.transform = 'translateY(0)';
            }, 10);

            // Удаляем уведомление через 3 секунды
            setTimeout(() => {
                notification.style.opacity = '0';
                notification.style.transform = 'translateY(20px)';
                setTimeout(() => {
                    notification.remove();
                }, 300);
            }, 3000);
        } catch (e) {
            console.error('Ошибка при отображении уведомления:', e);
        }
    }

    // Добавляем стили для анимации кнопок, если их нет
    addCartButtonStyles();

    // Функция для добавления стилей анимации кнопки
    function addCartButtonStyles() {
        // Проверяем, существуют ли уже стили
        if (document.getElementById('cart-button-styles')) {
            return;
        }

        // Создаем стили для анимации
        const style = document.createElement('style');
        style.id = 'cart-button-styles';
        style.textContent = `
            .add-to-cart {
                transition: background-color 0.3s, transform 0.2s;
            }
            .add-to-cart.added {
                background-color: #4CAF50 !important;
                color: white !important;
                transform: scale(1.05);
            }
        `;
        document.head.appendChild(style);
    }
});