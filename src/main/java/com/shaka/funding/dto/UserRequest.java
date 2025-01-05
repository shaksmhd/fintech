package com.shaka.funding.dto;

import com.shaka.funding.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

        private String firstName;
        private String lastName;
        private String otherName;
        private String gender;
        private String dateOfBirth;
        private String stateOfOrigin;
        private String accountNumber;
        private Role role;
        private String address;
        private String email;
        private String password;
        private String phoneNumber;
        private String alternativePhoneNumber;

}
