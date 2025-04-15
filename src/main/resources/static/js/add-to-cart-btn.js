document.addEventListener('DOMContentLoaded', function() {
    // Управление количеством товара на странице детального просмотра
    const quantityInput = document.querySelector('.quantity-input');
    const decreaseBtn = document.querySelector('.decrease-btn');
    const increaseBtn = document.querySelector('.increase-btn');

    if (quantityInput && decreaseBtn && increaseBtn) {
        // Обработчик уменьшения количества
        decreaseBtn.addEventListener('click', function(e) {
            e.preventDefault();
            const currentValue = parseInt(quantityInput.value);
            if (currentValue > parseInt(quantityInput.min)) {
                quantityInput.value = currentValue - 1;
            }
        });

        // Обработчик увеличения количества
        increaseBtn.addEventListener('click', function(e) {
            e.preventDefault();
            const currentValue = parseInt(quantityInput.value);
            const maxValue = parseInt(quantityInput.getAttribute('max'));

            if (currentValue < maxValue) {
                quantityInput.value = currentValue + 1;
            } else {
                showNotification('Достигнуто максимальное доступное количество');
            }
        });

        // Проверка ввода в поле количества
        quantityInput.addEventListener('change', function() {
            let value = parseInt(this.value);
            const min = parseInt(this.min);
            const max = parseInt(this.getAttribute('max'));

            if (isNaN(value) || value < min) {
                this.value = min;
                value = min;
            } else if (value > max) {
                this.value = max;
                value = max;
                showNotification('Достигнуто максимальное доступное количество');
            }
        });
    }

    // Обработка кнопки "Добавить в корзину" на странице детального просмотра
    const detailAddToCartBtn = document.querySelector('.add-to-cart-section .add-to-cart-btn');

    if (detailAddToCartBtn) {
        detailAddToCartBtn.addEventListener('click', function() {
            const productId = this.getAttribute('data-id');
            const quantity = document.querySelector('.quantity-input').value;

            if (!productId) {
                console.error('Не удалось найти ID товара для добавления в корзину');
                showNotification('Ошибка при добавлении товара в корзину');
                return;
            }

            console.log('Добавление в корзину товара с ID:', productId, 'Количество:', quantity);

            // Отправляем запрос на добавление товара в корзину
            fetch(`/cart/api/add?flowerId=${productId}&quantity=${quantity}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                }
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    }
                    throw new Error('Ошибка при добавлении в корзину');
                })
                .then(data => {
                    // Показываем уведомление
                    showNotification('Товар добавлен в корзину');

                    // Обновляем счетчик товаров в корзине
                    updateCartCounter(data.totalItems);

                    // Анимация кнопки для визуального подтверждения
                    animateCartButton(this);
                })
                .catch(error => {
                    console.error('Ошибка:', error);
                    showNotification('Произошла ошибка при добавлении в корзину');
                });
        });
    }

    // Обработка кнопок "Добавить в корзину" в каталоге
    const cartButtons = document.querySelectorAll('.add-to-cart');
    console.log('Найдено кнопок для добавления в корзину:', cartButtons.length);

    cartButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Находим ID товара в родительском элементе
            const productCard = this.closest('.product-card');

            if (!productCard) {
                console.error('Не найден родительский элемент .product-card');
                showNotification('Ошибка при добавлении товара в корзину');
                return;
            }

            const productId = productCard.getAttribute('data-product-id') ||
                (productCard.querySelector('.wishlist') ?
                    productCard.querySelector('.wishlist').getAttribute('data-id') : null);

            if (!productId) {
                console.error('Не удалось найти ID товара для добавления в корзину');
                showNotification('Ошибка при добавлении товара в корзину');
                return;
            }

            console.log('Добавление в корзину товара с ID:', productId);

            // Отправляем запрос на добавление товара в корзину
            fetch(`/cart/api/add?flowerId=${productId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                }
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    }
                    throw new Error('Ошибка при добавлении в корзину');
                })
                .then(data => {
                    // Показываем уведомление
                    showNotification('Товар добавлен в корзину');

                    // Обновляем счетчик товаров в корзине
                    updateCartCounter(data.totalItems);

                    // Анимация кнопки для визуального подтверждения
                    animateCartButton(this);
                })
                .catch(error => {
                    console.error('Ошибка:', error);
                    showNotification('Произошла ошибка при добавлении в корзину');
                });
        });
    });

    // Анимация кнопки добавления в корзину
    function animateCartButton(button) {
        button.classList.add('added');

        // Временно меняем текст кнопки
        const originalText = button.textContent;
        button.textContent = 'Добавлено';

        // Возвращаем исходное состояние через 1.5 секунды
        setTimeout(() => {
            button.classList.remove('added');
            button.textContent = originalText;
        }, 1500);
    }

    // Обновляет счетчик товаров в корзине
    function updateCartCounter(count) {
        const counters = document.querySelectorAll('.cart-counter');
        counters.forEach(counter => {
            counter.textContent = count;

            // Если счетчик равен 0, можно скрыть его
            if (count > 0) {
                counter.classList.remove('hidden');
            } else {
                counter.classList.add('hidden');
            }
        });
    }

    // Функция показа уведомлений
    function showNotification(message) {
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
    }
});