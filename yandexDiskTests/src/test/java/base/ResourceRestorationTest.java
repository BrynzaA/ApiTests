package base;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.testng.annotations.Test;

import utils.ResourceHelper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static utils.ResourceHelper.getTrashPathForResource;
import static utils.TestDataHelper.addCreatedFolder;

public class ResourceRestorationTest extends BaseTest {

    @Test(description = "TC-31 Восстановление папки из Корзины")
    public void testRestoreFolderFromTrash() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String folderName = "test_folder_restore_" + uuid;
        String originalPath = "/" + folderName;

        ResourceHelper.createFile(originalPath);

        ResourceHelper.createFileSecondTime(originalPath);


        String trashPath = getTrashPathForResource(folderName);
        if (trashPath == null) {
            throw new RuntimeException("Не удалось найти папку в корзине: " + folderName);
        }

        given()
                .spec(requestBaseSpec)
                .queryParam("path", trashPath)
                .when()
                .put(trashUrl + "/restore")
                .then()
                .statusCode(201)
                .body("href", notNullValue());


       given()
                .spec(requestBaseSpec)
                .queryParam("path", originalPath)
                .when()
                .get(resourceUrl)
               .then()
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


        ResourceHelper.createFile(originalPath);

        ResourceHelper.createFileSecondTime(originalPath);


        String trashPath = getTrashPathForResource(originalFolderName);
        if (trashPath == null) {
            throw new RuntimeException("Не удалось найти папку в корзине: " + originalFolderName);
        }


        given()
                .spec(requestBaseSpec)
                .queryParam("path", trashPath)
                .queryParam("name", newFolderName)
                .when()
                .put(trashUrl + "/restore")
                .then()
                .statusCode(201)
                .body("href", notNullValue());


        String newPath = "/" + newFolderName;

        given()
                .spec(requestBaseSpec)
                .queryParam("path", newPath)
                .when()
                .get(resourceUrl)
                .then()
                .statusCode(200)
                .body("name", equalTo(newFolderName));


        given()
                .spec(requestBaseSpec)
                .queryParam("path", originalPath)
                .when()
                .get(resourceUrl)
                .then()
                .statusCode(404);


        addCreatedFolder(newPath);
    }

    @Test(description = "TC-33 Восстановление папки с указанием возвращаемых полей")
    public void testRestoreFolderWithFields() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String folderName = "test_folder_fields_" + uuid;
        String originalPath = "/" + folderName;

        ResourceHelper.createFile(originalPath);

        ResourceHelper.createFileSecondTime(originalPath);


        String trashPath = getTrashPathForResource(folderName);
        if (trashPath == null) {
            throw new RuntimeException("Не удалось найти папку в корзине: " + folderName);
        }


        given()
                .spec(requestBaseSpec)
                .queryParam("path", trashPath)
                .queryParam("fields", "path,name,type")
                .when()
                .put(trashUrl + "/restore")
                .then()
                .statusCode(201);


        given()
                .spec(requestBaseSpec)
                .queryParam("path", originalPath)
                .when()
                .get(resourceUrl)
                .then()
                .statusCode(200)
                .body("name", equalTo(folderName));

        addCreatedFolder(originalPath);
    }
}