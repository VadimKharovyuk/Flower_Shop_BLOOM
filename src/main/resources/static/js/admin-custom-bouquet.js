/**
 * admin-custom-bouquet.js
 * Скрипт для управления заявками на индивидуальные букеты в админ-панели
 */

document.addEventListener('DOMContentLoaded', function() {
    // DOM-элементы
    const statusFilter = document.getElementById('statusFilter');
    const refreshButton = document.getElementById('refreshButton');
    const requestsTableBody = document.getElementById('requestsTableBody');
    const noRequestsMessage = document.getElementById('noRequestsMessage');
    const loadingIndicator = document.getElementById('loadingIndicator');
    const requestDetailsModal = document.getElementById('requestDetailsModal');
    const closeModalBtn = document.querySelector('.close');
    const updateStatusBtn = document.getElementById('updateStatusBtn');

    // ID текущей выбранной заявки
    let currentRequestId = null;

    // Инициализация страницы
    init();

    /**
     * Инициализация страницы
     */
    function init() {
        // Загружаем заявки при загрузке страницы
        loadRequests();

        // Добавляем обработчики событий
        addEventListeners();
    }

    /**
     * Добавляет слушатели событий к элементам интерфейса
     */
    function addEventListeners() {
        // Обработка изменения фильтра статуса
        statusFilter.addEventListener('change', function() {
            loadRequests();
        });

        // Обработка нажатия кнопки обновления
        refreshButton.addEventListener('click', function() {
            loadRequests();
        });

        // Закрытие модального окна
        closeModalBtn.addEventListener('click', function() {
            requestDetailsModal.style.display = 'none';
        });

        // Закрытие модального окна при клике вне его
        window.addEventListener('click', function(event) {
            if (event.target == requestDetailsModal) {
                requestDetailsModal.style.display = 'none';
            }
        });

        // Обновление статуса заявки
        updateStatusBtn.addEventListener('click', function() {
            if (currentRequestId) {
                updateRequestStatus();
            }
        });
    }

    /**
     * Загружает заявки из API с учетом выбранного фильтра
     */
    function loadRequests() {
        // Показываем индикатор загрузки
        loadingIndicator.style.display = 'flex';
        noRequestsMessage.style.display = 'none';
        requestsTableBody.innerHTML = '';

        // Формируем URL с учетом выбранного фильтра
        let url = '/api/custom-bouquet/admin/requests';
        if (statusFilter.value) {
            url = `/api/custom-bouquet/admin/requests/status/${statusFilter.value}`;
        }

        // Делаем запрос к API
        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка при загрузке данных');
                }
                return response.json();
            })
            .then(data => {
                // Скрываем индикатор загрузки
                loadingIndicator.style.display = 'none';

                // Если заявок нет, показываем сообщение
                if (!data || data.length === 0) {
                    noRequestsMessage.style.display = 'block';
                    return;
                }

                // Отображаем заявки в таблице
                renderRequestsTable(data);
            })
            .catch(error => {
                console.error('Ошибка:', error);
                loadingIndicator.style.display = 'none';
                noRequestsMessage.style.display = 'block';
                noRequestsMessage.querySelector('p').textContent = 'Ошибка при загрузке данных. Попробуйте позже.';
            });
    }

    /**
     * Отрисовывает таблицу с заявками
     * @param {Array} requests - Массив заявок
     */
    function renderRequestsTable(requests) {
        // Очищаем таблицу
        requestsTableBody.innerHTML = '';

        // Добавляем строки для каждой заявки
        requests.forEach(request => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${request.id}</td>
                <td>
                    <span class="status-badge ${getStatusClass(request.status)}">
                        ${request.status.displayName}
                    </span>
                </td>
                <td>${request.customerName}</td>
                <td>${formatPhone(request.phone)}</td>
                <td>${translateOccasion(request.occasion)}</td>
                <td>${request.budgetRange}</td>
                <td>${formatDate(request.createdAt)}</td>
                <td>
                    <div class="action-buttons">
                        <button class="table-btn view-btn" data-id="${request.id}">
                            <i class="fas fa-eye"></i> Подробнее
                        </button>
                    </div>
                </td>
            `;

            // Добавляем обработчик клика на кнопку "Подробнее"
            row.querySelector('.view-btn').addEventListener('click', function() {
                const requestId = this.getAttribute('data-id');
                openRequestDetails(requestId);
            });

            requestsTableBody.appendChild(row);
        });
    }

    /**
     * Открывает модальное окно с подробностями заявки
     * @param {string} requestId - ID заявки
     */
    function openRequestDetails(requestId) {
        currentRequestId = requestId;

        // Загружаем данные заявки
        fetch(`/api/custom-bouquet/admin/requests/${requestId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка при загрузке данных заявки');
                }
                return response.json();
            })
            .then(data => {
                // Заполняем модальное окно данными
                document.getElementById('modalRequestId').textContent = data.id;
                document.getElementById('modalCustomerName').textContent = data.customerName;
                document.getElementById('modalPhone').textContent = formatPhone(data.phone);
                document.getElementById('modalOccasion').textContent = translateOccasion(data.occasion);
                document.getElementById('modalBudget').textContent = data.budgetRange;
                document.getElementById('modalCreatedAt').textContent = formatDate(data.createdAt);
                document.getElementById('modalUpdatedAt').textContent = formatDate(data.updatedAt);
                document.getElementById('modalPreferences').textContent = data.preferences;
                document.getElementById('modalAdminComment').value = data.adminComment || '';

                // Устанавливаем выбранный статус
                const statusSelect = document.getElementById('statusSelect');
                statusSelect.value = data.status;

                // Показываем модальное окно
                requestDetailsModal.style.display = 'block';
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert('Не удалось загрузить данные заявки. Попробуйте позже.');
            });
    }

    /**
     * Обновляет статус заявки
     */
    function updateRequestStatus() {
        const newStatus = document.getElementById('statusSelect').value;
        const adminComment = document.getElementById('modalAdminComment').value;

        // Формируем URL с параметрами
        const url = `/api/custom-bouquet/admin/requests/${currentRequestId}/status?status=${newStatus}&adminComment=${encodeURIComponent(adminComment)}`;

        // Отправляем запрос на обновление статуса
        fetch(url, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Ошибка при обновлении статуса');
                }
                return response.json();
            })
            .then(data => {
                // Закрываем модальное окно
                requestDetailsModal.style.display = 'none';

                // Обновляем таблицу заявок
                loadRequests();

                // Показываем уведомление об успехе
                showNotification('Статус заявки успешно обновлен');
            })
            .catch(error => {
                console.error('Ошибка:', error);
                alert('Не удалось обновить статус заявки. Попробуйте позже.');
            });
    }

    /**
     * Показывает уведомление
     * @param {string} message - Текст уведомления
     */
    function showNotification(message) {
        // Проверяем, существует ли уже уведомление
        let notification = document.querySelector('.admin-notification');
        if (notification) {
            notification.remove();
        }

        // Создаем новое уведомление
        notification = document.createElement('div');
        notification.className = 'admin-notification';
        notification.innerHTML = `
            <div class="admin-notification-content">
                <i class="fas fa-check-circle"></i>
                <span>${message}</span>
            </div>
        `;
        document.body.appendChild(notification);

        // Анимируем появление
        setTimeout(() => {
            notification.classList.add('show');
        }, 10);

        // Удаляем уведомление через 3 секунды
        setTimeout(() => {
            notification.classList.remove('show');
            setTimeout(() => {
                notification.remove();
            }, 300);
        }, 3000);
    }

    /**
     * Возвращает CSS-класс для статуса заявки
     * @param {Object} status - Объект статуса заявки
     * @returns {string} - CSS-класс для статуса
     */
    function getStatusClass(status) {
        const statusMap = {
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

        return statusMap[status] || 'status-new';
    }

    /**
     * Форматирует номер телефона для отображения
     * @param {string} phone - Номер телефона
     * @returns {string} - Отформатированный номер
     */
    function formatPhone(phone) {
        if (!phone) return '';

        // Удаляем все нецифровые символы
        const digits = phone.replace(/\D/g, '');

        // Форматируем номер
        if (digits.length === 11) {
            return `+${digits[0]} (${digits.substring(1, 4)}) ${digits.substring(4, 7)}-${digits.substring(7, 9)}-${digits.substring(9, 11)}`;
        }

        return phone;
    }

    /**
     * Форматирует дату для отображения
     * @param {string} dateString - Строка с датой в формате ISO
     * @returns {string} - Отформатированная дата
     */
    function formatDate(dateString) {
        if (!dateString) return '';

        const date = new Date(dateString);
        return date.toLocaleString('ru-RU', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    /**
     * Переводит код повода на русский язык
     * @param {string} occasion - Код повода
     * @returns {string} - Название повода на русском
     */
    function translateOccasion(occasion) {
        const occasionMap = {
            'birthday': 'День рождения',
            'anniversary': 'Годовщина',
            'wedding': 'Свадьба',
            'date': 'Свидание',
            'corporate': 'Корпоративное событие',
            'other': 'Другое'
        };

        return occasionMap[occasion] || occasion;
    }
});