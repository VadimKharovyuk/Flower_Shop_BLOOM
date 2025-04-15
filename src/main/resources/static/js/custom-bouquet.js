// /**
//  * custom-bouquet.js
//  * Скрипт для работы с формой заказа индивидуального букета
//  *
//  * Этот скрипт обрабатывает:
//  * - Отправку формы заказа индивидуального букета
//  * - Валидацию полей формы
//  * - Маскирование телефонного номера
//  * - Проверку статуса заявки
//  * - Отображение результатов и уведомлений
//  */
//
// document.addEventListener('DOMContentLoaded', function() {
//     // Основные DOM-элементы
//     const customBouquetForm = document.getElementById('customBouquetForm');
//     const formResult = document.getElementById('formResult');
//     const statusTracker = document.getElementById('statusTracker');
//     const checkStatusBtn = document.getElementById('checkStatusBtn');
//     const statusResult = document.getElementById('statusResult');
//
//     // Инициализация маски для телефонных полей
//     initPhoneMasks();
//
//     // Обработка отправки формы заказа
//     if (customBouquetForm) {
//         customBouquetForm.addEventListener('submit', handleFormSubmit);
//     }
//
//     // Обработка кнопки проверки статуса
//     if (checkStatusBtn) {
//         checkStatusBtn.addEventListener('click', handleStatusCheck);
//     }
//
//     /**
//      * Инициализирует маски для всех телефонных полей
//      */
//     function initPhoneMasks() {
//         const phoneInputs = document.querySelectorAll('input[type="tel"]');
//         phoneInputs.forEach(input => {
//             input.addEventListener('input', formatPhoneNumber);
//         });
//     }
//
//     /**
//      * Форматирует телефонный номер в формате +7 (XXX) XXX-XX-XX
//      */
//     function formatPhoneNumber(e) {
//         // Удаляем все нецифровые символы
//         let value = e.target.value.replace(/\D/g, '');
//
//         // Если первая цифра не 7, добавляем её
//         if (value.length > 0 && value[0] !== '7') {
//             value = '7' + value;
//         }
//
//         // Форматируем номер по маске
//         let formattedValue = '';
//         if (value.length > 0) {
//             formattedValue = '+' + value[0];
//         }
//         if (value.length > 1) {
//             formattedValue += ' (' + value.substring(1, 4);
//         }
//         if (value.length > 4) {
//             formattedValue += ') ' + value.substring(4, 7);
//         }
//         if (value.length > 7) {
//             formattedValue += '-' + value.substring(7, 9);
//         }
//         if (value.length > 9) {
//             formattedValue += '-' + value.substring(9, 11);
//         }
//
//         e.target.value = formattedValue;
//     }
//
//     /**
//      * Обрабатывает отправку формы
//      */
//     function handleFormSubmit(e) {
//         e.preventDefault();
//
//         // Валидация формы перед отправкой
//         if (!validateForm()) {
//             return;
//         }
//
//         // Собираем данные формы
//         const formData = {
//             occasion: document.getElementById('occasion').value,
//             budgetRange: document.getElementById('budget').value,
//             preferences: document.getElementById('preferences').value,
//             customerName: document.getElementById('name').value,
//             phone: document.getElementById('phone').value.replace(/\D/g, '')
//         };
//
//         // Отправляем запрос на создание заявки
//         sendCustomBouquetRequest(formData);
//     }
//
//     /**
//      * Проверяет статус существующей заявки
//      */
//     function handleStatusCheck() {
//         const trackingId = document.getElementById('trackingId').value;
//         const trackingPhone = document.getElementById('trackingPhone').value.replace(/\D/g, '');
//
//         if (!trackingId || !trackingPhone) {
//             statusResult.innerHTML = '<p class="error-text">Пожалуйста, введите номер заявки и телефон</p>';
//             statusResult.style.display = 'block';
//             return;
//         }
//
//         // Отправляем запрос на проверку статуса
//         checkRequestStatus(trackingId, trackingPhone);
//     }
//
//     /**
//      * Валидирует форму заказа
//      * @returns {boolean} - Результат валидации
//      */
//     function validateForm() {
//         let isValid = true;
//
//         // Проверка поля "Повод"
//         const occasion = document.getElementById('occasion');
//         if (occasion.value === '' || occasion.selectedIndex === 0) {
//             highlightInvalid(occasion);
//             isValid = false;
//         } else {
//             removeHighlight(occasion);
//         }
//
//         // Проверка поля "Бюджет"
//         const budget = document.getElementById('budget');
//         if (budget.value === '' || budget.selectedIndex === 0) {
//             highlightInvalid(budget);
//             isValid = false;
//         } else {
//             removeHighlight(budget);
//         }
//
//         // Проверка поля "Пожелания"
//         const preferences = document.getElementById('preferences');
//         if (preferences.value.trim() === '') {
//             highlightInvalid(preferences);
//             isValid = false;
//         } else {
//             removeHighlight(preferences);
//         }
//
//         // Проверка поля "Имя"
//         const name = document.getElementById('name');
//         if (name.value.trim() === '') {
//             highlightInvalid(name);
//             isValid = false;
//         } else {
//             removeHighlight(name);
//         }
//
//         // Проверка поля "Телефон"
//         const phone = document.getElementById('phone');
//         if (phone.value.trim() === '' || !isValidPhone(phone.value)) {
//             highlightInvalid(phone);
//             isValid = false;
//         } else {
//             removeHighlight(phone);
//         }
//
//         return isValid;
//     }
//
//     /**
//      * Проверяет корректность формата телефона
//      * @param {string} phone - Телефонный номер для проверки
//      * @returns {boolean} - Результат проверки
//      */
//     function isValidPhone(phone) {
//         // Минимальная проверка - номер должен содержать не менее 10 цифр
//         return phone.replace(/\D/g, '').length >= 11;
//     }
//
//     /**
//      * Подсвечивает невалидное поле
//      * @param {HTMLElement} element - Поле для подсветки
//      */
//     function highlightInvalid(element) {
//         element.classList.add('invalid');
//     }
//
//     /**
//      * Убирает подсветку невалидного поля
//      * @param {HTMLElement} element - Поле для очистки подсветки
//      */
//     function removeHighlight(element) {
//         element.classList.remove('invalid');
//     }
//
//     /**
//      * Отправляет запрос на создание индивидуального букета
//      * @param {Object} formData - Данные формы
//      */
//     function sendCustomBouquetRequest(formData) {
//         fetch('/api/custom-bouquet/request', {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json',
//             },
//             body: JSON.stringify(formData)
//         })
//             .then(response => {
//                 if (!response.ok) {
//                     throw new Error('Ошибка при отправке заявки');
//                 }
//                 return response.json();
//             })
//             .then(data => {
//                 // Показываем сообщение об успехе
//                 showSuccessMessage(data);
//
//                 // Очищаем форму и показываем блок отслеживания
//                 customBouquetForm.style.display = 'none';
//                 statusTracker.style.display = 'block';
//
//                 // Заполняем поля отслеживания
//                 document.getElementById('trackingId').value = data.id;
//                 document.getElementById('trackingPhone').value = document.getElementById('phone').value;
//             })
//             .catch(error => {
//                 console.error('Ошибка:', error);
//                 showErrorMessage();
//             });
//     }
//
//     /**
//      * Проверяет статус заявки
//      * @param {number} id - ID заявки
//      * @param {string} phone - Телефон клиента
//      */
//     function checkRequestStatus(id, phone) {
//         fetch(`/api/custom-bouquet/request/status?id=${id}&phone=${phone}`)
//             .then(response => {
//                 if (!response.ok) {
//                     throw new Error('Заявка не найдена');
//                 }
//                 return response.json();
//             })
//             .then(data => {
//                 statusResult.innerHTML = `
//                 <div class="status-info">
//                     <p>Статус вашей заявки: <strong>${data.statusDisplayName}</strong></p>
//                 </div>
//             `;
//                 statusResult.style.display = 'block';
//             })
//             .catch(error => {
//                 console.error('Ошибка:', error);
//                 statusResult.innerHTML = '<p class="error-text">Заявка не найдена или указан неверный телефон</p>';
//                 statusResult.style.display = 'block';
//             });
//     }
//
//     /**
//      * Показывает сообщение об успешной отправке заявки
//      * @param {Object} data - Данные созданной заявки
//      */
//     function showSuccessMessage(data) {
//         formResult.innerHTML = `
//             <div class="success-message">
//                 <h3>Ваша заявка успешно отправлена!</h3>
//                 <p>Номер вашей заявки: <strong>${data.id}</strong></p>
//                 <p>Наши флористы свяжутся с вами в ближайшее время для уточнения деталей.</p>
//                 <p>Вы можете отслеживать статус вашей заявки, используя номер заявки и ваш телефон.</p>
//             </div>
//         `;
//         formResult.style.display = 'block';
//     }
//
//     /**
//      * Показывает сообщение об ошибке при отправке заявки
//      */
//     function showErrorMessage() {
//         formResult.innerHTML = `
//             <div class="error-message">
//                 <h3>Произошла ошибка</h3>
//                 <p>К сожалению, не удалось отправить вашу заявку. Пожалуйста, попробуйте еще раз позже или свяжитесь с нами по телефону.</p>
//             </div>
//         `;
//         formResult.style.display = 'block';
//     }
// });

/**
 * custom-bouquet.js - Обновленная версия
 * Скрипт для работы с формой заказа индивидуального букета и отслеживания статуса
 */

document.addEventListener('DOMContentLoaded', function() {
    // DOM-элементы
    const customBouquetForm = document.getElementById('customBouquetForm');
    const formResult = document.getElementById('formResult');
    const checkStatusBtn = document.getElementById('checkStatusBtn');
    const statusResult = document.getElementById('statusResult');
    const statusHistory = document.getElementById('statusHistory');
    const modeBtns = document.querySelectorAll('.mode-btn');
    const modeContents = document.querySelectorAll('.mode-content');

    // Инициализация
    initModeSwitching();
    initPhoneMasks();
    initFormSubmission();
    initStatusChecking();

    /**
     * Инициализирует переключение между режимами (создание/отслеживание)
     */
    function initModeSwitching() {
        modeBtns.forEach(btn => {
            btn.addEventListener('click', function() {
                const mode = this.getAttribute('data-mode');

                // Переключаем активную кнопку
                modeBtns.forEach(b => b.classList.remove('active'));
                this.classList.add('active');

                // Переключаем содержимое
                modeContents.forEach(content => {
                    content.style.display = 'none';
                });

                document.getElementById(mode + 'Mode').style.display = 'block';

                // Сбрасываем результаты
                formResult.style.display = 'none';
                statusResult.style.display = 'none';
                statusHistory.style.display = 'none';
            });
        });
    }

    /**
     * Инициализирует маски для телефонных полей
     */
    function initPhoneMasks() {
        const phoneInputs = document.querySelectorAll('input[type="tel"]');
        phoneInputs.forEach(input => {
            input.addEventListener('input', formatPhoneNumber);
        });
    }

    /**
     * Инициализирует отправку формы заказа
     */
    function initFormSubmission() {
        if (customBouquetForm) {
            customBouquetForm.addEventListener('submit', handleFormSubmit);
        }
    }

    /**
     * Инициализирует проверку статуса заявки
     */
    function initStatusChecking() {
        if (checkStatusBtn) {
            checkStatusBtn.addEventListener('click', handleStatusCheck);
        }
    }

    /**
     * Форматирует телефонный номер в формате +7 (XXX) XXX-XX-XX
     */
    function formatPhoneNumber(e) {
        // Удаляем все нецифровые символы
        let value = e.target.value.replace(/\D/g, '');

        // Если первая цифра не 7, добавляем её
        if (value.length > 0 && value[0] !== '7') {
            value = '7' + value;
        }

        // Форматируем номер по маске
        let formattedValue = '';
        if (value.length > 0) {
            formattedValue = '+' + value[0];
        }
        if (value.length > 1) {
            formattedValue += ' (' + value.substring(1, 4);
        }
        if (value.length > 4) {
            formattedValue += ') ' + value.substring(4, 7);
        }
        if (value.length > 7) {
            formattedValue += '-' + value.substring(7, 9);
        }
        if (value.length > 9) {
            formattedValue += '-' + value.substring(9, 11);
        }

        e.target.value = formattedValue;
    }

    /**
     * Обрабатывает отправку формы
     */
    function handleFormSubmit(e) {
        e.preventDefault();

        // Валидация формы перед отправкой
        if (!validateForm()) {
            return;
        }

        // Собираем данные формы
        const formData = {
            occasion: document.getElementById('occasion').value,
            budgetRange: document.getElementById('budget').value,
            preferences: document.getElementById('preferences').value,
            customerName: document.getElementById('name').value,
            phone: document.getElementById('phone').value.replace(/\D/g, '')
        };

        // Отправляем запрос на создание заявки
        sendCustomBouquetRequest(formData);
    }

    /**
     * Проверяет статус существующей заявки
     */
    function handleStatusCheck() {
        const trackingId = document.getElementById('trackingId').value;
        const trackingPhone = document.getElementById('trackingPhone').value.replace(/\D/g, '');

        if (!trackingId || !trackingPhone) {
            showStatusError('Пожалуйста, введите номер заявки и телефон');
            return;
        }

        // Отправляем запрос на проверку статуса
        checkRequestStatus(trackingId, trackingPhone);
    }

    /**
     * Валидирует форму заказа
     * @returns {boolean} - Результат валидации
     */
    function validateForm() {
        let isValid = true;

        // Проверка поля "Повод"
        const occasion = document.getElementById('occasion');
        if (occasion.value === '' || occasion.selectedIndex === 0) {
            highlightInvalid(occasion);
            isValid = false;
        } else {
            removeHighlight(occasion);
        }

        // Проверка поля "Бюджет"
        const budget = document.getElementById('budget');
        if (budget.value === '' || budget.selectedIndex === 0) {
            highlightInvalid(budget);
            isValid = false;
        } else {
            removeHighlight(budget);
        }

        // Проверка поля "Пожелания"
        const preferences = document.getElementById('preferences');
        if (preferences.value.trim() === '') {
            highlightInvalid(preferences);
            isValid = false;
        } else {
            removeHighlight(preferences);
        }

        // Проверка поля "Имя"
        const name = document.getElementById('name');
        if (name.value.trim() === '') {
            highlightInvalid(name);
            isValid = false;
        } else {
            removeHighlight(name);
        }

        // Проверка поля "Телефон"
        const phone = document.getElementById('phone');
        if (phone.value.trim() === '' || !isValidPhone(phone.value)) {
            highlightInvalid(phone);
            isValid = false;
        } else {
            removeHighlight(phone);
        }

        return isValid;
    }

    /**
     * Проверяет корректность формата телефона
     * @param {string} phone - Телефонный номер для проверки
     * @returns {boolean} - Результат проверки
     */
    function isValidPhone(phone) {
        // Минимальная проверка - номер должен содержать не менее 10 цифр
        return phone.replace(/\D/g, '').length >= 11;
    }

    /**
     * Подсвечивает невалидное поле
     * @param {HTMLElement} element - Поле для подсветки
     */
    function highlightInvalid(element) {
        element.classList.add('invalid');
    }

    /**
     * Убирает подсветку невалидного поля
     * @param {HTMLElement} element - Поле для очистки подсветки
     */
    function removeHighlight(element) {
        element.classList.remove('invalid');
    }

    /**
     * Отправляет запрос на создание индивидуального букета
     * @param {Object} formData - Данные формы
     */
    function sendCustomBouquetRequest(formData) {
        fetch('/api/custom-bouquet/request', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка при отправке заявки');
                }
                return response.json();
            })
            .then(data => {
                // Показываем сообщение об успехе
                showSuccessMessage(data);

                // Очищаем форму и заполняем поле отслеживания
                customBouquetForm.reset();

                // Автоматически переключаем на режим отслеживания
                document.querySelector('[data-mode="track"]').click();

                // Заполняем поля отслеживания
                document.getElementById('trackingId').value = data.id;
                document.getElementById('trackingPhone').value = document.getElementById('phone').value;

                // Автоматически проверяем статус
                setTimeout(() => {
                    document.getElementById('checkStatusBtn').click();
                }, 500);
            })
            .catch(error => {
                console.error('Ошибка:', error);
                showErrorMessage();
            });
    }

    /**
     * Проверяет статус заявки
     * @param {number} id - ID заявки
     * @param {string} phone - Телефон клиента
     */
    function checkRequestStatus(id, phone) {
        // Показываем индикатор загрузки
        statusResult.innerHTML = '<div class="loading-spinner"></div>';
        statusResult.style.display = 'block';
        statusHistory.style.display = 'none';

        fetch(`/api/custom-bouquet/request/status?id=${id}&phone=${phone}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Заявка не найдена');
                }
                return response.json();
            })
            .then(data => {
                showStatusResult(data);
                // Создаем историю статусов (заглушка)
                generateStatusHistory(data);
            })
            .catch(error => {
                console.error('Ошибка:', error);
                showStatusError('Заявка не найдена или указан неверный телефон');
            });
    }

    /**
     * Показывает результат проверки статуса
     * @param {Object} data - Данные о статусе заявки
     */
    function showStatusResult(data) {
        // Определяем иконку и описание для текущего статуса
        const statusInfo = getStatusInfo(data.status);

        statusResult.innerHTML = `
            <div class="status-current">
                <div class="status-icon">
                    <i class="${statusInfo.icon}"></i>
                </div>
                <div class="status-info">
                    <div class="status-name">${data.statusDisplayName}</div>
                    <div class="status-description">${statusInfo.description}</div>
                </div>
            </div>
            <button type="button" class="view-history-toggle">
                Показать историю статусов <i class="fas fa-chevron-down"></i>
            </button>
        `;

        statusResult.style.display = 'block';

        // Обработчик для кнопки "Показать историю"
        const historyToggle = statusResult.querySelector('.view-history-toggle');
        historyToggle.addEventListener('click', function() {
            if (statusHistory.style.display === 'none') {
                statusHistory.style.display = 'block';
                this.innerHTML = 'Скрыть историю статусов <i class="fas fa-chevron-up"></i>';
                this.classList.add('active');
            } else {
                statusHistory.style.display = 'none';
                this.innerHTML = 'Показать историю статусов <i class="fas fa-chevron-down"></i>';
                this.classList.remove('active');
            }
        });
    }

    /**
     * Показывает сообщение об ошибке при проверке статуса
     * @param {string} message - Текст сообщения
     */
    function showStatusError(message) {
        statusResult.innerHTML = `
            <div class="status-error">
                <i class="fas fa-exclamation-circle"></i>
                <p>${message}</p>
            </div>
        `;
        statusResult.style.display = 'block';
        statusHistory.style.display = 'none';
    }

    /**
     * Генерирует историю статусов (заглушка)
     * @param {Object} data - Данные о статусе заявки
     */
    function generateStatusHistory(data) {
        // В реальной системе здесь будет запрос к API для получения истории статусов

        // Создаем заглушку с текущим статусом
        const statusInfo = getStatusInfo(data.status);
        const currentDate = new Date().toLocaleDateString('ru-RU');

        statusHistory.innerHTML = `
            <div class="accordion">
                <div class="accordion-item active">
                    <div class="accordion-header">
                        <span class="status-date">${currentDate}</span>
                        <span class="status-badge ${getStatusClass(data.status)}">${data.statusDisplayName}</span>
                        <span class="accordion-toggle"><i class="fas fa-chevron-down"></i></span>
                    </div>
                    <div class="accordion-content" style="padding: 15px; max-height: 200px;">
                        <p>${statusInfo.description}</p>
                    </div>
                </div>
                
                <!-- Дополнительные статусы (заглушка) -->
                <div class="accordion-item">
                    <div class="accordion-header">
                        <span class="status-date">${getPreviousDate(1)}</span>
                        <span class="status-badge status-new">Новая заявка</span>
                        <span class="accordion-toggle"><i class="fas fa-chevron-down"></i></span>
                    </div>
                    <div class="accordion-content">
                        <p>Ваша заявка принята и будет обработана в ближайшее время.</p>
                    </div>
                </div>
            </div>
        `;

        // Добавляем обработчики для аккордеона
        const accordionHeaders = statusHistory.querySelectorAll('.accordion-header');
        accordionHeaders.forEach(header => {
            header.addEventListener('click', function() {
                const accordionItem = this.parentElement;
                accordionItem.classList.toggle('active');
            });
        });
    }

    /**
     * Получает информацию о статусе
     * @param {string} status - Код статуса
     * @returns {Object} - Объект с информацией о статусе
     */
    function getStatusInfo(status) {
        const statusMap = {
            'NEW': {
                icon: 'fas fa-file-alt',
                description: 'Ваша заявка принята и будет обработана в ближайшее время.'
            },
            'PROCESSING': {
                icon: 'fas fa-spinner',
                description: 'Мы обрабатываем вашу заявку и скоро свяжемся с вами.'
            },
            'DESIGN_STAGE': {
                icon: 'fas fa-palette',
                description: 'Наши флористы разрабатывают дизайн вашего букета.'
            },
            'AWAITING_APPROVAL': {
                icon: 'fas fa-hourglass-half',
                description: 'Дизайн букета готов и ожидает вашего подтверждения.'
            },
            'CONFIRMED': {
                icon: 'fas fa-check-circle',
                description: 'Дизайн букета подтвержден и передан в производство.'
            },
            'IN_PRODUCTION': {
                icon: 'fas fa-cut',
                description: 'Ваш букет находится на этапе сборки.'
            },
            'READY': {
                icon: 'fas fa-box',
                description: 'Ваш букет готов к выдаче или доставке.'
            },
            'DELIVERED': {
                icon: 'fas fa-truck',
                description: 'Ваш букет доставлен. Спасибо за заказ!'
            },
            'COMPLETED': {
                icon: 'fas fa-flag-checkered',
                description: 'Заказ успешно выполнен. Будем рады видеть вас снова!'
            },
            'CANCELLED': {
                icon: 'fas fa-ban',
                description: 'Заказ был отменен. Если у вас есть вопросы, пожалуйста, свяжитесь с нами.'
            }
        };

        return statusMap[status] || {
            icon: 'fas fa-question-circle',
            description: 'Статус вашего заказа обновлен.'
        };
    }

    /**
     * Возвращает CSS-класс для статуса
     * @param {string} status - Код статуса
     * @returns {string} - CSS-класс
     */
    function getStatusClass(status) {
        const classMap = {
            'NEW': 'status-new',
            'PROCESSING': 'status-processing',
            'DESIGN_STAGE': 'status-design',
            'AWAITING_APPROVAL': 'status-awaiting',
            'CONFIRMED': 'status-confirmed',
            'IN_PRODUCTION': 'status-production',
            'READY': 'status-ready',
            'DELIVERED': 'status-delivered',
            'COMPLETED': 'status-completed',
            'CANCELLED': 'status-cancelled'
        };

        return classMap[status] || 'status-new';
    }

    /**
     * Получает дату, смещенную на заданное количество дней
     * @param {number} daysAgo - Количество дней смещения
     * @returns {string} - Строка даты в формате ДД.ММ.ГГГГ
     */
    function getPreviousDate(daysAgo) {
        const date = new Date();
        date.setDate(date.getDate() - daysAgo);
        return date.toLocaleDateString('ru-RU');
    }

    /**
     * Показывает сообщение об успешной отправке заявки
     * @param {Object} data - Данные созданной заявки
     */
    function showSuccessMessage(data) {
        formResult.innerHTML = `
            <div class="success-message">
                <h3>Ваша заявка успешно отправлена!</h3>
                <p>Номер вашей заявки: <strong>${data.id}</strong></p>
                <p>Наши флористы свяжутся с вами в ближайшее время для уточнения деталей.</p>
                <p>Вы можете отслеживать статус вашей заявки, используя номер заявки и ваш телефон.</p>
            </div>
        `;
        formResult.style.display = 'block';
    }

    /**
     * Показывает сообщение об ошибке при отправке заявки
     */
    function showErrorMessage() {
        formResult.innerHTML = `
            <div class="error-message">
                <h3>Произошла ошибка</h3>
                <p>К сожалению, не удалось отправить вашу заявку. Пожалуйста, попробуйте еще раз позже или свяжитесь с нами по телефону.</p>
            </div>
        `;
        formResult.style.display = 'block';
    }
});