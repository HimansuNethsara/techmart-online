
(function () {
    var POLL_INTERVAL_MS = 3000;

    function escapeHtml(str) {
        var div = document.createElement('div');
        div.appendChild(document.createTextNode(String(str)));
        return div.innerHTML;
    }

    function renderSummary(data) {
        document.getElementById('m-total').textContent = data.totalRequests;
        document.getElementById('m-avg').textContent = data.averageResponseTimeMs;
        document.getElementById('m-slowest').textContent = data.slowestRequestMs;
        document.getElementById('m-errors').textContent = data.errorCount;
    }

    function renderEndpoints(endpoints) {
        var tbody = document.getElementById('endpoint-rows');
        if (!endpoints || endpoints.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="color:#6b7080;">No requests recorded yet.</td></tr>';
            return;
        }
        // Sort by request count, busiest endpoint first
        endpoints.sort(function (a, b) { return b.count - a.count; });

        var html = '';
        endpoints.forEach(function (e) {
            html += '<tr>' +
                '<td>' + escapeHtml(e.endpoint) + '</td>' +
                '<td>' + e.count + '</td>' +
                '<td>' + e.avgMs + '</td>' +
                '<td>' + e.maxMs + '</td>' +
                '</tr>';
        });
        tbody.innerHTML = html;
    }

    function renderRecent(recent) {
        var tbody = document.getElementById('recent-rows');
        if (!recent || recent.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" style="color:#6b7080;">No requests recorded yet.</td></tr>';
            return;
        }

        var html = '';
        recent.forEach(function (r) {
            var isError = r.statusCode >= 400;
            var pillClass = isError ? 'status-error' : 'status-ok';
            html += '<tr>' +
                '<td>' + escapeHtml(r.time) + '</td>' +
                '<td>' + escapeHtml(r.endpoint) + '</td>' +
                '<td>' + r.durationMs + '</td>' +
                '<td><span class="status-pill ' + pillClass + '">' + r.statusCode + '</span></td>' +
                '</tr>';
        });
        tbody.innerHTML = html;
    }

    function pollMetrics() {
        var base = document.body.getAttribute('data-context-path') || '';
        fetch(base + '/metrics')
            .then(function (res) { return res.json(); })
            .then(function (data) {
                renderSummary(data);
                renderEndpoints(data.endpoints);
                renderRecent(data.recent);
            })
            .catch(function (err) { console.error('Metrics poll failed:', err); });
    }

    document.addEventListener('DOMContentLoaded', function () {
        pollMetrics();
        setInterval(pollMetrics, POLL_INTERVAL_MS);
    });
})();