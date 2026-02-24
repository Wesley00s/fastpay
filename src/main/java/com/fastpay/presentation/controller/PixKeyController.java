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
@RequestMapping("/api/v1/keys")
@RequiredArgsConstructor
@Tag(name = "Pix Keys Management", description = "Endpoints for creating, listing, and resolving Pix alias keys.")
public class PixKeyController {

    private final RegisterPixKeyUseCase registerPixKeyUseCase;
    private final FindPixKeyUseCase findPixKeyUseCase;
    private final ListPixKeysUseCase listPixKeysUseCase;
    private final PixWebMapper mapper;

    @Operation(summary = "Register a new Pix key", description = "Links a new Pix alias (CPF, EMAIL, RANDOM) to an existing account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pix key successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "409", description = "Pix key already registered to another account", content = @Content)
    })
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

    @Operation(summary = "Find Pix key owner details", description = "Retrieves the account details and masked document of the key owner to safely confirm a transfer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Key details retrieved"),
            @ApiResponse(responseCode = "404", description = "Pix key not found", content = @Content)
    })
    @GetMapping("/{value}")
    public ResponseEntity<PixKeyOwnerResponse> findOwnerDetails(
            @Parameter(description = "The string value of the Pix key") @PathVariable String value
    ) {
        PixKey pixKey = findPixKeyUseCase.findByValue(value);
        PixKeyOwnerResponse response = mapper.toOwnerResponse(pixKey);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List Pix keys by account", description = "Returns a paginated list of all Pix keys linked to a specific account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Keys retrieved successfully")
    })
    @GetMapping("/account/{accountId}")
    public ResponseEntity<PageResult<PixKeyResponse>> listByAccount(
            @Parameter(description = "The UUID of the account") @PathVariable UUID accountId,
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "The size of the page to be returned") @RequestParam(defaultValue = "10") int size
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