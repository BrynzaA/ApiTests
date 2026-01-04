package base;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import models.ErrorResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static utils.ResourceHelper.*;

public class FileUploadCopyTest extends BaseTest {

    private static final String INPUT_FOLDER = "/input_data";
    private static final String OUTPUT_FOLDER = "/output_data";
    public static final String FILENAME = "data.txt";

    public static File tempFile;
    public static String uniqueSuffix;
    public static String inputFolderPath;
    public static String outputFolderPath;
    public static String inputFilePath;
    public static String outputFilePath;

    @BeforeClass
    public void setUpTestData() throws IOException {
        uniqueSuffix = "_" + UUID.randomUUID().toString().substring(0, 8);

        inputFolderPath = INPUT_FOLDER + uniqueSuffix;
        outputFolderPath = OUTPUT_FOLDER + uniqueSuffix;
        inputFilePath = inputFolderPath + "/" + FILENAME;
        outputFilePath = outputFolderPath + "/" + FILENAME;

        tempFile = createTestFile();

        createFolderIfNotExists(inputFolderPath);
        createFolderIfNotExists(outputFolderPath);

        deleteFileIfExists(inputFilePath);
        deleteFileIfExists(outputFilePath);
    }

    @Test(description = "Тест-кейс №3: Загрузка и копирование файла")
    public void testFileUploadAndCopy() {
        uploadFile();

        given()
                .spec(requestBaseSpec)
                .contentType(ContentType.JSON)
                .queryParam("from", inputFilePath)
                .queryParam("path", outputFilePath)
                .when()
                .post(resourceUrl + "/copy")
                .then()
                .statusCode(201)
                .body("href", notNullValue(),"method", notNullValue(),"templated", notNullValue());


        checkFileExists(outputFilePath);

        Response copyResponse = given()
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
}