(function() {
    'use strict';

    const contextPath = document.body.getAttribute('data-context-path') || '';

    // ----- Configuration -----
    const CONFIG = {
        logUpdateInterval: 5000,    // 5 seconds
        particleCount: 50,
        maxLogEntries: 50
    };

    // ----- State -----
    let logUpdateTimer = null;

    // ============================================
    // 1. PARTICLE SYSTEM
    // ============================================
    function initParticles() {
        const container = document.getElementById('particles');
        if (!container) return;

        for (let i = 0; i < CONFIG.particleCount; i++) {
            const particle = document.createElement('div');
            particle.className = 'particle';
            const size = Math.random() * 3 + 1;
            particle.style.width = size + 'px';
            particle.style.height = size + 'px';
            particle.style.left = Math.random() * 100 + '%';
            particle.style.animationDuration = (Math.random() * 15 + 10) + 's';
            particle.style.animationDelay = (Math.random() * 10) + 's';
            particle.style.opacity = Math.random() * 0.3 + 0.1;

            const colors = ['#ff3333', '#ff6666', '#ff8888', '#ffaa44', '#00ff88'];
            particle.style.background = colors[Math.floor(Math.random() * colors.length)];

            container.appendChild(particle);
        }
    }

    // ============================================
    // 2. LIVE CLOCK
    // ============================================
    function updateClock() {
        const clockElement = document.getElementById('liveTime');
        if (!clockElement) return;

        const now = new Date();
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        const seconds = String(now.getSeconds()).padStart(2, '0');
        clockElement.textContent = hours + ':' + minutes + ':' + seconds;
    }

    // ============================================
    // 3. ACTIVITY LOG
    // ============================================
    function escapeHtml(str) {
        const div = document.createElement('div');
        div.appendChild(document.createTextNode(str));
        return div.innerHTML;
    }

    function addLogEntry(message, type = 'info') {
        const logContainer = document.getElementById('adminActivityLog');
        if (!logContainer) {
            // Silently return if container doesn't exist (manage-products page)
            return;
        }

        const now = new Date();
        const time = now.toTimeString().split(' ')[0];

        const entry = document.createElement('div');
        entry.className = 'log-entry';

        const badgeMap = {
            info: 'System',
            success: 'Success',
            warning: 'Warning',
            danger: 'Error'
        };

        entry.innerHTML = `
        <span class="log-time">${time}</span>
        <span class="log-badge ${type}">${badgeMap[type] || 'System'}</span>
        <span class="log-message">${escapeHtml(message)}</span>
    `;

        logContainer.insertBefore(entry, logContainer.firstChild);

        // Remove old entries
        while (logContainer.children.length > CONFIG.maxLogEntries) {
            logContainer.removeChild(logContainer.lastChild);
        }
    }

    function simulateActivity() {
        const actions = [
            'Admin logged in',
            'Product catalog updated',
            'New order received',
            'Inventory sync completed',
            'Customer account modified',
            'System health check passed',
            'Security scan completed',
            'Database backup initiated',
            'Product stock updated',
            'User session created'
        ];

        const types = ['info', 'success', 'warning', 'info'];
        const randomAction = actions[Math.floor(Math.random() * actions.length)];
        const randomType = types[Math.floor(Math.random() * types.length)];

        addLogEntry(randomAction, randomType);
    }

    // ============================================
    // 4. NOTIFICATION SYSTEM
    // ============================================
    function showNotification(message, title = 'Notification', type = 'info', duration = 4000) {
        const container = document.getElementById('notificationContainer');
        if (!container) {
            console.warn('Notification container not found');
            return;
        }

        const notification = document.createElement('div');
        notification.className = `notification ${type}`;

        notification.innerHTML = `
            <button class="notify-close" onclick="this.parentElement.remove()">×</button>
            <div class="notify-title">${escapeHtml(title)}</div>
            <div class="notify-message">${escapeHtml(message)}</div>
        `;

        container.appendChild(notification);

        setTimeout(() => {
            if (notification.parentElement) {
                notification.style.opacity = '0';
                notification.style.transform = 'translateX(40px)';
                setTimeout(() => notification.remove(), 300);
            }
        }, duration);
    }

    // ============================================
    // 5. ACTION CARDS - Fixed to not override links
    // ============================================
    function initActionCards() {
        document.querySelectorAll('.action-card').forEach(card => {
            // Only add visual effects on hover
            card.addEventListener('mouseenter', function() {
                const glow = this.querySelector('.action-glow');
                if (glow) {
                    glow.style.transform = 'scale(1.5)';
                    glow.style.opacity = '0.5';
                }
            });

            card.addEventListener('mouseleave', function() {
                const glow = this.querySelector('.action-glow');
                if (glow) {
                    glow.style.transform = 'scale(1)';
                    glow.style.opacity = '0.3';
                }
            });

            // Add click ripple effect but ONLY if click is not on a link or button
            card.addEventListener('click', function(e) {
                // Check if the click target is or is inside an <a> or <button> tag
                const target = e.target;
                const isLink = target.closest('a') !== null;
                const isButton = target.closest('button') !== null;

                // If clicked on a link or button, don't add ripple (let the navigation happen)
                if (isLink || isButton) {
                    return;
                }

                // Add ripple effect only for clicks on the card itself (not on links/buttons)
                const ripple = document.createElement('div');
                ripple.style.cssText = `
                position: absolute;
                border-radius: 50%;
                background: rgba(255, 51, 51, 0.2);
                width: 100px;
                height: 100px;
                left: ${e.clientX - this.getBoundingClientRect().left - 50}px;
                top: ${e.clientY - this.getBoundingClientRect().top - 50}px;
                pointer-events: none;
                transform: scale(0);
                animation: ripple-anim 0.6s ease-out forwards;
            `;
                this.appendChild(ripple);
                setTimeout(() => ripple.remove(), 600);
            });
        });
    }

    // ============================================
    // 6. PRODUCT FORMS
    // ============================================
    function initProductForms() {
        // Add Product Form
        const addForm = document.getElementById('addProductForm');
        if (addForm) {
            const fileInput = addForm.querySelector('#imageFile');
            const fileNameSpan = addForm.querySelector('#fileName');
            if (fileInput && fileNameSpan) {
                fileInput.addEventListener('change', function() {
                    if (this.files && this.files[0]) {
                        fileNameSpan.textContent = this.files[0].name;
                        fileNameSpan.style.color = '#66ff88';
                    } else {
                        fileNameSpan.textContent = 'No file selected';
                        fileNameSpan.style.color = '#666666';
                    }
                });
            }

            addForm.addEventListener('submit', function(e) {
                const categoryId = document.getElementById('categoryId');
                const name = document.getElementById('name');
                const brand = document.getElementById('brand');
                const basePrice = document.getElementById('basePrice');

                if (!categoryId || !categoryId.value) {
                    e.preventDefault();
                    showNotification('Please select a category', 'Validation Error', 'warning');
                    if (categoryId) categoryId.focus();
                    return false;
                }

                if (!name || !name.value.trim()) {
                    e.preventDefault();
                    showNotification('Please enter a product name', 'Validation Error', 'warning');
                    if (name) name.focus();
                    return false;
                }

                if (!brand || !brand.value.trim()) {
                    e.preventDefault();
                    showNotification('Please enter a brand', 'Validation Error', 'warning');
                    if (brand) brand.focus();
                    return false;
                }

                if (!basePrice || !basePrice.value || parseFloat(basePrice.value) <= 0) {
                    e.preventDefault();
                    showNotification('Please enter a valid price', 'Validation Error', 'warning');
                    if (basePrice) basePrice.focus();
                    return false;
                }

                const submitBtn = this.querySelector('.btn-primary');
                if (submitBtn) {
                    submitBtn.innerHTML = '<span class="btn-icon">⏳</span> Adding...';
                    submitBtn.disabled = true;
                }
            });
        }

        // Update Product Form
        const updateForm = document.getElementById('updateProductForm');
        if (updateForm) {
            const fileInput = updateForm.querySelector('#imageFile');
            const fileNameSpan = updateForm.querySelector('#fileName');
            if (fileInput && fileNameSpan) {
                fileInput.addEventListener('change', function() {
                    if (this.files && this.files[0]) {
                        fileNameSpan.textContent = this.files[0].name;
                        fileNameSpan.style.color = '#66ff88';
                    } else {
                        fileNameSpan.textContent = 'Leave empty to keep current';
                        fileNameSpan.style.color = '#666666';
                    }
                });
            }

            updateForm.addEventListener('submit', function(e) {
                const name = document.getElementById('name');
                const brand = document.getElementById('brand');
                const basePrice = document.getElementById('basePrice');

                if (!name || !name.value.trim()) {
                    e.preventDefault();
                    showNotification('Please enter a product name', 'Validation Error', 'warning');
                    if (name) name.focus();
                    return false;
                }

                if (!brand || !brand.value.trim()) {
                    e.preventDefault();
                    showNotification('Please enter a brand', 'Validation Error', 'warning');
                    if (brand) brand.focus();
                    return false;
                }

                if (!basePrice || !basePrice.value || parseFloat(basePrice.value) <= 0) {
                    e.preventDefault();
                    showNotification('Please enter a valid price', 'Validation Error', 'warning');
                    if (basePrice) basePrice.focus();
                    return false;
                }

                const submitBtn = this.querySelector('.btn-primary');
                if (submitBtn) {
                    submitBtn.innerHTML = '<span class="btn-icon">⏳</span> Updating...';
                    submitBtn.disabled = true;
                }
            });
        }

        // Delete Product Form
        const deleteForm = document.getElementById('deleteProductForm');
        if (deleteForm) {
            const confirmCheck = document.getElementById('confirmCheck');
            const deleteBtn = document.getElementById('deleteBtn');

            if (confirmCheck && deleteBtn) {
                confirmCheck.addEventListener('change', function() {
                    deleteBtn.disabled = !this.checked;
                    deleteBtn.style.opacity = this.checked ? '1' : '0.5';
                });
            }

            deleteForm.addEventListener('submit', function(e) {
                if (!confirmCheck || !confirmCheck.checked) {
                    e.preventDefault();
                    showNotification('Please confirm deletion', 'Confirmation Required', 'warning');
                    return false;
                }

                if (deleteBtn) {
                    deleteBtn.innerHTML = '<span class="btn-icon">⏳</span> Deleting...';
                    deleteBtn.disabled = true;
                }

                if (!confirm('Are you absolutely sure you want to delete this product permanently?')) {
                    e.preventDefault();
                    if (deleteBtn) {
                        deleteBtn.innerHTML = '<span class="btn-icon">🗑️</span> Permanently Delete Product';
                        deleteBtn.disabled = false;
                    }
                    return false;
                }
            });
        }
    }

    // ============================================
    // 7. KEYBOARD SHORTCUTS
    // ============================================
    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey && e.key === '1') {
            e.preventDefault();
            window.location.href = contextPath + '/metrics-page';
        }
        if (e.ctrlKey && e.key === 'r' && !e.shiftKey) {
            e.preventDefault();
            addLogEntry('Manual refresh triggered', 'info');
            showNotification('Dashboard refreshed!', 'Refresh', 'success', 2000);
        }
    });

    // ============================================
    // 8. INITIALIZATION
    // ============================================
    function init() {
        console.log('🚀 TechMart Admin Panel Initialized');
        console.log('📌 Shortcuts: Ctrl+1 (Metrics), Ctrl+R (Refresh)');
        console.log('📌 Click on any action card to navigate');

        // Initialize particles
        initParticles();

        // Start clock
        updateClock();
        setInterval(updateClock, 1000);

        // Initial log entries
        addLogEntry('Admin panel initialized successfully', 'success');
        addLogEntry('System ready for operations', 'info');
        addLogEntry('Connected to database', 'success');

        // Simulate activity every 5 seconds
        logUpdateTimer = setInterval(simulateActivity, CONFIG.logUpdateInterval);

        // Initialize action cards (visual effects only)
        initActionCards();

        // Initialize product forms
        initProductForms();

        // Expose notification function globally
        window.showNotification = showNotification;

        // Add more log entries after a moment
        setTimeout(() => {
            addLogEntry('Loading system configuration', 'info');
        }, 1000);

        setTimeout(() => {
            addLogEntry('System health: All systems operational', 'success');
        }, 2000);

        setTimeout(() => {
            addLogEntry('Security scan completed: No threats detected', 'success');
        }, 3000);
    }

    // ============================================
    // 9. CLEANUP
    // ============================================
    window.addEventListener('beforeunload', function() {
        if (logUpdateTimer) clearInterval(logUpdateTimer);
    });

    // Add ripple animation dynamically
    const styleSheet = document.createElement('style');
    styleSheet.textContent = `
        @keyframes ripple-anim {
            0% { transform: scale(0); opacity: 1; }
            100% { transform: scale(4); opacity: 0; }
        }
    `;
    document.head.appendChild(styleSheet);

    // Start when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }

    // Add warehouse row dynamically
    function addWarehouseRow() {
        const container = document.getElementById('additionalWarehouses');
        if (!container) return;

        const rowCount = container.children.length + 1;
        const row = document.createElement('div');
        row.className = 'warehouse-row';
        row.innerHTML = `
        <button type="button" class="btn-remove" onclick="this.parentElement.remove()">✕</button>
        <div class="inventory-grid">
            <div class="form-group">
                <label for="warehouseId_${rowCount}">Select Warehouse *</label>
                <select id="warehouseId_${rowCount}" name="warehouseIds" required>
                    <option value="">Select Warehouse</option>
                    ${document.getElementById('warehouseId').innerHTML}
                </select>
            </div>
            <div class="form-group">
                <label for="stockQuantity_${rowCount}">Stock Quantity</label>
                <input type="number" id="stockQuantity_${rowCount}" name="stockQuantities"
                       min="0" value="0" placeholder="0">
            </div>
            <div class="form-group">
                <label for="reorderThreshold_${rowCount}">Reorder Threshold</label>
                <input type="number" id="reorderThreshold_${rowCount}" name="reorderThresholds"
                       min="1" value="5" placeholder="5">
            </div>
        </div>
    `;
        container.appendChild(row);
    }

})();