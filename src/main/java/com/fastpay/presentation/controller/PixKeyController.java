package com.fastpay.presentation.controller;

import com.fastpay.domain.model.PixKey;
import com.fastpay.domain.pagination.PageResult;
import com.fastpay.domain.port.in.FindPixKeyUseCase;
import com.fastpay.domain.port.in.ListPixKeysUseCase;
import com.fastpay.domain.port.in.RegisterPixKeyUseCase;
import com.fastpay.presentation.controller.request.PixKeyRequest;
import com.fastpay.presentation.controller.response.PixKeyOwnerResponse;
import com.fastpay.presentation.controller.response.PixKeyResponse;
import com.fastpay.presentation.mapper.PixWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/keys")
@RequiredArgsConstructor
public class PixKeyController {

    private final RegisterPixKeyUseCase registerPixKeyUseCase;
    private final FindPixKeyUseCase findPixKeyUseCase;
    private final ListPixKeysUseCase listPixKeysUseCase;
    private final PixWebMapper mapper;

    @PostMapping
    public ResponseEntity<PixKeyResponse> register(@RequestBody @Valid PixKeyRequest request) {
        PixKey registeredKey = registerPixKeyUseCase.register(
                request.accountId(),
                request.type(),
                request.value()
        );

        PixKeyResponse response = mapper.toResponse(registeredKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{value}")
    public ResponseEntity<PixKeyOwnerResponse> findOwnerDetails(@PathVariable String value) {
        PixKey pixKey = findPixKeyUseCase.findByValue(value);
        PixKeyOwnerResponse response = mapper.toOwnerResponse(pixKey);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<PageResult<PixKeyResponse>> listByAccount(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        PageResult<PixKey> domainPage = listPixKeysUseCase.listByAccountId(accountId, page, size);

        List<PixKeyResponse> responseData = domainPage.data().stream()
                .map(mapper::toResponse)
                .toList();

        PageResult<PixKeyResponse> response = new PageResult<>(
                responseData,
                domainPage.currentPage(),
                domainPage.totalPages(),
                domainPage.totalElements()
        );

        return ResponseEntity.ok(response);
    }
}