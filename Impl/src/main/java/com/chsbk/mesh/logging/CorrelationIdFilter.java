package com.chsbk.mesh.logging;

import com.chsbk.mesh.transport.MeshEndpoints;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
public class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String suppliedId = request.getHeader(MeshEndpoints.CORRELATION_HEADER_NAME);
        String correlationId = StringUtils.hasText(suppliedId)
                && suppliedId.matches("[A-Za-z0-9_-]{1,128}")
                ? suppliedId : UUID.randomUUID().toString();

        MDC.put("correlationId", correlationId);
        response.setHeader(MeshEndpoints.CORRELATION_HEADER_NAME, correlationId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("correlationId");
        }
    }
}
