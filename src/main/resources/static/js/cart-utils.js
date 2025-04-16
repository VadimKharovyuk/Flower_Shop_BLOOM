// cart-utils.js

function showNotification(message) {
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) existingNotification.remove();

    const notification = document.createElement('div');
    notification.className = 'notification';
    notification.innerHTML = message;
    document.body.appendChild(notification);

    notification.style.opacity = '0';
    notification.style.transform = 'translateY(20px)';
    setTimeout(() => {
        notification.style.opacity = '1';
        notification.style.transform = 'translateY(0)';
    }, 10);

    setTimeout(() => {
        notification.style.opacity = '0';
        notification.style.transform = 'translateY(20px)';
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

function updateCartCounter(count) {
    const counters = document.querySelectorAll('.cart-counter');
    counters.forEach(counter => {
        counter.textContent = count;
        counter.classList.toggle('hidden', count === 0);
    });
}

function animateCartButton(button) {
    button.classList.add('added');
    const originalText = button.textContent;
    button.textContent = 'Добавлено';
    setTimeout(() => {
        button.classList.remove('added');
        button.textContent = originalText;
    }, 1500);
}