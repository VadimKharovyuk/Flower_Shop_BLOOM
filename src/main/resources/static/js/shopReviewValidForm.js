 // JavaScript для отображения выбранного файла
    document.getElementById('file').addEventListener('change', function() {
    var fileName = this.files[0] ? this.files[0].name : '';
    var selectedFileElement = document.getElementById('selected-file-name');

    if (fileName) {
    selectedFileElement.textContent = 'Выбрано: ' + fileName;
    selectedFileElement.style.display = 'block';
} else {
    selectedFileElement.style.display = 'none';
}
});

    // Синхронизация звезд рейтинга с select-полем
    const ratingInputs = document.querySelectorAll('.rating-stars input');
    const ratingSelect = document.getElementById('rating');

    ratingInputs.forEach(input => {
    input.addEventListener('change', function() {
        ratingSelect.value = this.value;
    });
});

    // Проверка наличия параметров в URL, которые могут указывать на возврат после ошибки
    window.addEventListener('DOMContentLoaded', function() {
    // Функция для получения параметров из URL
    function getUrlParameter(name) {
        name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
        var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
        var results = regex.exec(location.search);
        return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
    }

    // Установка значений полей из localStorage, если они были сохранены
    if (localStorage.getItem('reviewFormData')) {
    try {
    const formData = JSON.parse(localStorage.getItem('reviewFormData'));

    if (formData.userName) document.getElementById('userName').value = formData.userName;
    if (formData.city) document.getElementById('city').value = formData.city;
    if (formData.shoutDescription) document.getElementById('shoutDescription').value = formData.shoutDescription;
    if (formData.rating) {
    document.getElementById('rating').value = formData.rating;
    if (formData.rating) {
    document.getElementById('star' + formData.rating).checked = true;
}
}

    // Очищаем данные после использования
    localStorage.removeItem('reviewFormData');
} catch (e) {
    console.error('Ошибка восстановления данных формы:', e);
}
}

    // Проверка наличия параметра error в URL
    const hasError = getUrlParameter('error');
    if (hasError === 'validation') {
    const errorAlert = document.getElementById('js-validation-alert');
    errorAlert.style.display = 'flex';

    // Если есть сохраненные ошибки, отображаем их
    if (localStorage.getItem('validationErrors')) {
    try {
    const errors = JSON.parse(localStorage.getItem('validationErrors'));
    const errorsList = document.getElementById('js-validation-errors');

    // Очищаем список
    errorsList.innerHTML = '';

    // Добавляем ошибки в список
    errors.forEach(error => {
    const li = document.createElement('li');
    li.textContent = error;
    errorsList.appendChild(li);
});

    // Подсвечиваем поля с ошибками
    if (errors.some(e => e.includes('Имя'))) {
    document.getElementById('userName').classList.add('error-marker');
    document.querySelector('.error-userName').textContent = errors.find(e => e.includes('Имя'));
    document.querySelector('.error-userName').style.display = 'block';
}

    if (errors.some(e => e.includes('Краткое описание'))) {
    document.getElementById('shoutDescription').classList.add('error-marker');
    document.querySelector('.error-shoutDescription').textContent = errors.find(e => e.includes('Краткое описание'));
    document.querySelector('.error-shoutDescription').style.display = 'block';
}

    if (errors.some(e => e.includes('Оценка'))) {
    document.getElementById('rating').classList.add('error-marker');
    document.querySelector('.error-rating').textContent = errors.find(e => e.includes('Оценка'));
    document.querySelector('.error-rating').style.display = 'block';
}

    // Очищаем ошибки после использования
    localStorage.removeItem('validationErrors');
} catch (e) {
    console.error('Ошибка восстановления данных об ошибках:', e);
}
}
}
});

    // Клиентская валидация перед отправкой формы
    document.getElementById('review-form').addEventListener('submit', function(event) {
    let hasError = false;
    const errors = [];

    // Очищаем предыдущие ошибки
    document.querySelectorAll('.error').forEach(el => {
    el.textContent = '';
    el.style.display = 'none';
});

    document.querySelectorAll('input, textarea, select').forEach(el => {
    el.classList.remove('error-marker');
});

    document.getElementById('js-validation-alert').style.display = 'none';

    const userName = document.getElementById('userName').value.trim();
    const shoutDescription = document.getElementById('shoutDescription').value.trim();
    const rating = document.getElementById('rating').value;
    const city = document.getElementById('city').value.trim();

    // Проверка имени пользователя
    if (!userName) {
    const errorMsg = 'Имя пользователя не может быть пустым';
    errors.push(errorMsg);
    document.getElementById('userName').classList.add('error-marker');
    document.querySelector('.error-userName').textContent = errorMsg;
    document.querySelector('.error-userName').style.display = 'block';
    hasError = true;
} else if (userName.length < 2 || userName.length > 50) {
    const errorMsg = 'Имя должно содержать от 2 до 50 символов';
    errors.push(errorMsg);
    document.getElementById('userName').classList.add('error-marker');
    document.querySelector('.error-userName').textContent = errorMsg;
    document.querySelector('.error-userName').style.display = 'block';
    hasError = true;
}

    // Проверка краткого описания
    if (!shoutDescription) {
    const errorMsg = 'Краткое описание не может быть пустым';
    errors.push(errorMsg);
    document.getElementById('shoutDescription').classList.add('error-marker');
    document.querySelector('.error-shoutDescription').textContent = errorMsg;
    document.querySelector('.error-shoutDescription').style.display = 'block';
    hasError = true;
} else if (shoutDescription.length < 5 || shoutDescription.length > 60) {
    const errorMsg = 'Краткое описание должно содержать от 5 до 60 символов';
    errors.push(errorMsg);
    document.getElementById('shoutDescription').classList.add('error-marker');
    document.querySelector('.error-shoutDescription').textContent = errorMsg;
    document.querySelector('.error-shoutDescription').style.display = 'block';
    hasError = true;
}

    // Проверка рейтинга
    if (!rating) {
    const errorMsg = 'Оценка не может быть пустой';
    errors.push(errorMsg);
    document.querySelector('.rating-stars').classList.add('error-marker');
    document.querySelector('.error-rating').textContent = errorMsg;
    document.querySelector('.error-rating').style.display = 'block';
    hasError = true;
}

    if (hasError) {
    event.preventDefault();

    // Отображаем общее сообщение об ошибке
    const errorAlert = document.getElementById('js-validation-alert');
    const errorsList = document.getElementById('js-validation-errors');

    // Очищаем список
    errorsList.innerHTML = '';

    // Добавляем ошибки в список
    errors.forEach(error => {
    const li = document.createElement('li');
    li.textContent = error;
    errorsList.appendChild(li);
});

    errorAlert.style.display = 'flex';

    // Скроллим к сообщению об ошибке
    errorAlert.scrollIntoView({ behavior: 'smooth', block: 'start' });
} else {
    // Сохраняем данные формы в localStorage перед отправкой
    const formData = {
    userName: userName,
    city: city,
    shoutDescription: shoutDescription,
    rating: rating
};

    localStorage.setItem('reviewFormData', JSON.stringify(formData));
}
});
