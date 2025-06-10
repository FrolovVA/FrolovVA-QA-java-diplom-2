package ru.yandex.practicum.steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import ru.yandex.practicum.dto.CreateUserRequest;
import ru.yandex.practicum.dto.LoginUserRequest;
import static io.restassured.RestAssured.given;

public class UserSteps {

private String baseUrl = "https://stellarburgers.nomoreparties.site/";
private String createUserHandle = "api/auth/register";
private String loginUserHandle = "/api/auth/login";
private String userInfoHandle = "/api/auth/user";

    @Step("Формируем json тело для запроса POST /api/auth/register для создания пользователя")
    public CreateUserRequest createUserRequestBody(String email, String password, String name){
        CreateUserRequest request =new CreateUserRequest();
        request.setEmail(email);
        request.setPassword(password);
        request.setName(name);
        return request;
    }

    @Step("Отравляем Api запрос POST /api/auth/register для создания пользователя")
    public ValidatableResponse postCreateUserRequest(String baseUrl, CreateUserRequest createUserRequestBody, String createUserHandle){
        return given()
                .contentType(ContentType.JSON)
                .baseUri(baseUrl)
                .body(createUserRequestBody)
                .when()
                .post(createUserHandle)
                .then();
    }

    public ValidatableResponse createUser(String email, String password, String name) {
        CreateUserRequest createUserRequestBody = createUserRequestBody(email, password, name);
        return postCreateUserRequest(baseUrl, createUserRequestBody, createUserHandle);
    }

    @Step("Формируем json тело для запроса POST /api/auth/login для логина пользователя")
    public LoginUserRequest loginUserRequestBody(String email, String password){
        LoginUserRequest request = new LoginUserRequest();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

    @Step("Отправляем Api запрос POST /api/auth/login для логина пользователя")
    public ValidatableResponse postLoginUserRequest(String baseUrl, LoginUserRequest loginUserRequestBody, String loginUserHandle){
        return given()
                .contentType(ContentType.JSON)
                .baseUri(baseUrl)
                .body(loginUserRequestBody)
                .when()
                .post(loginUserHandle)
                .then();
    }

    public ValidatableResponse loginUser(String email, String password){
        LoginUserRequest loginUserRequestBody = loginUserRequestBody(email, password);
        return postLoginUserRequest(baseUrl, loginUserRequestBody, loginUserHandle);
    }

    @Step("Отправляем Api запрос DELETE /api/auth/user для удаления пользователя")
    public  ValidatableResponse deleteDeleteUserRequest(String accessToken){
        return given()
                .headers(
                        "Authorization", accessToken,
                        "Content-Type", ContentType.JSON
                        )
                .baseUri(baseUrl)
                .when()
                .delete(userInfoHandle)
                .then();
    }

    public ValidatableResponse deleteUser(String accessToken){
        return deleteDeleteUserRequest(accessToken);
    }

    @Step("Оправляем Api запрос PATCH /api/auth/user для обновления имени в информации о пользователе")
    public ValidatableResponse patchUserInfoNameRequest(String accessToken, String name) {
        var requestSpec = given()
                .contentType(ContentType.JSON)
                .baseUri(baseUrl)
                .body("{\n" + "\"name\": \"" + name + "\"\n" + "}");
        if (accessToken != null) {
            requestSpec.header("Authorization", accessToken);
        }
        return requestSpec
                .when()
                .patch(userInfoHandle)
                .then();
    }

    public ValidatableResponse patchUserName(String accessToken, String name) throws IllegalArgumentException{
        return patchUserInfoNameRequest(accessToken, name);
    }

    @Step("Оправляем Api запрос PATCH /api/auth/user для обновления имени в информации о пользователе")
    public ValidatableResponse patchUserInfoEmailRequest(String accessToken, String email) {
        var requestSpec = given()
                .contentType(ContentType.JSON)
                .baseUri(baseUrl)
                .body("{\n" + "\"email\": \"" + email + "\"\n" + "}");
        if (accessToken != null) {
            requestSpec.header("Authorization", accessToken);
        }
        return requestSpec
                .when()
                .patch(userInfoHandle)
                .then();
    }

    public ValidatableResponse patchUserEmail(String accessToken, String email) throws IllegalArgumentException{
        return patchUserInfoEmailRequest(accessToken, email);
    }
}
