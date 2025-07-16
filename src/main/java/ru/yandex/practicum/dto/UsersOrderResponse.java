package ru.yandex.practicum.dto;

import lombok.Data;
import java.util.List;

@Data
public class UsersOrderResponse {
    private Boolean success;
    private List<UsersOrderInfo> orders;
    private Integer total;
    private Integer totalToday;
}
