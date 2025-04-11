// // Простой скрипт для отображения таймера обратного отсчета
// document.addEventListener('DOMContentLoaded', function() {
//     // Можно заменить на актуальную дату окончания акции
//     const countdownElement = document.querySelector('.timer-countdown');
//
//     if (countdownElement) {
//         // Обновление таймера каждую секунду
//         setInterval(function() {
//             // Здесь должна быть логика для расчета оставшегося времени
//             // В этом примере мы просто уменьшаем секунды
//             let timeparts = countdownElement.textContent.split(':');
//             let hours = parseInt(timeparts[0]);
//             let minutes = parseInt(timeparts[1]);
//             let seconds = parseInt(timeparts[2]);
//
//             seconds--;
//
//             if (seconds < 0) {
//                 seconds = 59;
//                 minutes--;
//             }
//
//             if (minutes < 0) {
//                 minutes = 59;
//                 hours--;
//             }
//
//             if (hours < 0) {
//                 hours = 23; // Сбрасываем на 24 часа
//             }
//
//             // Форматируем числа, чтобы всегда были две цифры
//             const formatNumber = (num) => num < 10 ? '0' + num : num;
//
//             countdownElement.innerHTML = `<span>${formatNumber(hours)}</span>:<span>${formatNumber(minutes)}</span>:<span>${formatNumber(seconds)}</span>`;
//         }, 1000);
//     }
// });

document.addEventListener('DOMContentLoaded', function() {
    const countdownElements = document.querySelectorAll('.timer-countdown');

    countdownElements.forEach(element => {
        // Получаем дату окончания из атрибута данных
        const endDateStr = element.getAttribute('data-end-date') || '';
        if (!endDateStr) return;

        const endDate = new Date(endDateStr);

        // Обновляем счетчик каждую секунду
        const timer = setInterval(function() {
            const now = new Date();
            const diff = endDate - now;

            // Если время истекло
            if (diff <= 0) {
                clearInterval(timer);
                element.innerHTML = '<span>00</span>:<span>00</span>:<span>00</span>';

                // Опционально: показываем сообщение о завершении акции
                const expiredMessage = element.closest('.timer-container')?.querySelector('.timer-expired');
                if (expiredMessage) {
                    expiredMessage.style.display = 'block';
                }
                return;
            }

            // Рассчитываем оставшееся время
            const days = Math.floor(diff / (1000 * 60 * 60 * 24));
            const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((diff % (1000 * 60)) / 1000);

            // Форматируем числа
            const formatNumber = (num) => num < 10 ? '0' + num : num;

            // Обновляем отображение
            if (days > 0) {
                element.innerHTML = `${days} дней ${formatNumber(hours)}:${formatNumber(minutes)}:${formatNumber(seconds)}`;
            } else {
                element.innerHTML = `<span>${formatNumber(hours)}</span>:<span>${formatNumber(minutes)}</span>:<span>${formatNumber(seconds)}</span>`;
            }
        }, 1000);
    });
});