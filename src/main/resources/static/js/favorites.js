/**
 * favorites.js - Скрипт для работы с избранными товарами
 *
 * Данный скрипт отвечает за функциональность избранного:
 * - Добавление/удаление товаров в избранное
 * - Переключение иконок "сердечко"
 * - Обновление счетчиков избранного
 * - Удаление товаров из списка избранного на странице избранного
 *
 * Подключите этот скрипт на все страницы, где используются
 * иконки добавления в избранное (.add-to-wishlist)
 */document.addEventListener('DOMContentLoaded', function() {
    // Обработка кнопок "Добавить в избранное"
    const wishlistButtons = document.querySelectorAll('.wishlist');
    wishlistButtons.forEach(button => {
        const productId = button.getAttribute('data-id');
        const icon = button.querySelector('i');

        // Проверяем, есть ли товар в избранном при загрузке страницы
        checkIfInFavorites(productId, icon);

        button.addEventListener('click', function() {
            const productId = this.getAttribute('data-id');
            const icon = this.querySelector('i');

            // Отправляем запрос на изменение статуса товара в избранном
            fetch(`/favorites/api/toggle?flowerId=${productId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                }
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    }
                    throw new Error('Ошибка при обновлении избранного');
                })
                .then(data => {
                    // Обновляем иконку в зависимости от статуса
                    if (data.inFavorites) {
                        icon.classList.remove('far');
                        icon.classList.add('fas');
                        showNotification('Товар добавлен в избранное');
                    } else {
                        icon.classList.remove('fas');
                        icon.classList.add('far');
                        showNotification('Товар удален из избранного');
                    }

                    // Обновляем счетчик товаров в избранном, если он есть на странице
                    updateFavoritesCounter(data.totalItems);
                })
                .catch(error => {
                    console.error('Ошибка:', error);
                    showNotification('Произошла ошибка при обновлении избранного');
                });
        });
    });

    // Проверяет, находится ли товар в избранном
    function checkIfInFavorites(productId, icon) {
        fetch(`/favorites/api/check?flowerId=${productId}`)
            .then(response => response.json())
            .then(data => {
                if (data.inFavorites) {
                    icon.classList.remove('far');
                    icon.classList.add('fas');
                } else {
                    icon.classList.remove('fas');
                    icon.classList.add('far');
                }
            })
            .catch(error => {
                console.error('Ошибка при проверке избранного:', error);
            });
    }

    // Обновляет счетчик товаров в избранном
    function updateFavoritesCounter(count) {
        const counters = document.querySelectorAll('.favorites-counter');
        counters.forEach(counter => {
            counter.textContent = count;

            // Если счетчик равен 0, можно скрыть его
            if (count > 0) {
                counter.classList.remove('hidden');
            } else {
                counter.classList.add('hidden');
            }
        });

        // Также обновляем счетчик на странице избранного, если мы на ней
        const favoritesCount = document.getElementById('favorites-count');
        if (favoritesCount) {
            favoritesCount.textContent = count;
        }
    }

    // Функция показа уведомлений (используем ту же, что и в корзине)
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

    // Обработка кнопок удаления из избранного на странице избранного
    const removeButtons = document.querySelectorAll('.remove-from-favorites');
    if (removeButtons.length > 0) {
        removeButtons.forEach(button => {
            button.addEventListener('click', function() {
                const productId = this.getAttribute('data-id');
                const productElement = this.closest('.favorites-item');

                // Отправляем запрос на удаление товара из избранного
                fetch(`/favorites/api/remove?flowerId=${productId}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    }
                })
                    .then(response => {
                        if (response.ok) {
                            return response.json();
                        }
                        throw new Error('Ошибка при удалении из избранного');
                    })
                    .then(data => {
                        // Удаляем элемент со страницы
                        if (productElement) {
                            productElement.remove();
                        }

                        // Обновляем счетчик товаров в избранном
                        updateFavoritesCounter(data.totalItems);

                        // Показываем уведомление
                        showNotification('Товар удален из избранного');

                        // Если товаров в избранном не осталось, обновляем страницу
                        if (data.totalItems === 0) {
                            setTimeout(() => {
                                location.reload();
                            }, 1000);
                        }
                    })
                    .catch(error => {
                        console.error('Ошибка:', error);
                        showNotification('Произошла ошибка при удалении из избранного');
                    });
            });
        });
    }

    // Обработка кнопки "Очистить избранное"
    const clearFavoritesButton = document.querySelector('.clear-favorites');
    if (clearFavoritesButton) {
        clearFavoritesButton.addEventListener('click', function() {
            if (confirm('Вы уверены, что хотите очистить избранное?')) {
                fetch('/favorites/api/clear', {
                    method: 'POST'
                })
                    .then(response => {
                        if (response.ok) {
                            return response.json();
                        }
                        throw new Error('Ошибка при очистке избранного');
                    })
                    .then(data => {
                        // Обновляем страницу
                        location.reload();
                    })
                    .catch(error => {
                        console.error('Ошибка:', error);
                        showNotification('Произошла ошибка при очистке избранного');
                    });
            }
        });
    }

    // Загружаем количество товаров в избранном при загрузке страницы
    fetch('/favorites/api/count')
        .then(response => response.json())
        .then(data => {
            updateFavoritesCounter(data.count);
        })
        .catch(error => {
            console.error('Ошибка при получении количества товаров в избранном:', error);
        });
});