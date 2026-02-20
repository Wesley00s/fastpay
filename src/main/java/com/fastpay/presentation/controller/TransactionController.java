package com.fastpay.presentation.controller;

import com.fastpay.domain.model.Transaction;
import com.fastpay.domain.port.in.SendPixUseCase;
import com.fastpay.presentation.controller.request.TransferRequest;
import com.fastpay.presentation.controller.response.TransferResponse;
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
@RequestMapping("/api/v1/pix")
@RequiredArgsConstructor
public class TransactionController {

    private final SendPixUseCase sendPixUseCase;
    private final PixWebMapper mapper;

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody @Valid TransferRequest request) {
        log.info("Received transfer request from account: {}", request.senderAccountId());

        Transaction transaction = sendPixUseCase.send(
                request.senderAccountId(),
                request.destinationKey(),
                request.amountInCents()
        );

        TransferResponse response = mapper.toResponse(transaction);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}