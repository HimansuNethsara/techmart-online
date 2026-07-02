(function () {
    const POLL_INTERVAL_MS = 4000;

    function escapeHtml(str) {
        const div = document.createElement('div');
        div.appendChild(document.createTextNode(str));
        return div.innerHTML;
    }

    function renderActivity(data) {
        const list = document.getElementById('activity-list');
        if (!list) return;

        // noinspection JSUnresolvedVariable
        if (!data.notifications || data.notifications.length === 0) {
            list.innerHTML = '<div class="activity-empty">No activity yet.</div>';
            return;
        }

        let html = '';
        data.notifications.forEach(function (n) {
            html += '<div class="activity-item">' +
                '<span class="activity-time">' + escapeHtml(n.time) + '</span>' +
                '<span class="activity-type">' + escapeHtml(n.type) + '</span>' +
                escapeHtml(n.message) +
                '</div>';
        });
        list.innerHTML = html;
    }

    function pollActivity() {
        const base = document.body.getAttribute('data-context-path') || '';
        fetch(base + '/activity')
            .then(function (res) { return res.json(); })
            .then(renderActivity)
            .catch(function (err) { console.error('Activity poll failed:', err); });
    }

    // === HIGHLIGHT ACTIVE NAVIGATION LINK ===
    function highlightActiveNav() {
        const currentPath = window.location.pathname;
        const contextPath = document.body.getAttribute('data-context-path') || '';

        document.querySelectorAll('.navbar nav a').forEach(function(link) {
            const href = link.getAttribute('href');
            // Remove any existing active class
            link.classList.remove('active');

            // Check if the link's href matches the current path
            if (href === currentPath ||
                href === contextPath + currentPath.replace(contextPath, '')) {
                link.classList.add('active');
            }
        });
    }

    // === QUANTITY STEPPER BUTTONS ===
    function initQuantitySteppers() {
        document.querySelectorAll('.qty-decrease').forEach(function (btn) {
            btn.addEventListener('click', function () {
                const input = btn.parentElement.querySelector('input[type="number"]');
                if (input) {
                    let val = parseInt(input.value, 10) || 1;
                    input.value = Math.max(1, val - 1);
                }
            });
        });
        document.querySelectorAll('.qty-increase').forEach(function (btn) {
            btn.addEventListener('click', function () {
                const input = btn.parentElement.querySelector('input[type="number"]');
                if (input) {
                    let val = parseInt(input.value, 10) || 1;
                    input.value = val + 1;
                }
            });
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        // Initialize activity polling
        if (document.getElementById('activity-list')) {
            pollActivity();
            setInterval(pollActivity, POLL_INTERVAL_MS);
        }

        // Initialize quantity stepper buttons
        initQuantitySteppers();

        // Highlight active navigation link
        highlightActiveNav();
    });

})();