package ru.yandex.practicum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.steps.UserSteps;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.*;

public class APICreateUserTest {

private String email;
private String password;
private String name;
private UserSteps userSteps = new UserSteps();

    @Before
    public void setUp(){
        email = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(10);
        name = RandomStringUtils.randomAlphabetic(10);
        RestAssured.config = RestAssured.config()
                .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());
    }

    @Step("Создание пользователя")
    public ValidatableResponse creatingUser(String email, String password, String name){
        return userSteps.createUser(email, password, name);
    }

    @Step("Логин пользователя")
    public ValidatableResponse loginInUser(String email, String password){
        return userSteps.loginUser(email, password);
    }

    @Step("Получение accessToken")
    public String gettingAccessToken(ValidatableResponse validatableResponse){
        return validatableResponse.extract().path("accessToken");
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deletingUser(String accessToken){
        return userSteps.deleteUser(accessToken);
    }

    @Step("Проверка статуса ответа на запрос")
    public void checkStatusCode(ValidatableResponse validatableResponse,int statusCode){
        validatableResponse.statusCode(statusCode);
    }

    @Step("Проверка success:true в ответе при валидном запросе")
    public void checkSuccessStatus(ValidatableResponse validatableResponse, boolean expected){
        validatableResponse.body("success", is(expected));
    }

    @Step("Проверка наличия accessToken в ответе при валидном запросе")
    public void checkAccessTokenIsNotNull(ValidatableResponse validatableResponse){
        validatableResponse.body("accessToken", is(notNullValue()));
    }

    @Step("Проверка наличия accessToken в ответе при валидном запросе")
    public void checkRefreshTokenIsNotNull(ValidatableResponse validatableResponse){
        validatableResponse.body("refreshToken", is(notNullValue()));
    }

    @Step("Проверка наличия accessToken в ответе при валидном запросе")
    public void checkUserInfoBlock(ValidatableResponse validatableResponse, String expectedEmail, String expectedName){
        validatableResponse.body("user.email", equalTo(expectedEmail));
        validatableResponse.body("user.name", equalTo(expectedName));
    }

    @Step("Проверка сообщения об ошибке")
    public void checkMessage(ValidatableResponse validatableResponse, String expectedMessage){
        validatableResponse.body("message", equalTo(expectedMessage));
    }

    @Test
    @DisplayName("Проверка Api запроса POST /api/auth/register для создания пользователя с валидным заполнением полей")
    public void CreateUserTest(){
        ValidatableResponse validatableResponse = creatingUser(email, password, name);
        checkStatusCode(validatableResponse, 200);
        checkSuccessStatus(validatableResponse, true);
        checkAccessTokenIsNotNull(validatableResponse);
        checkRefreshTokenIsNotNull(validatableResponse);
        checkUserInfoBlock(validatableResponse, email, name);
    }

    @Test
    @DisplayName("Проверка Api запроса POST /api/auth/register при попытке создания пользователя с одинаковым email")
    public void CreteUserWithSameEmailTest(){
        creatingUser(email, password, name);
        ValidatableResponse validatableResponse = creatingUser(email, password, name);
        checkStatusCode(validatableResponse, 403);
        checkSuccessStatus(validatableResponse, false);
        checkMessage(validatableResponse, "User already exists");
    }

    @Test
    @DisplayName("Проверка Api запроса POST /api/auth/register при попытке создания пользователя без email")
    public void CreteUserWithoutEmailTest(){
        ValidatableResponse validatableResponse = creatingUser(null, password, name);
        checkStatusCode(validatableResponse, 403);
        checkSuccessStatus(validatableResponse, false);
        checkMessage(validatableResponse, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Проверка Api запроса POST /api/auth/register при попытке создания пользователя без password")
    public void CreteUserWithoutPasswordTest(){
        ValidatableResponse validatableResponse = creatingUser(email, null, name);
        checkStatusCode(validatableResponse, 403);
        checkSuccessStatus(validatableResponse, false);
        checkMessage(validatableResponse, "Email, password and name are required fields");
    }

    @Test
    @DisplayName("Проверка Api запроса POST /api/auth/register при попытке создания пользователя без name")
    public void CreteUserWithoutNameTest(){
        ValidatableResponse validatableResponse = creatingUser(email, password, null);
        checkStatusCode(validatableResponse, 403);
        checkSuccessStatus(validatableResponse, false);
        checkMessage(validatableResponse, "Email, password and name are required fields");
    }

    @After
    public void tearDown() {
        String accessToken = gettingAccessToken(loginInUser(email, password));
        if (accessToken != null) {
            deletingUser(accessToken);
        }
    }

}
