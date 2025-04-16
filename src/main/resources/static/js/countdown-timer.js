// Файл: static/js/countdown-timer.js

/**
 * Инициализирует таймеры обратного отсчета на странице
 */
function initCountdownTimers() {
    // Находим все элементы с обратным отсчетом
    const countdownElements = document.querySelectorAll('.timer-countdown');

    // Для каждого элемента устанавливаем таймер
    countdownElements.forEach(function(element) {
        const endDate = new Date(element.getAttribute('data-end-date'));

        // Функция обновления таймера
        function updateTimer() {
            const now = new Date();
            const diff = endDate - now;

            if (diff <= 0) {
                element.innerHTML = '<span class="expired">Акция завершена</span>';
                return;
            }

            // Расчет оставшегося времени
            const hours = Math.floor(diff / (1000 * 60 * 60));
            const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((diff % (1000 * 60)) / 1000);

            // Обновляем отображение
            element.querySelector('.hours').textContent = hours.toString().padStart(2, '0');
            element.querySelector('.minutes').textContent = minutes.toString().padStart(2, '0');
            element.querySelector('.seconds').textContent = seconds.toString().padStart(2, '0');
        }

        // Обновляем таймер сразу
        updateTimer();

        // Запускаем обновление каждую секунду
        setInterval(updateTimer, 1000);
    });
}

// Запускаем инициализацию таймеров при загрузке страницы
document.addEventListener('DOMContentLoaded', initCountdownTimers);