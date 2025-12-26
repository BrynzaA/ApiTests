package base;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

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

        uploadFileToDisk();
    }

    @Test(description = "Тест-кейс №4: Скачивание текстового файла")
    public void testFileDownload() throws IOException {
        String downloadUrl = getDownloadUrl();

        downloadAndCompareFile(downloadUrl);
    }

}