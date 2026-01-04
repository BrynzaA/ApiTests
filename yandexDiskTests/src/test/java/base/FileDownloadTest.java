package base;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static utils.ResourceHelper.*;

public class FileDownloadTest extends BaseTest {

    private static final String FOLDER_NAME = "/sdet_data";
    private static final String FILENAME = "data.txt";
    private static final String FILE_PATH = FOLDER_NAME + "/" + FILENAME;

    public static File originalFile;
    public static File downloadedFile;
    public static String uniqueSuffix;
    public static String fullFilePath;

    @BeforeClass
    public void setUpTestData() throws IOException {
        uniqueSuffix = "_" + UUID.randomUUID().toString().substring(0, 8);
        fullFilePath = FOLDER_NAME + uniqueSuffix + "/" + FILENAME;

        originalFile = createTestFile();

        createFolderOnDisk(FOLDER_NAME + uniqueSuffix);

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

    @Test(description = "Тест-кейс №4: Скачивание текстового файла")
    public void testFileDownload() throws IOException {
        String downloadUrl = getDownloadUrl();

        downloadFile(downloadUrl);

        String originalContent = new String(Files.readAllBytes(originalFile.toPath()));

        String downloadedContent = new String(Files.readAllBytes(downloadedFile.toPath()));

        assertThat("Содержимое файлов должно совпадать",
                downloadedContent, equalTo(originalContent));
    }

}