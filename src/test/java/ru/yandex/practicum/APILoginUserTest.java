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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class APILoginUserTest {
    private String email;
    private String password;
    private String name;
    UserSteps userSteps = new UserSteps();

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
    @DisplayName("Проверка Api запроса POST /api/auth/login при логине пользователя с валидными полями")
    public void loginCourierTest(){
        creatingUser(email, password, name);
        ValidatableResponse validatableResponse = loginInUser(email, password);
        checkStatusCode(validatableResponse, 200);
        checkSuccessStatus(validatableResponse, true);
        checkAccessTokenIsNotNull(validatableResponse);
        checkRefreshTokenIsNotNull(validatableResponse);
        checkUserInfoBlock(validatableResponse, email, name);
    }

    @Test
    @DisplayName("Проверка Api запроса POST /api/auth/login при логине пользователя с несуществующим паролем")
    public void loginCourierWithIncorrectPasswordTest(){
        creatingUser(email, password, name);
        ValidatableResponse validatableResponse = loginInUser(email, name);
        checkStatusCode(validatableResponse, 401);
        checkSuccessStatus(validatableResponse, false);
        checkMessage(validatableResponse, "email or password are incorrect");
    }

    @Test
    @DisplayName("Проверка Api запроса POST /api/auth/login при логине пользователя с несуществующим email")
    public void loginCourierWithIncorrectEmailTest(){
        creatingUser(email, password, name);
        ValidatableResponse validatableResponse = loginInUser(name, password);
        checkStatusCode(validatableResponse, 401);
        checkSuccessStatus(validatableResponse, false);
        checkMessage(validatableResponse, "email or password are incorrect");
    }

    @Test
    @DisplayName("Проверка Api запроса POST /api/auth/login при логине пользователя без пароля")
    public void loginCourierWithoutPasswordTest(){
        creatingUser(email, password, name);
        ValidatableResponse validatableResponse = loginInUser(email, null);
        checkStatusCode(validatableResponse, 401);
        checkSuccessStatus(validatableResponse, false);
        checkMessage(validatableResponse, "email or password are incorrect");
    }

    @Test
    @DisplayName("Проверка Api запроса POST /api/auth/login при логине пользователя без email")
    public void loginCourierWithoutEmailTest(){
        creatingUser(email, password, name);
        ValidatableResponse validatableResponse = loginInUser(null, password);
        checkStatusCode(validatableResponse, 401);
        checkSuccessStatus(validatableResponse, false);
        checkMessage(validatableResponse, "email or password are incorrect");
    }

    @After
    public void tearDown() {
        String accessToken = gettingAccessToken(loginInUser(email, password));
        if (accessToken != null) {
            deletingUser(accessToken);
        }
    }
}
