package com.SupplementDistributor.SupplementDistributor.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthReponseDTO {

    private String token;
    private String type; // "Bearer"
    private UserResponse user;
}
