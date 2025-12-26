package base;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;


import static org.hamcrest.Matchers.*;

public class ResourceDeletionTest extends BaseTest {

    @Test(description = "TC-21 Удаление папки с перемещением в Корзину")
    public void testDeleteFolderToTrash() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String folderName = "test_folder_to_delete_" + uuid;
        String path = "/" + folderName;

        RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", path)
                .when()
                .put(resourceUrl)
                .then()
                .statusCode(201);


        Response getResponseBefore = RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", path)
                .when()
                .get(resourceUrl);

        getResponseBefore.then()
                .statusCode(200);

        Response deleteResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", path)
                .when()
                .delete(resourceUrl);

        deleteResponse.then()
                .statusCode(204)
                .body(emptyString());

        Response getResponseAfter = RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", path)
                .when()
                .get(resourceUrl);

        getResponseAfter.then()
                .statusCode(404);


        Response trashResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .when()
                .get(trashUrl);

        trashResponse.then()
                .statusCode(200);
    }

    @Test(description = "TC-22 Безвозвратное удаление папки (permanently=true)")
    public void testPermanentFolderDeletion() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String folderName = "test_folder_permanent_" + uuid;
        String path = "/" + folderName;


        RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", path)
                .when()
                .put(resourceUrl)
                .then()
                .statusCode(201);


        Response deleteResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", path)
                .queryParam("permanently", "true")
                .when()
                .delete(resourceUrl);


        deleteResponse.then()
                .statusCode(204)
                .body(emptyOrNullString());

        Response getResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", path)
                .when()
                .get(resourceUrl);

        getResponse.then()
                .statusCode(404);

        Response trashResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .when()
                .get(trashUrl);

        trashResponse.then()
                .statusCode(200);
    }
}