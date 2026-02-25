package com.fastpay.infra.web.filter;

import com.fastpay.infra.persistence.postgres.entity.IdempotencyRecordEntity;
import com.fastpay.infra.persistence.postgres.repository.SpringDataIdempotencyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class IdempotencyFilter extends OncePerRequestFilter {

    private static final String IDEMPOTENCY_HEADER = "Idempotency-Key";
    private final SpringDataIdempotencyRepository idempotencyRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String idempotencyKey = request.getHeader(IDEMPOTENCY_HEADER);
        String uri = request.getRequestURI();

        boolean isTransferRoute = uri.equals("/api/v1/pix/transfer") && request.getMethod().equalsIgnoreCase("POST");

        if (isTransferRoute && (idempotencyKey == null || idempotencyKey.trim().isEmpty())) {
            log.warn("Blocked transfer request missing Idempotency-Key header");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/problem+json");
            response.getWriter().write("""
                    {
                        "title": "Bad Request",
                        "status": 400,
                        "detail": "Idempotency-Key header is mandatory for this operation."
                    }
                    """);
            return;
        }

        if (idempotencyKey == null || !request.getMethod().equalsIgnoreCase("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        Optional<IdempotencyRecordEntity> existingRecord = idempotencyRepository.findByIdempotencyKey(idempotencyKey);

        if (existingRecord.isPresent()) {
            log.info("Idempotency key {} found. Returning cached response.", idempotencyKey);
            IdempotencyRecordEntity record = existingRecord.get();

            response.setStatus(record.getResponseStatus());
            response.setContentType("application/json");
            response.getWriter().write(record.getResponseBody());
            return;
        }

        log.info("Idempotency key {} not found. Processing request normally.", idempotencyKey);

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(request, responseWrapper);

        int status = responseWrapper.getStatus();

        if (status >= 200 && status < 300) {
            String responseBody = new String(responseWrapper.getContentAsByteArray(), responseWrapper.getCharacterEncoding());

            IdempotencyRecordEntity newRecord = IdempotencyRecordEntity.builder()
                    .id(UUID.randomUUID())
                    .idempotencyKey(idempotencyKey)
                    .requestPath(request.getRequestURI())
                    .responseStatus(status)
                    .responseBody(responseBody)
                    .createdAt(Instant.now())
                    .build();

            idempotencyRepository.save(newRecord);
            log.info("Saved new idempotency record for key {}", idempotencyKey);
        }

        responseWrapper.copyBodyToResponse();
    }
}