package base;

import io.restassured.RestAssured;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

//Задача D3.  Автотесты REST
public class YandexDiskAuthTest extends BaseTest {

    @Test(description = "TC-01 Успешная авторизация по токену")
    public void yandexDiskAuthSuccessTest() {
        given()
                .spec(requestBaseSpec)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("user", notNullValue(), "user.login", notNullValue(), "user.display_name", notNullValue());
    }

    @Test(description = "TC-02 Не успешная авторизация без токена")
    public void yandexDiskAuthFailTest() {
        given()
                .spec(requestBaseNoAuthSpec)
                .when()
                .get()
                .then()
                .statusCode(401)
                .body("message", notNullValue());
    }
}
