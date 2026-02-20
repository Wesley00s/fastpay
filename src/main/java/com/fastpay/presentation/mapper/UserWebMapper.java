package com.fastpay.presentation.mapper;

import com.fastpay.domain.model.User;
import com.fastpay.presentation.controller.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserWebMapper {
    UserResponse toResponse(User domain);
}