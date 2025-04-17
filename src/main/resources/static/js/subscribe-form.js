document.addEventListener('DOMContentLoaded', function () {
    const subscribeForm = document.querySelector('.subscribe-form');

    if (subscribeForm) {
        subscribeForm.addEventListener('submit', function (event) {
            event.preventDefault();

            const emailInput = subscribeForm.querySelector('input[type="email"]');
            const email = emailInput.value.trim();

            if (!email) {
                showMessage(subscribeForm, 'Пожалуйста, введите email', 'error');
                return;
            }

            if (!isValidEmail(email)) {
                showMessage(subscribeForm, 'Пожалуйста, введите корректный email', 'error');
                return;
            }

            subscribeUser(email, subscribeForm);
        });
    }

    function isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    function subscribeUser(email, form) {
        const submitButton = form.querySelector('button[type="submit"]');
        const originalButtonText = submitButton.textContent;
        submitButton.disabled = true;
        submitButton.textContent = 'Отправка...';

        const data = { email };

        fetch('/api/subscriptions/subscribe', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (response.ok) {
                    return response.json().then(data => {
                        form.querySelector('input[type="email"]').value = '';
                        showMessage(form, 'Спасибо за подписку! Проверьте вашу почту.', 'success');
                        return data;
                    });
                } else {
                    return response.text().then(errorText => {
                        throw new Error(errorText || 'Произошла ошибка при подписке');
                    });
                }
            })
            .catch(error => {
                console.error('Ошибка:', error);
                showMessage(form, error.message, 'error');
            })
            .finally(() => {
                submitButton.disabled = false;
                submitButton.textContent = originalButtonText;
            });
    }

    function showMessage(form, text, type) {
        let messageElement = form.nextElementSibling;
        if (!messageElement || !messageElement.classList.contains('newsletter-message')) {
            messageElement = document.createElement('div');
            messageElement.className = 'newsletter-message';
            form.parentNode.insertBefore(messageElement, form.nextSibling);
        }

        messageElement.className = 'newsletter-message ' + type;
        messageElement.textContent = text;

        if (type === 'success') {
            setTimeout(() => {
                messageElement.style.opacity = '0';
                setTimeout(() => {
                    messageElement.remove();
                }, 500);
            }, 5000);
        }
    }
});