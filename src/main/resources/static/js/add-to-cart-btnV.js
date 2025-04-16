document.querySelectorAll('.add-to-cart-btnV').forEach(button => {
    button.addEventListener('click', function () {
        const productId = this.getAttribute('data-id');
        const quantityInput = document.querySelector('.quantity-input');
        const quantity = quantityInput ? quantityInput.value : 1;

        if (!productId) {
            console.error('Не удалось найти ID товара для добавления в корзину');
            showNotification('Ошибка при добавлении товара в корзину');
            return;
        }

        fetch(`/cart/api/add?flowerId=${productId}&quantity=${quantity}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            }
        })
            .then(response => {
                if (response.ok) return response.json();
                throw new Error('Ошибка при добавлении в корзину');
            })
            .then(data => {
                showNotification('Товар добавлен в корзину');
                updateCartCounter(data.totalItems);
                animateCartButton(this);
            })
            .catch(error => {
                console.error('Ошибка:', error);
                showNotification('Произошла ошибка при добавлении в корзину');
            });
    });
});