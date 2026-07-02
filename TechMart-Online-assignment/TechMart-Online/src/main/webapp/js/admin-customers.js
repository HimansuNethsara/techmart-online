(function() {
    'use strict';

    const contextPath = document.body.getAttribute('data-context-path') || '';

    // === SEARCH FUNCTIONALITY ===
    function searchCustomers() {
        const searchInput = document.getElementById('searchInput');
        if (!searchInput) return;

        const searchTerm = searchInput.value.trim();
        if (searchTerm) {
            window.location.href = contextPath + '/admin/customers?search=' + encodeURIComponent(searchTerm);
        } else {
            window.location.href = contextPath + '/admin/customers';
        }
    }

    function clearSearch() {
        window.location.href = contextPath + '/admin/customers';
    }

    // === KEYBOARD SHORTCUTS ===
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Enter') {
            const searchInput = document.getElementById('searchInput');
            if (document.activeElement === searchInput) {
                searchCustomers();
            }
        }
        if (e.key === 'Escape') {
            clearSearch();
        }
    });

    // === EXPOSE FUNCTIONS GLOBALLY ===
    window.searchCustomers = searchCustomers;
    window.clearSearch = clearSearch;

})();