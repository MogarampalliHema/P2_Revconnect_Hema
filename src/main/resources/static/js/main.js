// RevConnect - Main JavaScript
// Handles AJAX interactions, like toggles, auto-dismiss alerts, and UI polish

document.addEventListener('DOMContentLoaded', function() {
    // Auto-dismiss alerts
    document.querySelectorAll('.alert').forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.5s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 500);
        }, 4000);
    });

    // Character counter for post textarea
    const postTextarea = document.getElementById('postContent');
    const charCounter = document.getElementById('charCounter');
    if (postTextarea && charCounter) {
        postTextarea.addEventListener('input', function() {
            const count = this.value.length;
            charCounter.textContent = count + ' / 500';
            charCounter.style.color = count > 450 ? '#ef4444' : '#94a3b8';
        });
    }

    // Hashtag auto-extract helper
    const contentField = document.getElementById('postContent');
    const hashtagField = document.getElementById('hashtagField');
    if (contentField && hashtagField) {
        contentField.addEventListener('blur', function() {
            const matches = this.value.match(/#\w+/g);
            if (matches) {
                const tags = [...new Set(matches)].join(', ');
                if (!hashtagField.value) hashtagField.value = tags;
            }
        });
    }

    // Toggle advanced post options (scheduler, CTA)
    const advancedToggle = document.getElementById('advancedToggle');
    const advancedOptions = document.getElementById('advancedOptions');
    if (advancedToggle && advancedOptions) {
        advancedToggle.addEventListener('click', function() {
            const isHidden = advancedOptions.style.display === 'none' || !advancedOptions.style.display;
            advancedOptions.style.display = isHidden ? 'block' : 'none';
            this.textContent = isHidden ? '▲ Less options' : '▼ More options';
        });
    }

    // Like button AJAX toggle (for single post view)
    document.querySelectorAll('.like-form').forEach(form => {
        form.addEventListener('submit', function(e) {
            const btn = this.querySelector('.action-btn');
            if (btn) {
                btn.classList.toggle('liked');
                const countSpan = btn.querySelector('.like-count');
                if (countSpan) {
                    let count = parseInt(countSpan.textContent) || 0;
                    countSpan.textContent = btn.classList.contains('liked') ? count + 1 : Math.max(0, count - 1);
                }
            }
        });
    });

    // Confirm before delete
    document.querySelectorAll('.confirm-delete').forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!confirm('Are you sure you want to delete this?')) {
                e.preventDefault();
            }
        });
    });

    // Mobile nav toggle
    const navToggle = document.getElementById('navToggle');
    const mobileMenu = document.getElementById('mobileMenu');
    if (navToggle && mobileMenu) {
        navToggle.addEventListener('click', function() {
            mobileMenu.classList.toggle('open');
        });
    }

    // Smooth scroll to top button
    const topBtn = document.getElementById('scrollTopBtn');
    if (topBtn) {
        window.addEventListener('scroll', () => {
            topBtn.style.display = window.scrollY > 400 ? 'flex' : 'none';
        });
        topBtn.addEventListener('click', () => window.scrollTo({ top: 0, behavior: 'smooth' }));
    }
});
