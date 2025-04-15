// /**
//  * cart.js - Скрипт для работы с корзиной
//  *
//  * Данный скрипт отвечает за функциональность корзины:
//  * - Добавление товаров в корзину
//  * - Обновление счетчиков корзины
//  * - Обработка уведомлений
//  *
//  * Подключите этот скрипт на все страницы, где используются
//  * кнопки добавления в корзину (.add-to-cart)
//  */
// document.addEventListener('DOMContentLoaded', function() {
//     // Обработка кнопок "Добавить в корзину"
//     const cartButtons = document.querySelectorAll('.add-to-cart');
//     cartButtons.forEach(button => {
//         button.addEventListener('click', function() {
//             // Находим ID товара в родительском элементе
//             const productCard = this.closest('.product-card');
//             const productId = productCard.getAttribute('data-product-id') ||
//                 (productCard.querySelector('.wishlist') ?
//                     productCard.querySelector('.wishlist').getAttribute('data-id') : null);
//
//             if (!productId) {
//                 console.error('Не удалось найти ID товара для добавления в корзину');
//                 showNotification('Ошибка при добавлении товара в корзину');
//                 return;
//             }
//
//             // Отправляем запрос на добавление товара в корзину
//             fetch(`/cart/api/add?flowerId=${productId}`, {
//                 method: 'POST',
//                 headers: {
//                     'Content-Type': 'application/x-www-form-urlencoded',
//                 }
//             })
//                 .then(response => {
//                     if (response.ok) {
//                         return response.json();
//                     }
//                     throw new Error('Ошибка при добавлении в корзину');
//                 })
//                 .then(data => {
//                     // Показываем уведомление
//                     showNotification('Товар добавлен в корзину');
//
//                     // Обновляем счетчик товаров в корзине
//                     updateCartCounter(data.totalItems);
//
//                     // Анимация кнопки для визуального подтверждения
//                     animateCartButton(this);
//                 })
//                 .catch(error => {
//                     console.error('Ошибка:', error);
//                     showNotification('Произошла ошибка при добавлении в корзину');
//                 });
//         });
//     });
//
//     // Анимация кнопки добавления в корзину
//     function animateCartButton(button) {
//         button.classList.add('added');
//
//         // Временно меняем текст кнопки
//         const originalText = button.textContent;
//         button.textContent = 'Добавлено';
//
//         // Возвращаем исходное состояние через 1.5 секунды
//         setTimeout(() => {
//             button.classList.remove('added');
//             button.textContent = originalText;
//         }, 1500);
//     }
//
//     // Обновляет счетчик товаров в корзине
//     function updateCartCounter(count) {
//         const counters = document.querySelectorAll('.cart-counter');
//         counters.forEach(counter => {
//             counter.textContent = count;
//
//             // Если счетчик равен 0, можно скрыть его
//             if (count > 0) {
//                 counter.classList.remove('hidden');
//             } else {
//                 counter.classList.add('hidden');
//             }
//         });
//     }
//
//     // Функция показа уведомлений (такая же, как в favorites.js)
//     function showNotification(message) {
//         // Проверяем, существует ли уже уведомление, если да - удаляем его
//         const existingNotification = document.querySelector('.notification');
//         if (existingNotification) {
//             existingNotification.remove();
//         }
//
//         // Создаем новое уведомление
//         const notification = document.createElement('div');
//         notification.className = 'notification';
//         notification.innerHTML = message;
//         document.body.appendChild(notification);
//
//         // Анимация появления
//         notification.style.opacity = '0';
//         notification.style.transform = 'translateY(20px)';
//
//         setTimeout(() => {
//             notification.style.opacity = '1';
//             notification.style.transform = 'translateY(0)';
//         }, 10);
//
//         // Удаляем уведомление через 3 секунды
//         setTimeout(() => {
//             notification.style.opacity = '0';
//             notification.style.transform = 'translateY(20px)';
//             setTimeout(() => {
//                 notification.remove();
//             }, 300);
//         }, 3000);
//     }
//
//     // Загружаем количество товаров в корзине при загрузке страницы
//     fetch('/cart/api/count')
//         .then(response => response.json())
//         .then(data => {
//             updateCartCounter(data.count);
//         })
//         .catch(error => {
//             console.error('Ошибка при получении количества товаров в корзине:', error);
//         });
// });


/**
 * cart.js - Скрипт для работы с корзиной
 *
 * Данный скрипт отвечает за функциональность корзины:
 * - Добавление товаров в корзину
 * - Обновление счетчиков корзины
 * - Обработка уведомлений
 *
 * Подключите этот скрипт на все страницы, где используются
 * кнопки добавления в корзину (.add-to-cart)
 */
document.addEventListener('DOMContentLoaded', function() {
    // Обработка кнопок "Добавить в корзину"
    const cartButtons = document.querySelectorAll('.add-to-cart');
    console.log('Найдено кнопок:', cartButtons.length);

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