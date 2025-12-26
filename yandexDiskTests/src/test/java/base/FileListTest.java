package base;

import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;

//Задача D7.  JSON Schema Validation
public class FileListTest extends BaseTest {


    @Test(description = "TC-71: Получение списка файлов с валидацией JSON Schema")
    public void testGetFileListWithSchemaValidation() {
        given()
                .spec(requestBaseSpec)
                .queryParam("path", "/")
                .when()
                .get(resourceUrl)
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("file-list-schema.json"));
    }
}