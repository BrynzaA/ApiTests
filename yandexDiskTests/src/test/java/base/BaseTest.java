package base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import utils.ConfigReader;
import utils.TestDataHelper;

public class BaseTest {

    protected static String authToken;
    protected static String apiBaseUrl;

    public static RequestSpecification requestBaseSpec;

    public static RequestSpecification requestBaseNoAuthSpec;

    public static String resourceUrl;
    public static String trashUrl;


    public static List<String> createdResources = new ArrayList<>();

    @BeforeClass
    public static void setUp() {
        apiBaseUrl = ConfigReader.getApiBaseUrl();
        authToken = ConfigReader.getAuthToken();
        resourceUrl = ConfigReader.getResourceUrl();
        trashUrl = ConfigReader.getTrashUrl();

        RestAssured.baseURI = apiBaseUrl;

        requestBaseSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build()
                .header("Authorization", "OAuth " + authToken);

        requestBaseNoAuthSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();

    }

    @AfterClass
    public static void tearDown() {
        TestDataHelper.cleanUpTestData();
    }
}
