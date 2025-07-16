package ru.yandex.practicum.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.practicum.dto.ListIngredientsResponse;
import ru.yandex.practicum.dto.IngredientInfo;
import java.util.List;
import java.util.stream.Collectors;
import static io.restassured.RestAssured.given;

public class IngredientsListSteps {

    private final String getIngredientsListHandle = "api/ingredients";

    @Step("Формируем Api запрос GET /api/order для получения списка ингредиентов")
    public ValidatableResponse getIngredientsListRequest(){
        return given()
                .spec(RequestSpec.baseSpec())
                .when()
                .get(getIngredientsListHandle)
                .then();
    }

    @Step("Преобразуем ответ на запрос GET /api/order в список ингредиентов")
    public List<String> getIngredientsListAsString(ValidatableResponse validatableResponse) throws JsonProcessingException {
        String json = validatableResponse.extract().asString();
        ListIngredientsResponse ingredientsResponse = new ObjectMapper().readValue(json, ListIngredientsResponse.class);
        return ingredientsResponse.getData().stream()
                .map(IngredientInfo::getId)
                .collect(Collectors.toList());
    }


    @Step("Получаем список из id ингредиентов")
    public List<String> getIngredientsIdsAsList() throws JsonProcessingException {
        return getIngredientsListAsString(getIngredientsListRequest());
    }


}
