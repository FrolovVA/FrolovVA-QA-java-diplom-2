package ru.yandex.practicum.dto;

import lombok.Data;
import java.util.List;

@Data
public class UsersOrderInfo {
    private String _id;
    private List<String> ingredients;
    private String status;
    private String name;
    private String createdAt;
    private String updatedAt;
    private Integer number;
}
