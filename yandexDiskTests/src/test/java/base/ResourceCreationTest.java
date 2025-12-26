package base;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.UUID;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static utils.TestDataHelper.addCreatedFolder;

public class ResourceCreationTest extends BaseTest {

    @Test(description = "TC-11 Создание папки с минимальными обязательными полями через REST API")
    public void testResourceCreationWithMinimumData() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String folderName = "test_folder_" + timestamp;
        String path = "/" + folderName;

        Response response = RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", path)
                .when()
                .put(resourceUrl);

        addCreatedFolder(path);

        response.then()
                .statusCode(201)
                .body("method", notNullValue())
                .body("href", notNullValue())
                .body("templated", notNullValue());
    }

    @Test(description = "TC-12 Создание вложенной папки через REST API")
    public void testNestedFolderCreation() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String parentFolder = "/parent_folder_" + uuid;
        String childFolderName = "child_folder_" + uuid;
        String nestedPath = parentFolder + "/" + childFolderName;

        Response parentResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", parentFolder)
                .when()
                .put(resourceUrl);

        parentResponse.then()
                .statusCode(201)
                .body("method", notNullValue());

        Response nestedResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", nestedPath)
                .when()
                .put(resourceUrl);

        nestedResponse.then()
                .statusCode(201)
                .body("href", containsString(parentFolder.substring(1)))
                .body("href", containsString(childFolderName));

        Response checkResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", parentFolder)
                .when()
                .get(resourceUrl);

        checkResponse.then()
                .statusCode(200)
                .body("type", equalTo("dir"));

        addCreatedFolder(parentFolder);
    }
}
