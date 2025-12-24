package org.tests.base;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.tests.utils.DatabaseManager;

import java.sql.SQLException;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class PostReadTests extends BaseTest {

    @Test(description = "TC-21: Получение списка всех постов")
    public void testGetAllPosts() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("[0].id", notNullValue())
                .body("[0].title", notNullValue())
                .extract()
                .response();

        List<Map<String, Object>> posts = response.jsonPath().getList("$");
        assertTrue(posts.size() > 0, "Should return at least one post");

        Map<String, Object> firstPost = posts.get(0);
        assertTrue(firstPost.containsKey("id"), "Post should have id");
        assertTrue(firstPost.containsKey("title"), "Post should have title");
        assertTrue(firstPost.containsKey("content"), "Post should have content");
    }

    @Test(description = "TC-22: Получение конкретного поста по ID")
    public void testGetPostById() throws SQLException {
        String title = generateUniqueTitle();
        String content = generateUniqueContent();
        Integer postId = createTestPost(title, content, "publish");

        Map<String, Object> dbPost = DatabaseManager.getPostById(postId);
        assertNotNull(dbPost, "Post should exist in database");
        assertEquals(dbPost.get("post_title"), title, "Title should match in database");

        Response response = getPostById(postId);

        response.then()
                .statusCode(200)
                .body("id", equalTo(postId))
                .body("title.rendered", equalTo(title))
                .body("content.rendered", containsString(content.substring(0, Math.min(content.length(), 20))))
                .body("status", equalTo("publish"));

        String responseTitle = response.jsonPath().getString("title.rendered");
        String responseContent = response.jsonPath().getString("content.rendered");

        assertEquals(responseTitle, title, "Title should match");
        assertTrue(responseContent.contains(content), "Content should contain the created content");

    }
}