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
    protected static RequestSpecification requestSpec;
    protected static RequestSpecification requestSpecForSinglePost;
    protected static String basicAuthUsername;
    protected static String basicAuthPassword;
    protected static String apiBaseUrl;
    protected static String restRouteName;
    protected static String restRouteValue;
    protected static String restRouteCommentsValue;

    protected String generateUniqueTitle() {
        return "Test Post " + UUID.randomUUID().toString().substring(0, 8);
    }

    protected String generateUniqueContent() {
        return "Test content " + UUID.randomUUID().toString().substring(0, 8);
    }

    protected Date getFutureDate(int daysToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
        return calendar.getTime();
    }

    protected String formatDateForAPI(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.format(date);
    }

    @BeforeClass
    public void setup() {
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

    protected Integer createTestPost(String title, String content, String status) {
        Post post = new Post();
        post.setTitle(new Post.Title(title));
        post.setContent(new Post.Content(content));
        if (status != null) {
            post.setStatus(status);
        }

        Response response = given()
                .spec(requestSpec)
                .body(post)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .response();

        return response.jsonPath().getInt("id");
    }

    protected Response getPostById(Integer postId) {
        String fullRestRoute = restRouteValue + "/" + postId;

        return RestAssured.given()
                .spec(requestSpecForSinglePost)
                .queryParam(restRouteName, fullRestRoute)
                .when()
                .get()
                .then()
                .extract()
                .response();
    }

    protected Response deletePost(Integer postId, boolean force) {
        String fullRestRoute = restRouteValue + "/" + postId;

        return RestAssured.given()
                .spec(requestSpec)
                .queryParam(restRouteName, fullRestRoute)
                .queryParam("force", force)
                .when()
                .delete()
                .then()
                .log().all()
                .extract()
                .response();
    }

    protected Response updatePost(Integer postId, Post post) {
        String fullRestRoute = restRouteValue + "/" + postId;

        return RestAssured.given()
                .spec(requestSpecForSinglePost)
                .queryParam(restRouteName, fullRestRoute)
                .body(post)
                .when()
                .put()
                .then()
                .extract()
                .response();
    }

    protected Integer createTestComment(Integer postId, String content, String authorName, String authorEmail) {
        Comment comment = new Comment();
        comment.setPost(postId);
        comment.setContent(new Comment.Content(content));
        comment.setAuthorName(authorName);
        comment.setAuthorEmail(authorEmail);
        comment.setStatus("approved");

        String fullRestRoute = restRouteCommentsValue;

        Response response = RestAssured.given()
                .spec(requestSpec)
                .queryParam(restRouteName, fullRestRoute)
                .body(comment)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .response();

        return response.jsonPath().getInt("id");
    }

    protected Response getCommentById(Integer commentId) {
        String fullRestRoute = restRouteCommentsValue + "/" + commentId;


        return RestAssured.given()
                .spec(requestSpec)
                .queryParam(restRouteName, fullRestRoute)
                .when()
                .get()
                .then()
                .extract()
                .response();
    }

    protected Response getAllComments() {
        String fullRestRoute = restRouteCommentsValue;

        return RestAssured.given()
                .spec(requestSpec)
                .queryParam(restRouteName, fullRestRoute)
                .when()
                .get()
                .then()
                .extract()
                .response();
    }

    protected Response getCommentsForPost(Integer postId) {
        String fullRestRoute = restRouteCommentsValue;

        return RestAssured.given()
                .spec(requestSpec)
                .queryParam(restRouteName, fullRestRoute)
                .queryParam("post", postId)
                .when()
                .get()
                .then()
                .extract()
                .response();
    }

    protected Response updateComment(Integer commentId, Comment comment) {
        String fullRestRoute = restRouteCommentsValue + "/" + commentId;

        return RestAssured.given()
                .spec(requestSpec)
                .queryParam(restRouteName, fullRestRoute)
                .body(comment)
                .when()
                .put()
                .then()
                .extract()
                .response();
    }

    protected Response deleteComment(Integer commentId, boolean force) {
        String fullRestRoute = restRouteCommentsValue + "/" + commentId;

        return RestAssured.given()
                .spec(requestSpec)
                .queryParam(restRouteName, fullRestRoute)
                .queryParam("force", force)
                .when()
                .delete()
                .then()
                .extract()
                .response();
    }
}