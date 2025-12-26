package base;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

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

        copyFileFirstTime();

        copyFileSecondTime();

    }
}