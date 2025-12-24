package org.tests.base;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.tests.models.Comment;
import org.tests.models.Post;
import org.tests.utils.ConfigReader;
import org.tests.utils.DatabaseManager;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class BaseTest {
    public static RequestSpecification requestSpec;
    public static RequestSpecification requestSpecForSinglePost;
    protected static String basicAuthUsername;
    protected static String basicAuthPassword;
    protected static String apiBaseUrl;
    public static String restRouteName;
    public static String restRouteValue;
    public static String restRouteCommentsValue;


    @BeforeClass
    public void setUp() {
        basicAuthUsername = ConfigReader.getValidBasicUsername();
        basicAuthPassword = ConfigReader.getValidBasicPassword();
        apiBaseUrl = ConfigReader.getApiBaseUrl();
        restRouteName = ConfigReader.getRestRouteName();
        restRouteValue = ConfigReader.getRestRouteValue();
        restRouteCommentsValue = ConfigReader.getRestRouteCommentsValue();

        RestAssured.baseURI = apiBaseUrl;

        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addQueryParam(restRouteName, restRouteValue)
                .setAuth(RestAssured.preemptive().basic(basicAuthUsername, basicAuthPassword))
                .build();

        requestSpecForSinglePost = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAuth(RestAssured.preemptive().basic(basicAuthUsername, basicAuthPassword))
                .build();

        try {
            Class.forName(ConfigReader.getDbDriver());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load database driver: " + e.getMessage());
        }
    }

    @AfterClass
    public void tearDown() throws SQLException {
        DatabaseManager.cleanupTestData();
        DatabaseManager.closeConnection();
    }
}