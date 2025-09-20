package com.tutoring.Tutorverse.Dto;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDto {
    private String email;
    private String password;
    private String name;
    private String role;
    private String providerId;
    private boolean isEmailVerified;


    public static UserCreateDto emailUser(String email,String name,String password,String role){
        return new UserCreateDto(email,password,name,role,null,false);
    }

    public static UserCreateDto googleUser(String email,String role,String providerId,String name){
        return new UserCreateDto(email,null,name,role,providerId,true);
    }
}
