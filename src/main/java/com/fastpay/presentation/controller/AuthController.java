package com.fastpay.presentation.controller;

import com.fastpay.domain.model.User;
import com.fastpay.domain.port.in.UserSignInUseCase;
import com.fastpay.domain.port.in.UserSignUpUseCase;
import com.fastpay.domain.port.in.command.LoginCommand;
import com.fastpay.domain.port.in.command.SignUpCommand;
import com.fastpay.presentation.controller.request.LoginRequest;
import com.fastpay.presentation.controller.request.SignUpRequest;
import com.fastpay.presentation.controller.response.TokenResponse;
import com.fastpay.presentation.controller.response.UserResponse;
import com.fastpay.presentation.mapper.UserWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and JWT token generation.")
public class AuthController {

    private final UserSignUpUseCase userSignUpUseCase;
    private final UserSignInUseCase userSignInUseCase;
    private final UserWebMapper mapper;

    @SecurityRequirements()
    @Operation(summary = "Register a new user", description = "Creates a new user account with an associated banking account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email or Document already exists", content = @Content)
    })
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> register(
            @RequestBody @Valid SignUpRequest request
    ) {
        SignUpCommand command = new SignUpCommand(
                request.name(),
                request.document(),
                request.email(),
                request.password()
        );

        User createdUser = userSignUpUseCase.signUp(command);
        UserResponse response = mapper.toResponse(createdUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @SecurityRequirements()
    @Operation(summary = "Sign in to the platform", description = "Authenticates a user and returns a JWT token for secure API access.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    @PostMapping("/signin")
    public ResponseEntity<TokenResponse> login(
            @RequestBody @Valid LoginRequest request
    ) {
        LoginCommand command = new LoginCommand(request.email(), request.password());
        String jwtToken = userSignInUseCase.signIn(command);

        TokenResponse response = new TokenResponse(jwtToken, "Bearer");
        return ResponseEntity.ok(response);
    }
}