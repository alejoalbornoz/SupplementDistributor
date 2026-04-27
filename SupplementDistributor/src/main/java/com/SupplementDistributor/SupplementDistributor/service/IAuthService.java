package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.request.LoginRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.AuthReponseDTO;

public interface IAuthService {
    public AuthReponseDTO login (LoginRequestDTO request);
}
