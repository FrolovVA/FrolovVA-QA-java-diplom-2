package ru.yandex.practicum.dto;

import lombok.Data;

@Data
public class LoginUserResponse {
    private String success;
    private String accessToken;
    private String refreshToken;
    private UserInfoBlock userInfoBlock;
}
