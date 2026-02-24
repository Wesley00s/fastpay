package com.fastpay.presentation.controller;

import com.fastpay.domain.model.Account;
import com.fastpay.domain.port.in.DepositUseCase;
import com.fastpay.domain.port.in.GetAccountDetailsUseCase;
import com.fastpay.presentation.controller.request.DepositRequest;
import com.fastpay.presentation.controller.response.AccountDetailsResponse;
import com.fastpay.presentation.mapper.AccountWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final GetAccountDetailsUseCase getAccountDetailsUseCase;
    private final DepositUseCase depositUseCase;
    private final AccountWebMapper mapper;

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDetailsResponse> getDetails(@PathVariable UUID accountId) {
        Account account = getAccountDetailsUseCase.getDetails(accountId);
        AccountDetailsResponse response = mapper.toResponse(account);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<AccountDetailsResponse> deposit(
            @PathVariable UUID accountId,
            @RequestBody @Valid DepositRequest request
    ) {
        Account updatedAccount = depositUseCase.deposit(accountId, request.amountInCents());
        AccountDetailsResponse response = mapper.toResponse(updatedAccount);
        return ResponseEntity.ok(response);
    }
}