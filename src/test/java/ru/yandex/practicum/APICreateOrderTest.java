package ru.yandex.practicum;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.steps.IngredientsListSteps;
import ru.yandex.practicum.steps.OrderSteps;
import ru.yandex.practicum.steps.UserSteps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class APICreateOrderTest {

    private String email;
    private String password;
    private String name;
    private String accessToken;
    private List<String> ingredients;
    private IngredientsListSteps ingredientsListSteps;
    private OrderSteps orderSteps;
    private UserSteps userSteps;

    @Before
    public void setUp() throws JsonProcessingException {
        RestAssured.config = RestAssured.config()
                .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());
        email = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(10);
        name = RandomStringUtils.randomAlphabetic(10);
        ingredientsListSteps = new IngredientsListSteps();
        orderSteps = new OrderSteps();
        userSteps = new UserSteps();
        ingredients = getNRandomIngredients(getListOfIngredientsIds(), 3);
        ValidatableResponse validatableResponse = creatingUser(email, password, name);
        accessToken = gettingAccessToken(validatableResponse);
    }

    @Step("Получаем лист ингредиентов в виде списка из строк")
    public List<String> getListOfIngredientsIds() throws JsonProcessingException {
      return ingredientsListSteps.getIngredientsIdsAsList();
    }

    @Step("Получаем n случайных элементов из списка ингредиентов")
    public List<String> getNRandomIngredients(List<String> ingredients, int n){
        if (n >= ingredients.size()) {
            return ingredients;
        }
        List<String> shuffled = ingredients.stream().collect(Collectors.toList());
        Collections.shuffle(shuffled);
        return shuffled.subList(0, n);
    }

    @Step("Создание пользователя")
    public ValidatableResponse creatingUser(String email, String password, String name){
        return userSteps.createUser(email, password, name);
    }

    @Step("Получение accessToken")
    public String gettingAccessToken(ValidatableResponse validatableResponse){
        return validatableResponse.extract().path("accessToken");
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deletingUser(String accessToken){
        return userSteps.deleteUser(accessToken);
    }

    @Step("Создание заказа")
    public ValidatableResponse creatingOrder(String accessToken, List<String> ingredients){
        return orderSteps.createOrder(accessToken, ingredients);
    }

    @Step("Проверка сообщения об ошибке")
    public void checkMessage(ValidatableResponse validatableResponse, String expectedMessage){
        validatableResponse.body("message", equalTo(expectedMessage));
    }

    @Step("Проверка статуса ответа на запрос")
    public void checkStatusCode(ValidatableResponse validatableResponse,int statusCode){
        validatableResponse.statusCode(statusCode);
    }

    @Step("Проверка success:true в ответе при валидном запросе")
    public void checkSuccessStatus(ValidatableResponse validatableResponse, boolean expected){
        validatableResponse.body("success", is(expected));
    }

    @Step("Проверка сообщения об ошибке")
    public void checkOrderNumber(ValidatableResponse validatableResponse){
        validatableResponse.body("order.number", is(notNullValue()));
    }

    @Test
    @DisplayName("Проверка Api запроса POST /api/orders для создания заказа с ингредиентами и авторизацией")
    public void createOrderWithIngredientsAndAuthTest(){
        ValidatableResponse validatableResponse = creatingOrder(accessToken, ingredients);
        checkStatusCode(validatableResponse, 200);
        checkSuccessStatus(validatableResponse, true);
        checkOrderNumber(validatableResponse);
    }

    @Test
    @DisplayName("Проверка Api запроса POST /api/orders для создания заказа с ингредиентами без авторизации")
    public void createOrderWithoutAuthTest(){
        ValidatableResponse validatableResponse = creatingOrder(null, ingredients);
        checkStatusCode(validatableResponse, 200);
        checkSuccessStatus(validatableResponse, true);
        checkOrderNumber(validatableResponse);
    }

    @Test
    @DisplayName("Проверка Api запроса POST /api/orders для создания заказа без ингредиентов и с авторизацией")
    public void createOrderWithoutIngredientsTest(){
        ValidatableResponse validatableResponse = creatingOrder(accessToken, null);
        checkStatusCode(validatableResponse, 400);
        checkSuccessStatus(validatableResponse, false);
        checkMessage(validatableResponse, "Ingredient ids must be provided");
    }

    @Test
    @DisplayName("Проверка Api запроса POST /api/orders для создания заказа с авторизацией, но с неверным хешем ингредиентов.")
    public void createOrderWithIncorrectIngredientsTest(){
        List<String> incorrectIngredients = new ArrayList<>();
        incorrectIngredients.add(RandomStringUtils.randomAlphabetic(10));
        incorrectIngredients.add(RandomStringUtils.randomAlphabetic(15));
        incorrectIngredients.add(RandomStringUtils.randomAlphabetic(20));
        ValidatableResponse validatableResponse = creatingOrder(accessToken, incorrectIngredients);
        checkStatusCode(validatableResponse, 500);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            deletingUser(accessToken);
        }
    }
}
