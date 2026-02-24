package com.fastpay.presentation.controller;

import com.fastpay.domain.model.Account;
import com.fastpay.domain.port.in.DepositUseCase;
import com.fastpay.domain.port.in.GetAccountDetailsUseCase;
import com.fastpay.infra.security.SecurityUtils;
import com.fastpay.presentation.controller.request.DepositRequest;
import com.fastpay.presentation.controller.response.AccountDetailsResponse;
import com.fastpay.presentation.mapper.AccountWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "Endpoints for managing user accounts, checking balances, and cash-in operations.")
public class AccountController {

    private final GetAccountDetailsUseCase getAccountDetailsUseCase;
    private final DepositUseCase depositUseCase;
    private final AccountWebMapper mapper;

    @Operation(summary = "Get account details", description = "Retrieves the account information, including agency, number, and current balance.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved account details"),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<AccountDetailsResponse> getMyAccount(Authentication authentication) {
        String email = SecurityUtils.extractEmail(authentication);
        Account account = getAccountDetailsUseCase.getDetailsByEmail(email);
        AccountDetailsResponse response = mapper.toResponse(account);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Deposit funds (Cash-In)", description = "Deposits a specific amount into the target account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload (e.g., negative amount)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    })
    @PostMapping("/me/deposit")
    public ResponseEntity<AccountDetailsResponse> deposit(
            @RequestBody @Valid DepositRequest request,
            Authentication authentication
    ) {
        String email = SecurityUtils.extractEmail(authentication);
        Account account = getAccountDetailsUseCase.getDetailsByEmail(email);
        Account updatedAccount = depositUseCase.deposit(account.getId(), request.amountInCents());

        AccountDetailsResponse response = mapper.toResponse(updatedAccount);
        return ResponseEntity.ok(response);
    }
}