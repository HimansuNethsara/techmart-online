(function() {
    'use strict';

    const contextPath = document.body.getAttribute('data-context-path') || '';

    // === SEARCH FUNCTIONALITY ===
    function searchProducts() {
        const searchInput = document.getElementById('searchInput');
        if (!searchInput) return;

        const searchTerm = searchInput.value.toLowerCase().trim();
        const rows = document.querySelectorAll('#productTableBody tr');
        let hasResults = false;

        rows.forEach(row => {
            const name = row.getAttribute('data-name')?.toLowerCase() || '';
            const brand = row.getAttribute('data-brand')?.toLowerCase() || '';
            const match = name.includes(searchTerm) || brand.includes(searchTerm);
            row.style.display = match ? '' : 'none';
            if (match) hasResults = true;
        });

        const emptyState = document.getElementById('emptyState');
        const productTable = document.getElementById('productTable');
        if (emptyState) emptyState.style.display = hasResults ? 'none' : 'block';
        if (productTable) productTable.style.display = hasResults ? '' : 'none';
    }

    function clearSearch() {
        const searchInput = document.getElementById('searchInput');
        if (searchInput) searchInput.value = '';

        const rows = document.querySelectorAll('#productTableBody tr');
        rows.forEach(row => row.style.display = '');

        const emptyState = document.getElementById('emptyState');
        const productTable = document.getElementById('productTable');
        if (emptyState) emptyState.style.display = 'none';
        if (productTable) productTable.style.display = '';
    }

    // === STOCK UPDATE MODAL ===
    let currentProductId = null;
    let currentProductName = null;
    let currentStock = null;

    function openStockModal(productId, productName, stock) {
        currentProductId = productId;
        currentProductName = productName;
        currentStock = stock;

        const modalProductName = document.getElementById('modalProductName');
        const modalCurrentStock = document.getElementById('modalCurrentStock');
        const modalStockQuantity = document.getElementById('modalStockQuantity');
        const stockModal = document.getElementById('stockModal');

        if (modalProductName) modalProductName.textContent = productName;
        if (modalCurrentStock) modalCurrentStock.textContent = stock;
        if (modalStockQuantity) modalStockQuantity.value = stock;
        if (stockModal) stockModal.style.display = 'block';
        if (modalStockQuantity) modalStockQuantity.focus();
    }

    function closeStockModal() {
        const stockModal = document.getElementById('stockModal');
        if (stockModal) stockModal.style.display = 'none';
        currentProductId = null;
        currentProductName = null;
        currentStock = null;
    }

    function updateStock() {
        if (!currentProductId) {
            showNotification('No product selected', 'Error', 'danger');
            return;
        }

        const modalStockQuantity = document.getElementById('modalStockQuantity');
        if (!modalStockQuantity) return;

        const newStock = parseInt(modalStockQuantity.value);

        if (isNaN(newStock) || newStock < 0) {
            showNotification('Please enter a valid stock quantity', 'Validation Error', 'warning');
            return;
        }

        // Show loading state
        const updateBtn = document.querySelector('#stockModal .btn-primary');
        const originalText = updateBtn ? updateBtn.innerHTML : '';
        if (updateBtn) {
            updateBtn.innerHTML = '⏳ Updating...';
            updateBtn.disabled = true;
        }

        fetch(contextPath + '/admin/update-stock', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'productId=' + currentProductId + '&stockQuantity=' + newStock
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showNotification('Stock updated successfully!', 'Success', 'success');
                    // Update the table row
                    const row = document.querySelector(`#productTableBody tr[data-product-id="${currentProductId}"]`);
                    if (row) {
                        const stockCell = row.querySelector('.stock-badge');
                        if (stockCell) {
                            stockCell.textContent = newStock + ' units';
                            stockCell.className = 'stock-badge ' + (newStock <= 5 ? 'stock-low' : 'stock-ok');
                        }
                    }
                    closeStockModal();
                } else {
                    showNotification(data.message || 'Failed to update stock', 'Error', 'danger');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification('Error updating stock', 'Error', 'danger');
            })
            .finally(() => {
                if (updateBtn) {
                    updateBtn.innerHTML = originalText;
                    updateBtn.disabled = false;
                }
            });
    }

    // === TOGGLE PRODUCT STATUS ===
    let deleteProductId = null;
    let deleteProductName = null;
    let deleteProductActive = null;

    function toggleProductStatus(productId, productName, isActive) {
        deleteProductId = productId;
        deleteProductName = productName;
        deleteProductActive = isActive;

        const deleteProductNameEl = document.getElementById('deleteProductName');
        const statusAction = document.getElementById('statusAction');
        const statusWarning = document.getElementById('statusWarning');
        const statusIcon = document.getElementById('statusIcon');
        const actionBtnText = document.getElementById('actionBtnText');
        const deleteConfirmCheck = document.getElementById('deleteConfirmCheck');
        const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
        const deleteModal = document.getElementById('deleteModal');

        if (isActive) {
            // Currently active -> going to deactivate
            if (statusAction) statusAction.textContent = 'deactivate';
            if (statusWarning) statusWarning.textContent = 'This will hide the product from the catalog. You can activate it later.';
            if (statusIcon) statusIcon.textContent = '🔴';
            if (actionBtnText) actionBtnText.textContent = '🔴 Deactivate Product';
            if (confirmDeleteBtn) {
                confirmDeleteBtn.className = 'btn btn-danger';
                confirmDeleteBtn.style.background = 'linear-gradient(135deg, #ff3333, #cc0000)';
            }
        } else {
            // Currently inactive -> going to activate
            if (statusAction) statusAction.textContent = 'activate';
            if (statusWarning) statusWarning.textContent = 'This will make the product visible in the catalog again.';
            if (statusIcon) statusIcon.textContent = '🟢';
            if (actionBtnText) actionBtnText.textContent = '🟢 Activate Product';
            if (confirmDeleteBtn) {
                confirmDeleteBtn.className = 'btn btn-success';
                confirmDeleteBtn.style.background = 'linear-gradient(135deg, #00cc44, #009933)';
            }
        }

        if (deleteProductNameEl) deleteProductNameEl.textContent = productName;
        if (deleteConfirmCheck) deleteConfirmCheck.checked = false;
        if (confirmDeleteBtn) confirmDeleteBtn.disabled = true;
        if (deleteModal) deleteModal.style.display = 'block';
    }

    function closeDeleteModal() {
        const deleteModal = document.getElementById('deleteModal');
        if (deleteModal) deleteModal.style.display = 'none';
        deleteProductId = null;
        deleteProductName = null;
        deleteProductActive = null;
    }

    function confirmDelete() {
        if (!deleteProductId) {
            showNotification('No product selected', 'Error', 'danger');
            return;
        }

        const deleteConfirmCheck = document.getElementById('deleteConfirmCheck');
        if (!deleteConfirmCheck || !deleteConfirmCheck.checked) {
            showNotification('Please confirm the action', 'Confirmation Required', 'warning');
            return;
        }

        const deleteBtn = document.getElementById('confirmDeleteBtn');
        const originalText = deleteBtn ? deleteBtn.innerHTML : '';
        if (deleteBtn) {
            deleteBtn.innerHTML = '⏳ Processing...';
            deleteBtn.disabled = true;
        }

        fetch(contextPath + '/admin/delete-product', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'productId=' + deleteProductId
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const action = data.active ? 'activated' : 'deactivated';
                    showNotification('Product ' + action + ' successfully!', 'Success', 'success');

                    // Update the table row
                    const row = document.querySelector(`#productTableBody tr[data-product-id="${deleteProductId}"]`);
                    if (row) {
                        // Update status badge
                        const statusBadge = row.querySelector('.status-badge');
                        if (statusBadge) {
                            if (data.active) {
                                statusBadge.textContent = 'Active';
                                statusBadge.className = 'status-badge status-active';
                            } else {
                                statusBadge.textContent = 'Inactive';
                                statusBadge.className = 'status-badge status-inactive';
                            }
                        }

                        // Update action button
                        const actionBtn = row.querySelector('.btn-activate, .btn-deactivate');
                        if (actionBtn) {
                            if (data.active) {
                                actionBtn.textContent = '🔴 Deactivate';
                                actionBtn.className = 'btn btn-sm btn-deactivate';
                                actionBtn.title = 'Deactivate Product';
                                actionBtn.setAttribute('onclick',
                                    `toggleProductStatus(${deleteProductId}, '${deleteProductName}', true)`);
                            } else {
                                actionBtn.textContent = '🟢 Activate';
                                actionBtn.className = 'btn btn-sm btn-activate';
                                actionBtn.title = 'Activate Product';
                                actionBtn.setAttribute('onclick',
                                    `toggleProductStatus(${deleteProductId}, '${deleteProductName}', false)`);
                            }
                        }
                    }
                    closeDeleteModal();
                } else {
                    showNotification(data.message || 'Failed to update product status', 'Error', 'danger');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification('Error updating product status', 'Error', 'danger');
            })
            .finally(() => {
                if (deleteBtn) {
                    deleteBtn.innerHTML = originalText;
                    deleteBtn.disabled = false;
                }
            });
    }

    // === MODAL CLOSE ON CLICK OUTSIDE ===
    window.onclick = function(event) {
        const stockModal = document.getElementById('stockModal');
        const deleteModal = document.getElementById('deleteModal');
        if (event.target == stockModal) {
            closeStockModal();
        }
        if (event.target == deleteModal) {
            closeDeleteModal();
        }
    };

    // === KEYBOARD SHORTCUTS ===
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Enter') {
            const activeModal = document.querySelector('.modal[style*="display: block"]');
            if (activeModal) {
                if (activeModal.id === 'stockModal') {
                    updateStock();
                } else if (activeModal.id === 'deleteModal') {
                    confirmDelete();
                }
            } else {
                const searchInput = document.getElementById('searchInput');
                if (document.activeElement === searchInput) {
                    searchProducts();
                }
            }
        }
        if (e.key === 'Escape') {
            closeStockModal();
            closeDeleteModal();
        }
    });

    // === DELETE CONFIRMATION CHECKBOX ===
    document.addEventListener('DOMContentLoaded', function() {
        const confirmCheck = document.getElementById('deleteConfirmCheck');
        const confirmBtn = document.getElementById('confirmDeleteBtn');
        if (confirmCheck && confirmBtn) {
            confirmCheck.addEventListener('change', function() {
                confirmBtn.disabled = !this.checked;
            });
        }

        // Initialize search on input change with debounce
        const searchInput = document.getElementById('searchInput');
        let searchTimeout;
        if (searchInput) {
            searchInput.addEventListener('input', function() {
                clearTimeout(searchTimeout);
                searchTimeout = setTimeout(searchProducts, 300);
            });
        }

        // Stock quantity validation
        const stockInput = document.getElementById('modalStockQuantity');
        if (stockInput) {
            stockInput.addEventListener('blur', function() {
                if (this.value && parseInt(this.value) < 0) {
                    this.value = 0;
                }
            });
        }
    });

    // === EXPOSE FUNCTIONS GLOBALLY ===
    window.searchProducts = searchProducts;
    window.clearSearch = clearSearch;
    window.openStockModal = openStockModal;
    window.closeStockModal = closeStockModal;
    window.updateStock = updateStock;
    window.toggleProductStatus = toggleProductStatus;
    window.closeDeleteModal = closeDeleteModal;
    window.confirmDelete = confirmDelete;

})();