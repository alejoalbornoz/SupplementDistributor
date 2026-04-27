package com.SupplementDistributor.SupplementDistributor.service;

import com.SupplementDistributor.SupplementDistributor.dto.request.CreateUserRequestDTO;
import com.SupplementDistributor.SupplementDistributor.dto.response.UserResponseDTO;

import java.util.List;

public interface IUserService {

    public List<UserResponseDTO> getAllUsers();

    public UserResponseDTO getUserById(Long id);

    public UserResponseDTO createUser(CreateUserRequestDTO request);
}
