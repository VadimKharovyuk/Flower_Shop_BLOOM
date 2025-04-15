/**
 * custom-bouquet.js
 * Скрипт для работы с формой заказа индивидуального букета
 *
 * Этот скрипт обрабатывает:
 * - Отправку формы заказа индивидуального букета
 * - Валидацию полей формы
 * - Маскирование телефонного номера
 * - Проверку статуса заявки
 * - Отображение результатов и уведомлений
 */

document.addEventListener('DOMContentLoaded', function() {
    // Основные DOM-элементы
    const customBouquetForm = document.getElementById('customBouquetForm');
    const formResult = document.getElementById('formResult');
    const statusTracker = document.getElementById('statusTracker');
    const checkStatusBtn = document.getElementById('checkStatusBtn');
    const statusResult = document.getElementById('statusResult');

    // Инициализация маски для телефонных полей
    initPhoneMasks();

    // Обработка отправки формы заказа
    if (customBouquetForm) {
        customBouquetForm.addEventListener('submit', handleFormSubmit);
    }

    // Обработка кнопки проверки статуса
    if (checkStatusBtn) {
        checkStatusBtn.addEventListener('click', handleStatusCheck);
    }

    /**
     * Инициализирует маски для всех телефонных полей
     */
    function initPhoneMasks() {
        const phoneInputs = document.querySelectorAll('input[type="tel"]');
        phoneInputs.forEach(input => {
            input.addEventListener('input', formatPhoneNumber);
        });
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
            statusResult.innerHTML = '<p class="error-text">Пожалуйста, введите номер заявки и телефон</p>';
            statusResult.style.display = 'block';
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

                // Очищаем форму и показываем блок отслеживания
                customBouquetForm.style.display = 'none';
                statusTracker.style.display = 'block';

                // Заполняем поля отслеживания
                document.getElementById('trackingId').value = data.id;
                document.getElementById('trackingPhone').value = document.getElementById('phone').value;
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
        fetch(`/api/custom-bouquet/request/status?id=${id}&phone=${phone}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Заявка не найдена');
                }
                return response.json();
            })
            .then(data => {
                statusResult.innerHTML = `
                <div class="status-info">
                    <p>Статус вашей заявки: <strong>${data.statusDisplayName}</strong></p>
                </div>
            `;
                statusResult.style.display = 'block';
            })
            .catch(error => {
                console.error('Ошибка:', error);
                statusResult.innerHTML = '<p class="error-text">Заявка не найдена или указан неверный телефон</p>';
                statusResult.style.display = 'block';
            });
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