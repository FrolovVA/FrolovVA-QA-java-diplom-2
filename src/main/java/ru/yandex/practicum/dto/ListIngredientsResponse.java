package ru.yandex.practicum.dto;

import lombok.Data;
import java.util.List;

@Data
public class ListIngredientsResponse {
    private Boolean success;
    private List<IngredientInfo> data;
}
