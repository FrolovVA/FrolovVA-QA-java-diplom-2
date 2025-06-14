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

public class APIPatchUserTest {
    private String email;
    private String password;
    private String name;
    private String accessToken;
    UserSteps userSteps = new UserSteps();

    @Before
    public void setUp(){
        email = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(10);
        name = RandomStringUtils.randomAlphabetic(10);
        RestAssured.config = RestAssured.config()
                .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());
        ValidatableResponse validatableResponse = creatingUser(email, password, name);
        accessToken = gettingAccessToken(validatableResponse);
    }

    @Step("Создание пользователя")
    public ValidatableResponse creatingUser(String email, String password, String name){
        return userSteps.createUser(email, password, name);
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deletingUser(String accessToken){
        return userSteps.deleteUser(accessToken);
    }

    @Step("Запрос на изменение имени пользователя")
    public ValidatableResponse patchName(String accessToken, String newName){
        return userSteps.patchUserName(accessToken, newName);
    }

    @Step("Запрос на изменение имени пользователя")
    public ValidatableResponse patchEmail(String accessToken, String newEmail){
        return userSteps.patchUserEmail(accessToken, newEmail);
    }

    @Step("Получение accessToken")
    public String gettingAccessToken(ValidatableResponse validatableResponse){
        return validatableResponse.extract().path("accessToken");
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
    public void checkUserInfoBlock(ValidatableResponse validatableResponse, String expectedEmail, String expectedName){
        validatableResponse.body("user.email", equalTo(expectedEmail));
        validatableResponse.body("user.name", equalTo(expectedName));
    }

    @Step("Проверка сообщения об ошибке")
    public void checkMessage(ValidatableResponse validatableResponse, String expectedMessage){
        validatableResponse.body("message", equalTo(expectedMessage));
    }

    @Test
    @DisplayName("Проверка Api запроса PATCH /api/auth/user при изменении данных пользователя с уникальным именем и с авторизацией")
    public void patchUserInfoUniqueNameWithAuthTest(){
        String newName = RandomStringUtils.randomAlphabetic(10);
        ValidatableResponse validatableResponse = patchName(accessToken, newName);
        checkStatusCode(validatableResponse, 200);
        checkSuccessStatus(validatableResponse, true);
        checkUserInfoBlock(validatableResponse, email, newName);
    }

    @Test
    @DisplayName("Проверка Api запроса PATCH /api/auth/user при изменении данных пользователя с таким же именем и с авторизацией")
    public void patchUserInfoSameNameWithAuthTest(){
        ValidatableResponse validatableResponse = patchName(accessToken, name);
        checkStatusCode(validatableResponse, 200);
        checkSuccessStatus(validatableResponse, true);
        checkUserInfoBlock(validatableResponse, email, name);
    }

    @Test
    @DisplayName("Проверка Api запроса PATCH /api/auth/user при изменении данных пользователя с уникальным email и с авторизацией")
    public void patchUserInfoUniqueEmailWithAuthTest(){
        String newEmail = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@yandex.ru";
        ValidatableResponse validatableResponse = patchEmail(accessToken, newEmail);
        checkStatusCode(validatableResponse, 200);
        checkSuccessStatus(validatableResponse, true);
        checkUserInfoBlock(validatableResponse, newEmail, name);
    }

    @Test
    @DisplayName("Проверка Api запроса PATCH /api/auth/user при изменении данных пользователя, изменение email на существующий и с авторизацией")
    public void patchUserInfoSameEmailWithAuthTest(){
        String newEmail = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@yandex.ru";
        creatingUser(newEmail, password, name);
        ValidatableResponse validatableResponse = patchEmail(accessToken, newEmail);
        checkStatusCode(validatableResponse, 403);
        checkSuccessStatus(validatableResponse, false);
        checkMessage(validatableResponse, "User with such email already exists");
    }

    @Test
    @DisplayName("Проверка Api запроса PATCH /api/auth/user при изменении данных пользователя с уникальным именем, но без авторизацией")
    public void patchUserInfoUniqueNameWithoutAuthTest(){
        String newName = RandomStringUtils.randomAlphabetic(10);
        ValidatableResponse validatableResponse = patchName(null, newName);
        checkStatusCode(validatableResponse, 401);
        checkSuccessStatus(validatableResponse, false);
        checkMessage(validatableResponse, "You should be authorised");
    }

    @Test
    @DisplayName("Проверка Api запроса PATCH /api/auth/user при изменении данных пользователя с таким же именем, но без авторизацией")
    public void patchUserInfoSameNameWithoutAuthTest(){
        ValidatableResponse validatableResponse = patchName(null, name);
        checkStatusCode(validatableResponse, 401);
        checkSuccessStatus(validatableResponse, false);
        checkMessage(validatableResponse, "You should be authorised");
    }

    @Test
    @DisplayName("Проверка Api запроса PATCH /api/auth/user при изменении данных пользователя с уникальным email, но без авторизацией")
    public void patchUserInfoUniqueEmailWithoutAuthTest(){
        String newEmail = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@yandex.ru";
        ValidatableResponse validatableResponse = patchEmail(null, newEmail);
        checkStatusCode(validatableResponse, 401);
        checkSuccessStatus(validatableResponse, false);
        checkMessage(validatableResponse, "You should be authorised");
    }

    @Test
    @DisplayName("Проверка Api запроса PATCH /api/auth/user при изменении данных пользователя, изменение email на существующий, но без авторизацией")
    public void patchUserInfoSameEmailWithoutAuthTest(){
        ValidatableResponse validatableResponse = patchEmail(null, email);
        checkStatusCode(validatableResponse, 401);
        checkSuccessStatus(validatableResponse, false);
        checkMessage(validatableResponse, "You should be authorised");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            deletingUser(accessToken);
        }
    }
}
