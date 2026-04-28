package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.request.LoginRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.AuthResponseDTO;

public interface IAuthService {
    public AuthResponseDTO login (LoginRequestDTO request);
}
