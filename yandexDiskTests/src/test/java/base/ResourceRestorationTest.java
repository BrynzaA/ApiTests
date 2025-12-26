package base;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static utils.TestDataHelper.addCreatedFolder;

public class ResourceRestorationTest extends BaseTest {

    @Test(description = "TC-31 Восстановление папки из Корзины")
    public void testRestoreFolderFromTrash() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String folderName = "test_folder_restore_" + uuid;
        String originalPath = "/" + folderName;

        RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", originalPath)
                .when()
                .put(resourceUrl)
                .then()
                .statusCode(201);


        RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", originalPath)
                .when()
                .delete(resourceUrl)
                .then()
                .statusCode(204);


        String trashPath = getTrashPathForResource(folderName);
        if (trashPath == null) {
            throw new RuntimeException("Не удалось найти папку в корзине: " + folderName);
        }

        Response restoreResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", trashPath)
                .when()
                .put(trashUrl + "/restore");

        restoreResponse.then()
                .statusCode(201)
                .body("href", notNullValue());


        Response checkResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", originalPath)
                .when()
                .get(resourceUrl);

        checkResponse.then()
                .statusCode(200)
                .body("name", equalTo(folderName), "type", equalTo("dir"));



        addCreatedFolder(originalPath);

    }

    @Test(description = "TC-32 Восстановление папки с переименованием (name parameter)")
    public void testRestoreFolderWithRename() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String originalFolderName = "old_folder_" + uuid;
        String newFolderName = "new_folder_name_" + uuid;
        String originalPath = "/" + originalFolderName;


        RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", originalPath)
                .when()
                .put(resourceUrl)
                .then()
                .statusCode(201);

        RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", originalPath)
                .when()
                .delete(resourceUrl)
                .then()
                .statusCode(204);


        String trashPath = getTrashPathForResource(originalFolderName);
        if (trashPath == null) {
            throw new RuntimeException("Не удалось найти папку в корзине: " + originalFolderName);
        }


        Response restoreResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", trashPath)
                .queryParam("name", newFolderName)
                .when()
                .put(trashUrl + "/restore");

        restoreResponse.then()
                .statusCode(201)
                .body("href", notNullValue());


        String newPath = "/" + newFolderName;

        Response checkNewResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", newPath)
                .when()
                .get(resourceUrl);

        checkNewResponse.then()
                .statusCode(200)
                .body("name", equalTo(newFolderName));


        Response checkOldResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", originalPath)
                .when()
                .get(resourceUrl);

        checkOldResponse.then()
                .statusCode(404);


        addCreatedFolder(newPath);
    }

    @Test(description = "TC-33 Восстановление папки с указанием возвращаемых полей")
    public void testRestoreFolderWithFields() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String folderName = "test_folder_fields_" + uuid;
        String originalPath = "/" + folderName;

        RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", originalPath)
                .when()
                .put(resourceUrl)
                .then()
                .statusCode(201);


        RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", originalPath)
                .when()
                .delete(resourceUrl)
                .then()
                .statusCode(204);


        String trashPath = getTrashPathForResource(folderName);
        if (trashPath == null) {
            throw new RuntimeException("Не удалось найти папку в корзине: " + folderName);
        }


        Response restoreResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", trashPath)
                .queryParam("fields", "path,name,type")
                .when()
                .put(trashUrl + "/restore");

        restoreResponse.then()
                .statusCode(201);


        Response checkResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", originalPath)
                .when()
                .get(resourceUrl);

        checkResponse.then()
                .statusCode(200)
                .body("name", equalTo(folderName));

        addCreatedFolder(originalPath);
    }

    private String getTrashPathForResource(String resourceName) {
        try {
            Response response = RestAssured.given()
                    .spec(requestBaseSpec)
                    .when()
                    .get(trashUrl);

            JsonPath jsonPath = response.jsonPath();
            List<Map<String, Object>> items = jsonPath.getList("_embedded.items");

            if (items == null || items.isEmpty()) {
                return null;
            }

            for (Map<String, Object> item : items) {
                String name = (String) item.get("name");
                if (resourceName.equals(name)) {
                    return (String) item.get("path");
                }
            }

            return null;

        } catch (Exception e) {
            return null;
        }
    }
}