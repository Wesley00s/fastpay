package com.fastpay.presentation.controller;

import com.fastpay.domain.model.PixKey;
import com.fastpay.domain.port.in.RegisterPixKeyUseCase;
import com.fastpay.presentation.controller.request.PixKeyRequest;
import com.fastpay.presentation.controller.response.PixKeyResponse;
import com.fastpay.presentation.mapper.PixWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/keys")
@RequiredArgsConstructor
public class PixKeyController {

    private final RegisterPixKeyUseCase registerPixKeyUseCase;
    private final PixWebMapper mapper;

    @PostMapping
    public ResponseEntity<PixKeyResponse> register(
            @RequestBody @Valid PixKeyRequest request
    ) {

        PixKey registeredKey = registerPixKeyUseCase.register(
                request.accountId(),
                request.type(),
                request.value()
        );

        PixKeyResponse response = mapper.toResponse(registeredKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}