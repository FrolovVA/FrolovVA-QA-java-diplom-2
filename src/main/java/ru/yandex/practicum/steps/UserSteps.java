package ru.yandex.practicum.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ru.yandex.practicum.dto.CreateUserRequest;
import ru.yandex.practicum.dto.LoginUserRequest;
import ru.yandex.practicum.dto.PatchUserEmailRequest;
import ru.yandex.practicum.dto.PatchUserNameRequest;

import static io.restassured.RestAssured.given;

public class UserSteps {
private final String createUserHandle = "api/auth/register";
private final String loginUserHandle = "/api/auth/login";
private final String userInfoHandle = "/api/auth/user";

    @Step("Формируем json тело для запроса POST /api/auth/register для создания пользователя")
    public CreateUserRequest createUserRequestBody(String email, String password, String name){
        CreateUserRequest request =new CreateUserRequest();
        request.setEmail(email);
        request.setPassword(password);
        request.setName(name);
        return request;
    }

    @Step("Формируем Api запрос POST /api/auth/register для создания пользователя")
    public ValidatableResponse postCreateUserRequest( CreateUserRequest createUserRequestBody, String createUserHandle){
        return given()
                .spec(RequestSpec.baseSpec())
                .body(createUserRequestBody)
                .when()
                .post(createUserHandle)
                .then();
    }

    @Step("Отправляем Api запрос POST /api/auth/register для создания пользователя и получаем ответ")
    public ValidatableResponse createUser(String email, String password, String name) {
        CreateUserRequest createUserRequestBody = createUserRequestBody(email, password, name);
        return postCreateUserRequest(createUserRequestBody, createUserHandle);
    }

    @Step("Формируем json тело для запроса POST /api/auth/login для логина пользователя")
    public LoginUserRequest loginUserRequestBody(String email, String password){
        LoginUserRequest request = new LoginUserRequest();
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

    @Step("Формируем Api запрос POST /api/auth/login для логина пользователя")
    public ValidatableResponse postLoginUserRequest(LoginUserRequest loginUserRequestBody, String loginUserHandle){
        return given()
                .spec(RequestSpec.baseSpec())
                .body(loginUserRequestBody)
                .when()
                .post(loginUserHandle)
                .then();
    }

    @Step("Отправляем Api запрос POST /api/auth/login для логина пользователя и получаем ответ")
    public ValidatableResponse loginUser(String email, String password){
        LoginUserRequest loginUserRequestBody = loginUserRequestBody(email, password);
        return postLoginUserRequest(loginUserRequestBody, loginUserHandle);
    }

    @Step("Формируем Api запрос DELETE /api/auth/user для удаления пользователя")
    public  ValidatableResponse deleteDeleteUserRequest(String accessToken){
        var requestSpec = given()
                .spec(RequestSpec.baseSpec());
        if (accessToken != null) {
            requestSpec.header("Authorization", accessToken);
        }
        return requestSpec
                .when()
                .delete(userInfoHandle)
                .then();

    }

    @Step("Отправляем Api запрос DELETE /api/auth/user для удаления пользователя и получаем ответ")
    public ValidatableResponse deleteUser(String accessToken){
        return deleteDeleteUserRequest(accessToken);
    }



    @Step("Формируем json тело для запроса PATCH /api/auth/user для изменения имени в информации о пользователе")
    public PatchUserNameRequest patchUserNameRequestBody(String name){
        PatchUserNameRequest request =new PatchUserNameRequest();
        request.setName(name);
        return request;
    }


    @Step("Формируем Api запрос PATCH /api/auth/user для обновления имени в информации о пользователе")
    public ValidatableResponse patchUserInfoNameRequest(String accessToken, PatchUserNameRequest patchUserNameRequestBody) {
        var requestSpec = given()
                .spec(RequestSpec.baseSpec())
                .body(patchUserNameRequestBody);
        if (accessToken != null) {
            requestSpec.header("Authorization", accessToken);
        }
        return requestSpec
                .when()
                .patch(userInfoHandle)
                .then();
    }

    @Step("Отправляем Api запрос PATCH /api/auth/user для обновления имени в информации о пользователе и получаем ответ")
    public ValidatableResponse patchUserName(String accessToken, String name) throws IllegalArgumentException{
        PatchUserNameRequest patchUserNameRequestBody = patchUserNameRequestBody(name);
        return patchUserInfoNameRequest(accessToken, patchUserNameRequestBody);
    }


    @Step("Формируем json тело для запроса PATCH /api/auth/user для изменения email в информации о пользователе")
    public PatchUserEmailRequest patchUserEmailRequestBody(String email){
        PatchUserEmailRequest request =new PatchUserEmailRequest();
        request.setEmail(email);
        return request;
    }

    @Step("Формируем Api запрос PATCH /api/auth/user для обновления email в информации о пользователе")
    public ValidatableResponse patchUserInfoEmailRequest(String accessToken, PatchUserEmailRequest patchUserEmailRequestBody) {
        var requestSpec = given()
                .spec(RequestSpec.baseSpec())
                .body(patchUserEmailRequestBody);
        if (accessToken != null) {
            requestSpec.header("Authorization", accessToken);
        }
        return requestSpec
                .when()
                .patch(userInfoHandle)
                .then();
    }

    @Step("Отправляем Api запрос PATCH /api/auth/user для обновления email в информации о пользователе и получаем ответ")
    public ValidatableResponse patchUserEmail(String accessToken, String email) throws IllegalArgumentException{
        PatchUserEmailRequest patchUserEmailRequestBody = patchUserEmailRequestBody(email);
        return patchUserInfoEmailRequest(accessToken, patchUserEmailRequestBody);
    }
}
