package org.tests.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.tests.models.Post;
import org.tests.utils.ConfigReader;
import org.tests.utils.DatabaseManager;
import org.tests.utils.TestDataHelper;

import java.sql.SQLException;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;
import static org.tests.utils.TestDataHelper.*;

public class PostCreationTests extends BaseTest {


    @Test(description = "TC-11: Создание поста с минимальными данными")
    public void testCreatePostWithMinimumData() {
        String title = generateUniqueTitle();
        String content = generateUniqueContent();

        Post post = new Post();
        post.setTitle(new Post.Title(title));
        post.setContent(new Post.Content(content));

        Response response = given()
                .spec(requestSpec)
                .body(post)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("title.raw", equalTo(title), "content.raw", equalTo(content),
                        "status", equalTo("draft"), "id", notNullValue())
                .extract()
                .response();
    }

    @Test(description = "TC-12: Создание поста со всеми полями")
    public void testCreatePostWithAllFields() {
        String title = generateUniqueTitle();
        String content = generateUniqueContent();
        String slug = "test-slug-" + UUID.randomUUID().toString().substring(0, 8);
        Date futureDate = getFutureDate(1);

        Post post = new Post();
        post.setTitle(new Post.Title(title));
        post.setContent(new Post.Content(content));
        post.setSlug(slug);
        post.setStatus("future");
        post.setDate(futureDate);
        post.setDateGmt(futureDate);
        post.setCommentStatus("open");
        post.setPingStatus("open");
        post.setFormat("standard");
        post.setSticky(false);
        post.setTemplate("");

        Response response = given()
                .spec(requestSpec)
                .body(post)
                .when()
                .post()
                .then()
                .statusCode(201)
                .body("title.raw", equalTo(title), "content.raw", equalTo(content),
                        "slug", equalTo(slug), "status", equalTo("future"), "comment_status", equalTo("open"),
                        "ping_status", equalTo("open"), "format", equalTo("standard"),
                        "sticky", equalTo(false))
                .extract()
                .response();
    }


    @Test(description = "TC-13: Проверка в БД запись появилась в wp_posts с правильными параметрами")
    public void testDatabaseRecordAfterCreation() throws SQLException {
        String title = generateUniqueTitle();
        String content = generateUniqueContent();
        String slug = "db-test-" + UUID.randomUUID().toString().substring(0, 8);

        Post post = new Post();
        post.setTitle(new Post.Title(title));
        post.setContent(new Post.Content(content));
        post.setSlug(slug);
        post.setStatus("draft");

        Response response = given()
                .spec(requestSpec)
                .body(post)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .response();

        int postId = response.jsonPath().getInt("id");

        Map<String, Object> dbPost = DatabaseManager.getPostById(postId);
        assertNotNull(dbPost, "Post should exist in database");
        assertEquals(dbPost.get("post_title"), title, "Title should match in database");
        assertEquals(dbPost.get("post_content"), content, "Content should match in database");
        assertEquals(dbPost.get("post_status"), "draft", "Status should match in database");
        assertEquals(dbPost.get("post_name"), slug, "Slug should match in database");
    }

    @Test(description = "TC-14: Проверка статусов поста")
    public void testPostStatuses() {
        Map<String, String> statuses = new HashMap<>();
        statuses.put("draft", "черновик");
        statuses.put("publish", "опубликованный");
        statuses.put("pending", "на модерации");
        statuses.put("future", "запланированный");

        for (Map.Entry<String, String> entry : statuses.entrySet()) {
            String status = entry.getKey();
            String description = entry.getValue();

            Post post = new Post();
            post.setTitle(new Post.Title("Test " + description + " - " + generateUniqueTitle()));
            post.setContent(new Post.Content("Content for " + description));
            post.setStatus(status);

            if (status.equals("future")) {
                post.setDate(getFutureDate(1));
            }

            Response response = given()
                    .spec(requestSpec)
                    .body(post)
                    .when()
                    .post()
                    .then()
                    .statusCode(201)
                    .body("status", equalTo(status))
                    .extract()
                    .response();

            int postId = response.jsonPath().getInt("id");
            assertTrue(postId > 0, description + " post should be created successfully");
        }
    }
}