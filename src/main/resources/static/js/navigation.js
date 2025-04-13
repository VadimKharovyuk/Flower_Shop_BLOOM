document.addEventListener('DOMContentLoaded', function() {
    // Мобильное меню
    const mobileMenuBtn = document.querySelector('.mobile-menu-btn');
    const mobileMenu = document.querySelector('.mobile-menu');

    if(mobileMenuBtn && mobileMenu) {
        mobileMenuBtn.addEventListener('click', function() {
            this.classList.toggle('active');
            mobileMenu.classList.toggle('active');
        });
    }

    // Фиксированная навигация с эффектом скрытия/показа при прокрутке
    const siteHeader = document.querySelector('.site-header');
    let lastScrollTop = 0;

    window.addEventListener('scroll', function() {
        let scrollTop = window.pageYOffset || document.documentElement.scrollTop;

        if(scrollTop > lastScrollTop && scrollTop > 200) {
            siteHeader.classList.add('nav-hidden');
        } else {
            siteHeader.classList.remove('nav-hidden');
        }

        lastScrollTop = scrollTop;
    });
});