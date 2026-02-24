package com.fastpay.presentation.controller;

import com.fastpay.domain.model.Transaction;
import com.fastpay.domain.pagination.PageResult;
import com.fastpay.domain.port.in.GetTransactionHistoryUseCase;
import com.fastpay.domain.port.in.SendPixUseCase;
import com.fastpay.presentation.controller.request.TransferRequest;
import com.fastpay.presentation.controller.response.TransactionHistoryResponse;
import com.fastpay.presentation.controller.response.TransferResponse;
import com.fastpay.presentation.mapper.PixWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pix")
@RequiredArgsConstructor
public class TransactionController {

    private final SendPixUseCase sendPixUseCase;
    private final GetTransactionHistoryUseCase getTransactionHistoryUseCase;
    private final PixWebMapper mapper;

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestBody @Valid TransferRequest request) {

        Transaction transaction = sendPixUseCase.send(
                request.senderAccountId(),
                request.destinationKey(),
                request.amountInCents()
        );

        TransferResponse response = mapper.toResponse(transaction);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/history/{accountId}")
    public ResponseEntity<PageResult<TransactionHistoryResponse>> getHistory(
            @PathVariable UUID accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        PageResult<Transaction> domainPage = getTransactionHistoryUseCase.getHistory(accountId, page, size);

        List<TransactionHistoryResponse> responseData = domainPage.data().stream()
                .map(tx -> mapper.toHistoryResponse(tx, accountId))
                .toList();

        PageResult<TransactionHistoryResponse> response = new PageResult<>(
                responseData,
                domainPage.currentPage(),
                domainPage.totalPages(),
                domainPage.totalElements()
        );

        return ResponseEntity.ok(response);
    }
}