package com.fastpay.domain.port.out;

import com.fastpay.domain.model.User;

import java.util.Optional;

public interface UserDatabasePort {
    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmailOrDocument(String email, String document);
}