package ru.yandex.practicum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.dto.UsersOrderResponse;
import ru.yandex.practicum.steps.IngredientsListSteps;
import ru.yandex.practicum.steps.OrderSteps;
import ru.yandex.practicum.steps.UserSteps;
import ru.yandex.practicum.steps.UsersOrderSteps;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class APIGetUsersOrdersListTest {

    private String email;
    private String password;
    private String name;
    private IngredientsListSteps ingredientsListSteps;
    private OrderSteps orderSteps;
    private UserSteps userSteps;
    private List<String> ingredients;
    private String accessToken;
    private UsersOrderSteps usersOrderSteps;
    private Integer expectedOrderNumber;
    private String expectedCreatedAt;
    private String expectedUpdatedAt;

    @Step("Получение ответа на запрос Get /api/orders в виде класса UsersOrderResponse")
    public UsersOrderResponse getUsersOrderResponse(ValidatableResponse validatableResponse) throws JsonProcessingException {
        String json = validatableResponse.extract().asString();
        return new ObjectMapper().readValue(json, UsersOrderResponse.class);
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

    @Step("Создание заказа")
    public ValidatableResponse creatingOrder(String accessToken, List<String> ingredients){
        return orderSteps.createOrder(accessToken, ingredients);
    }

    @Step("Получаем проверочный номер заказа")
    public Integer getExpectedOrderNumber(ValidatableResponse validatableResponse){
        return validatableResponse.extract().path("order.number");
    }

    @Step("Получаем проверочное время заказа")
    public String getExpectedCreatedAt(ValidatableResponse validatableResponse){
        return validatableResponse.extract().path("order.createdAt").toString();
    }

    @Step("Получаем проверочное время заказа")
    public String getExpectedUpdatedAt(ValidatableResponse validatableResponse){
        return validatableResponse.extract().path("order.updatedAt").toString();
    }


    @Before
    public void setUp() throws JsonProcessingException {
        email = RandomStringUtils.randomAlphabetic(10).toLowerCase() + "@yandex.ru";
        password = RandomStringUtils.randomAlphabetic(10);
        name = RandomStringUtils.randomAlphabetic(10);
        ingredientsListSteps = new IngredientsListSteps();
        orderSteps = new OrderSteps();
        userSteps = new UserSteps();
        usersOrderSteps = new UsersOrderSteps();
        ingredients = getNRandomIngredients(getListOfIngredientsIds(), 3);

        ValidatableResponse creatingUserResponse = creatingUser(email, password, name);
        accessToken = gettingAccessToken(creatingUserResponse);

        ValidatableResponse creatingOrderResponse = creatingOrder(accessToken, ingredients);
        expectedOrderNumber = getExpectedOrderNumber(creatingOrderResponse);
        expectedCreatedAt = getExpectedCreatedAt(creatingOrderResponse);
        expectedUpdatedAt = getExpectedUpdatedAt(creatingOrderResponse);

        RestAssured.config = RestAssured.config()
                .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails());
    }

    @Step("Получение заказов пользователя")
    public ValidatableResponse gettingUsersOrderList(String accessToken){
        return usersOrderSteps.getUsersOrderList(accessToken);
    }

    @Step("Проверка номера заказа")
    public void checkOrderNumber(UsersOrderResponse usersOrderResponse, Integer expectedOrderNumber){
        Assert.assertEquals(expectedOrderNumber, usersOrderResponse.getOrders().get(0).getNumber());
    }

    @Step("Проверка времени создания заказа")
    public void checkCreatedAt(UsersOrderResponse usersOrderResponse, String expectedCreatedAt){
        Assert.assertEquals(expectedCreatedAt, usersOrderResponse.getOrders().get(0).getCreatedAt());
    }

    @Step("Проверка времени последнего изменения заказа")
    public void checkUpdatedAt(UsersOrderResponse usersOrderResponse, String expectedUpdatedAt){
        Assert.assertEquals(expectedUpdatedAt, usersOrderResponse.getOrders().get(0).getUpdatedAt());
    }

    @Step("Проверка списка ингредиентов в заказе")
    public void checkIngredientsList(UsersOrderResponse usersOrderResponse, List<String> expectedIngredients){
        Assert.assertEquals(expectedIngredients, usersOrderResponse.getOrders().get(0).getIngredients());
    }

    @Step("Проверка наличия счетчика total в ответе")
    public void checkTotalCounter(ValidatableResponse validatableResponse){
        validatableResponse.body("total", is(notNullValue()));
    }

    @Step("Проверка наличия счетчика totalToday в ответе")
    public void checkTotalTodayCounter(ValidatableResponse validatableResponse){
        validatableResponse.body("totalToday", is(notNullValue()));
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

    @Test
    @DisplayName("Проверка Api запроса GET /api/orders для получения заказов пользователя c авторизацией")
    public void getUsersOrderListWithAuthTest() throws JsonProcessingException {
        ValidatableResponse validatableResponse = gettingUsersOrderList(accessToken);
        UsersOrderResponse usersOrderResponse = getUsersOrderResponse(validatableResponse);
        checkStatusCode(validatableResponse, 200);
        checkSuccessStatus(validatableResponse, true);
        checkTotalCounter(validatableResponse);
        checkTotalTodayCounter(validatableResponse);

        checkOrderNumber(usersOrderResponse, expectedOrderNumber);
        checkCreatedAt(usersOrderResponse, expectedCreatedAt);
        checkUpdatedAt(usersOrderResponse, expectedUpdatedAt);
        checkIngredientsList(usersOrderResponse, ingredients);
    }

    @Test
    @DisplayName("Проверка Api запроса GET /api/orders для получения заказов пользователя без авторизации")
    public void getUsersOrderListWithoutAuthTest(){
        ValidatableResponse validatableResponse = gettingUsersOrderList(null);
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
