package ru.yandex.practicum.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    private List<String> ingredients;
}
