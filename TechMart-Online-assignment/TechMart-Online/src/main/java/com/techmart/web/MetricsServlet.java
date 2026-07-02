package com.techmart.web;

import com.techmart.metrics.PerformanceMetrics;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/metrics")
public class MetricsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        PerformanceMetrics metrics = PerformanceMetrics.getInstance();

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"totalRequests\":").append(metrics.getTotalRequests()).append(",");
        json.append("\"averageResponseTimeMs\":")
                .append(String.format("%.2f", metrics.getAverageResponseTimeMs())).append(",");
        json.append("\"slowestRequestMs\":").append(metrics.getSlowestRequestMs()).append(",");
        json.append("\"errorCount\":").append(metrics.getErrorCount()).append(",");

        json.append("\"endpoints\":[");
        Map<String, PerformanceMetrics.EndpointStats> endpointStats = metrics.getEndpointStats();
        int i = 0;
        for (Map.Entry<String, PerformanceMetrics.EndpointStats> entry : endpointStats.entrySet()) {
            if (i++ > 0) json.append(",");
            PerformanceMetrics.EndpointStats stats = entry.getValue();
            json.append("{")
                    .append("\"endpoint\":\"").append(escape(entry.getKey())).append("\",")
                    .append("\"count\":").append(stats.getCount()).append(",")
                    .append("\"avgMs\":").append(String.format("%.2f", stats.getAverageMs())).append(",")
                    .append("\"maxMs\":").append(stats.getMaxMs())
                    .append("}");
        }
        json.append("],");

        json.append("\"recent\":[");
        List<PerformanceMetrics.RequestLogEntry> recent = metrics.getRecentRequests();
        for (int j = 0; j < recent.size(); j++) {
            if (j > 0) json.append(",");
            PerformanceMetrics.RequestLogEntry entry = recent.get(j);
            json.append("{")
                    .append("\"time\":\"").append(entry.time).append("\",")
                    .append("\"endpoint\":\"").append(escape(entry.endpoint)).append("\",")
                    .append("\"durationMs\":").append(entry.durationMs).append(",")
                    .append("\"statusCode\":").append(entry.statusCode)
                    .append("}");
        }
        json.append("]");

        json.append("}");

        out.print(json);
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }
}