package com.fastpay.presentation.controller;

import com.fastpay.domain.model.Transaction;
import com.fastpay.domain.pagination.PageResult;
import com.fastpay.domain.port.in.GetTransactionHistoryUseCase;
import com.fastpay.domain.port.in.SendPixUseCase;
import com.fastpay.presentation.controller.request.TransferRequest;
import com.fastpay.presentation.controller.response.TransactionHistoryResponse;
import com.fastpay.presentation.controller.response.TransferResponse;
import com.fastpay.presentation.mapper.PixWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Pix Transactions", description = "Endpoints for initiating transfers and retrieving account statements.")
public class TransactionController {

    private final SendPixUseCase sendPixUseCase;
    private final GetTransactionHistoryUseCase getTransactionHistoryUseCase;
    private final PixWebMapper mapper;

    @Operation(summary = "Initiate a Pix transfer", description = "Creates a new transaction to transfer funds asynchronously from the sender to the destination key owner.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Transfer accepted for processing"),
            @ApiResponse(responseCode = "400", description = "Insufficient funds or invalid payload", content = @Content),
            @ApiResponse(responseCode = "404", description = "Destination key not found", content = @Content)
    })
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

    @Operation(summary = "Get transaction history", description = "Returns a paginated account statement detailing credit and debit operations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "History retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    @GetMapping("/history/{accountId}")
    public ResponseEntity<PageResult<TransactionHistoryResponse>> getHistory(
            @Parameter(description = "The UUID of the account") @PathVariable UUID accountId,
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "The size of the page to be returned") @RequestParam(defaultValue = "10") int size
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