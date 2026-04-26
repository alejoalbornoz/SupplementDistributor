package com.SupplementDistributor.SupplementDistributor.dto.response;


import com.SupplementDistributor.SupplementDistributor.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private RoleName role;
    // Sin password ✅
    // Sin active ✅
    // Sin createdAt ✅
}
