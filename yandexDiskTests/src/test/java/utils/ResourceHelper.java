package utils;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import models.DownloadResponse;
import models.UploadResponse;

import static base.FileDownloadTest.*;
import static base.FileUploadCopyTest.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;

public class ResourceHelper {

    public static void uploadFile() {
        String uploadUrl = getUploadUrl(inputFilePath);

        given()
                .spec(requestBaseSpec)
                .body(tempFile)
                .when()
                .put(uploadUrl)
                .then()
                .statusCode(201);


        checkFileExists(inputFilePath);
    }

    public static String getUploadUrl(String filePath) {
        Response response = given()
                .spec(requestBaseSpec)
                .queryParam("path", filePath)
                .when()
                .get(resourceUrl + "/upload");

        response.then().statusCode(200);

        UploadResponse uploadResponse = response.as(UploadResponse.class);

        assertThat("Поле 'href' должно быть не пустым",
                uploadResponse.getHref(), not(emptyString()));
        assertThat("Метод должен быть 'PUT'",
                uploadResponse.getMethod(), equalTo("PUT"));

        return uploadResponse.getHref();
    }

    public static void createFolderIfNotExists(String folderPath) {
        try {
            Response response = given()
                    .spec(requestBaseSpec)
                    .queryParam("path", folderPath)
                    .when()
                    .put(resourceUrl);

            if (response.getStatusCode() == 201) {
                addCreatedFolder(folderPath);
            }
        } catch (Exception ignored) {
        }
    }

    public static void deleteFileIfExists(String filePath) {
        try {
            Response response = given()
                    .spec(requestBaseSpec)
                    .queryParam("path", filePath)
                    .when()
                    .get(resourceUrl);

            if (response.getStatusCode() == 200) {
                given()
                        .spec(requestBaseSpec)
                        .queryParam("path", filePath)
                        .when()
                        .delete(resourceUrl)
                        .then()
                        .statusCode(204);
            }
        } catch (Exception ignored) {
        }
    }

    public static File createTestFile() throws IOException {
        File file = new File("data_" + System.currentTimeMillis() + ".txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Test_Test");
        }
        return file;
    }

    public static void checkFileExists(String filePath) {
        given()
                .spec(requestBaseSpec)
                .queryParam("path", filePath)
                .when()
                .get(resourceUrl)
                .then()
                .statusCode(200)
                .body("name", equalTo(FILENAME),"mime_type", notNullValue(),"media_type", notNullValue());
    }

    public static String getDownloadUrl() {
        Response response = given()
                .spec(requestBaseSpec)
                .queryParam("path", fullFilePath)
                .when()
                .get(resourceUrl + "/download");

        response.then()
                .statusCode(200);

        DownloadResponse downloadResponse = response.as(DownloadResponse.class);

        assertThat("Поле 'href' должно присутствовать в ответе",
                downloadResponse.getHref(), notNullValue());
        assertThat("Поле 'href' не должно быть пустым",
                downloadResponse.getHref(), not(emptyString()));

        return downloadResponse.getHref();
    }

    public static void downloadFile(String downloadUrl) throws IOException {
        Response downloadResponse = given()
                .urlEncodingEnabled(false)
                .when()
                .get(downloadUrl);

        downloadedFile = new File("downloaded_" + System.currentTimeMillis() + ".txt");
        Files.write(downloadedFile.toPath(), downloadResponse.asByteArray());

    }

    public static void createFolderOnDisk(String folderPath) {
        Response response = given()
                .spec(requestBaseSpec)
                .queryParam("path", folderPath)
                .when()
                .put(resourceUrl);

        int statusCode = response.getStatusCode();

        if (statusCode == 201 || statusCode == 409) {
            addCreatedFolder(folderPath);
        } else {
            throw new RuntimeException("Не удалось создать папку. Status code: " + statusCode);
        }
    }

    private static void addCreatedFolder(String folderPath) {
        try {
            TestDataHelper.addCreatedFolder(folderPath);
        } catch (Exception ignored) {
        }
    }

    public static ValidatableResponse createFile(String originalPath) {

        return given()
                .spec(requestBaseSpec)
                .queryParam("path", originalPath)
                .when()
                .put(resourceUrl)
                .then()
                .statusCode(201);

    }

    public static void createFileSecondTime(String originalPath) {

        given()
                .spec(requestBaseSpec)
                .queryParam("path", originalPath)
                .when()
                .delete(resourceUrl)
                .then()
                .statusCode(204);
    }

    public static String getTrashPathForResource(String resourceName) {
        try {
            Response response = given()
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
