package ru.yandex.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class UsersOrderSteps {

    private String baseUrl = "https://stellarburgers.nomoreparties.site/";
    private String orderHandle = "api/orders";

    @Step("Формируем Api запрос GET /api/order для получения заказа")
    public ValidatableResponse getUsersOrderRequest(String accessToken) {
        var requestSpec = given()
                .contentType(ContentType.JSON)
                .baseUri(baseUrl);
        if (accessToken != null) {
            requestSpec.header("Authorization", accessToken);
        }
        return requestSpec
                .when()
                .get(orderHandle)
                .then();
    }

    @Step("Отравляем Api запрос GET /api/order для получения заказа и получаем ответ")
    public ValidatableResponse getUsersOrderList (String accessToken){
        return getUsersOrderRequest(accessToken);
    }



}
