package utils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import base.BaseTest;
import models.DownloadResponse;
import models.ErrorResponse;
import models.UploadResponse;

import static base.FileDownloadTest.*;
import static base.FileUploadCopyTest.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;

public class ResourceHelper {

    public static void uploadFile() {
        String uploadUrl = getUploadUrl(inputFilePath);

        Response uploadResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .body(tempFile)
                .when()
                .put(uploadUrl);

        uploadResponse.then()
                .statusCode(201);


        checkFileExists(inputFilePath);
    }

    public static void copyFileFirstTime() {

        Response copyResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .contentType(ContentType.JSON)
                .queryParam("from", inputFilePath)
                .queryParam("path", outputFilePath)
                .when()
                .post(resourceUrl + "/copy");

        copyResponse.then()
                .statusCode(201)
                .body("href", notNullValue(),"method", notNullValue(),"templated", notNullValue());


        checkFileExists(outputFilePath);
    }

    public static void copyFileSecondTime() {

        Response copyResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .contentType(ContentType.JSON)
                .queryParam("from", inputFilePath)
                .queryParam("path", outputFilePath)
                .when()
                .post(resourceUrl + "/copy");

        copyResponse.then()
                .statusCode(409);

        ErrorResponse errorResponse = copyResponse.as(ErrorResponse.class);

        assertThat("Поле 'error' должно присутствовать",
                errorResponse.getError(), notNullValue());
        assertThat("Поле 'description' должно присутствовать",
                errorResponse.getDescription(), notNullValue());
        assertThat("Поле 'message' должно присутствовать",
                errorResponse.getMessage(), notNullValue());
    }

    private static String getUploadUrl(String filePath) {
        Response response = RestAssured.given()
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
            Response response = RestAssured.given()
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
            Response response = RestAssured.given()
                    .spec(requestBaseSpec)
                    .queryParam("path", filePath)
                    .when()
                    .get(resourceUrl);

            if (response.getStatusCode() == 200) {
                RestAssured.given()
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

    private static void checkFileExists(String filePath) {
        Response response = RestAssured.given()
                .spec(requestBaseSpec)
                .queryParam("path", filePath)
                .when()
                .get(resourceUrl);

        response.then()
                .statusCode(200)
                .body("name", equalTo(FILENAME),"mime_type", notNullValue(),"media_type", notNullValue());
    }

    public static String getDownloadUrl() {
        Response response = RestAssured.given()
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

    public static void downloadAndCompareFile(String downloadUrl) throws IOException {
        Response downloadResponse = RestAssured.given()
                .urlEncodingEnabled(false)
                .when()
                .get(downloadUrl);

        downloadedFile = new File("downloaded_" + System.currentTimeMillis() + ".txt");
        Files.write(downloadedFile.toPath(), downloadResponse.asByteArray());

        compareFileContents();
    }

    private static void compareFileContents() throws IOException {
        String originalContent = new String(Files.readAllBytes(originalFile.toPath()));

        String downloadedContent = new String(Files.readAllBytes(downloadedFile.toPath()));

        assertThat("Содержимое файлов должно совпадать",
                downloadedContent, equalTo(originalContent));
    }

    public static void uploadFileToDisk() throws IOException {
        String uploadUrl = getUploadUrl(fullFilePath);

        if (uploadUrl == null || uploadUrl.isEmpty()) {
            throw new RuntimeException("Не удалось получить ссылку для загрузки файла");
        }

        Response uploadResponse = RestAssured.given()
                .spec(requestBaseSpec)
                .body(originalFile)
                .when()
                .put(uploadUrl);

        int statusCode = uploadResponse.getStatusCode();

        if (statusCode != 200 && statusCode != 201) {
            throw new RuntimeException("Не удалось загрузить файл. Status code: " + statusCode);
        }
    }

    public static void createFolderOnDisk(String folderPath) {
        Response response = RestAssured.given()
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

    public static void uploadFileWithPath(String filePath, String content) throws IOException {
        Path tempFilePath = Files.createTempFile("test_", ".txt");
        Files.write(tempFilePath, content.getBytes());

        File tempFile = tempFilePath.toFile();

        try {
            String uploadUrl = getUploadUrl(filePath);

            Response uploadResponse = RestAssured.given()
                    .spec(requestBaseSpec)
                    .body(tempFile)
                    .when()
                    .put(uploadUrl);

            uploadResponse.then()
                    .statusCode(201);

            checkFileExists(filePath);

        } finally {
            tempFile.delete();
        }
    }
}
