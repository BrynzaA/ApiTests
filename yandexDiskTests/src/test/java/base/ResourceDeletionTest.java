package base;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ResourceDeletionTest extends BaseTest {

    @Test(description = "TC-21 Удаление папки с перемещением в Корзину")
    public void testDeleteFolderToTrash() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String folderName = "test_folder_to_delete_" + uuid;
        String path = "/" + folderName;

        createdResources.add(path);

        given()
                .spec(requestBaseSpec)
                .queryParam("path", path)
                .when()
                .get(resourceUrl)
                .then()
                .statusCode(200);

        given()
                .spec(requestBaseSpec)
                .queryParam("path", path)
                .when()
                .delete(resourceUrl)
                .then()
                .statusCode(204)
                .body(emptyString());

        given()
                .spec(requestBaseSpec)
                .queryParam("path", path)
                .when()
                .get(resourceUrl)
                .then()
                .statusCode(404);


        given()
                .spec(requestBaseSpec)
                .when()
                .get(trashUrl)
                .then()
                .statusCode(200);
    }

    @Test(description = "TC-22 Безвозвратное удаление папки (permanently=true)")
    public void testPermanentFolderDeletion() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String folderName = "test_folder_permanent_" + uuid;
        String path = "/" + folderName;


        createdResources.add(path);


       given()
                .spec(requestBaseSpec)
                .queryParam("path", path)
                .queryParam("permanently", "true")
                .when()
                .delete(resourceUrl)
                .then()
                .statusCode(204)
                .body(emptyOrNullString());

        given()
                .spec(requestBaseSpec)
                .queryParam("path", path)
                .when()
                .get(resourceUrl)
                .then()
                .statusCode(404);

        given()
                .spec(requestBaseSpec)
                .when()
                .get(trashUrl)
                .then()
                .statusCode(200);
    }
}