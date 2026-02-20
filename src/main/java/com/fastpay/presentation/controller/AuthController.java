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
public class AuthController {

    private final UserSignUpUseCase userSignUpUseCase;
    private final UserSignInUseCase userSignInUseCase;
    private final UserWebMapper mapper;

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