package ru.yandex.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import ru.yandex.practicum.dto.CreateOrderRequest;
import java.util.List;
import static io.restassured.RestAssured.given;

public class OrderSteps {
    private String baseUrl = "https://stellarburgers.nomoreparties.site/";
    private String orderHandle = "api/orders";

    @Step("Формируем Api запрос POST /api/orders для создания заказа")
    public ValidatableResponse postCreateOrderRequest(String accessToken, CreateOrderRequest createOrderRequestBody) {
        var requestSpec = given()
                .contentType(ContentType.JSON)
                .baseUri(baseUrl)
                .body(createOrderRequestBody);
        if (accessToken != null) {
            requestSpec.header("Authorization", accessToken);
        }
        return requestSpec
                .when()
                .post(orderHandle)
                .then();
    }

    @Step("Формируем тело для запроса POST /api/orders для создания заказа")
    public CreateOrderRequest createOrderRequestBody(List<String> ingredients){
        CreateOrderRequest createOrderRequestBody = new CreateOrderRequest();
        createOrderRequestBody.setIngredients(ingredients);
        return createOrderRequestBody;
    }


    @Step("Оправляем Api запрос POST /api/orders для создания заказа и получаем ответ")
    public ValidatableResponse createOrder(String accessToken, List<String> ingredients){
        CreateOrderRequest createOrderRequestBody = createOrderRequestBody(ingredients);
        return postCreateOrderRequest(accessToken, createOrderRequestBody);
    }
}
