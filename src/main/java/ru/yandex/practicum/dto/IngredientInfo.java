package ru.yandex.practicum.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IngredientInfo {
    @JsonProperty("_id")
    private String id;

    private String name;
    private String type;
    private Integer proteins;
    private Integer fat;
    private Integer carbohydrates;
    private Integer calories;
    private Integer price;
    private String image;

    @JsonProperty("image_mobile")
    private String imageMobile;
    @JsonProperty("image_large")
    private String imageLarge;
    @JsonProperty("__v")
    private Integer v;
}
