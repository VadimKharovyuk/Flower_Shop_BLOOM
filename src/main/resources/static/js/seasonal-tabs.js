/**
 * Скрипт для управления переключением сезонных вкладок
 * Обрабатывает клики на вкладки и отображает соответствующее содержимое
 */
document.addEventListener('DOMContentLoaded', function() {
    // Находим все элементы вкладок и контентных блоков
    const seasonTabs = document.querySelectorAll('.season-tab');
    const seasonContents = document.querySelectorAll('.season-content');

    // Если на странице есть вкладки сезонов
    if (seasonTabs.length && seasonContents.length) {
        // Устанавливаем обработчики событий для каждой вкладки
        seasonTabs.forEach(tab => {
            tab.addEventListener('click', function() {
                // Удаляем класс active со всех вкладок
                seasonTabs.forEach(t => t.classList.remove('active'));

                // Добавляем класс active на выбранную вкладку
                this.classList.add('active');

                // Скрываем содержимое всех сезонов
                seasonContents.forEach(content => content.classList.remove('active'));

                // Показываем содержимое выбранного сезона
                const season = this.getAttribute('data-season');
                const targetContent = document.getElementById(season);

                if (targetContent) {
                    targetContent.classList.add('active');
                }
            });
        });
    }
});