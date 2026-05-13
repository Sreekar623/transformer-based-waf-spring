package com.iomp.WAF.Filter;

import com.iomp.WAF.Entity.RequestLog;
import com.iomp.WAF.Repository.RequestLogRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Component
public class WafFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(WafFilter.class);

    private final RequestLogRepository repository;

    public WafFilter(RequestLogRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Apply filter only for search API
        if (!path.startsWith("/api/search")) {
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("🔥 WAF FILTER ACTIVE");

        String input = request.getParameter("input");

        if (input != null && !input.isEmpty()) {

            String decision = "ALLOW";
            String attackType = "NORMAL";

            try {

                String encoded =
                        URLEncoder.encode(input, "UTF-8");

                URL url = new URL(
                        "http://127.0.0.1:8000/check?input="
                                + encoded
                );

                HttpURLConnection conn =
                        (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                conn.getInputStream()
                        )
                );

                StringBuilder aiResponse =
                        new StringBuilder();

                String line;

                while ((line = in.readLine()) != null) {
                    aiResponse.append(line);
                }

                in.close();

                String result = aiResponse.toString();

                System.out.println(
                        "🤖 AI RESPONSE: " + result
                );

                JSONObject json = new JSONObject(result);

                decision =
                        json.optString("decision", "ALLOW");

                attackType =
                        json.optString(
                                "attackType",
                                "NORMAL"
                        );

            } catch (Exception e) {

                System.out.println(
                        "⚠️ AI service failed → fallback ALLOW"
                );

                decision = "ALLOW";
                attackType = "NORMAL";
            }

            // 🚫 BLOCK REQUEST
            if ("BLOCK".equalsIgnoreCase(decision)) {

                saveLog(input, "BLOCK", attackType);

                log.error(
                        "10.209.230.222 - 🚫 BLOCKED | Type: "
                                + attackType
                                + " | Payload: "
                                + input
                );

                response.setStatus(403);

                response.getWriter().write(
                        "Blocked: "
                                + attackType
                                + " 🚫"
                );

                return;
            }

            // ✅ ALLOW REQUEST
            saveLog(input, "ALLOW", attackType);

            log.info(
                    "10.209.230.222 - ALLOW | Payload: "
                            + input
            );
        }

        filterChain.doFilter(request, response);
    }

    private void saveLog(String input,
                         String status,
                         String type) {

        RequestLog logEntity = new RequestLog();

        logEntity.setInput(input);
        logEntity.setStatus(status);
        logEntity.setAttackType(type);

        repository.save(logEntity);
    }
}