package org.tests.base;

import io.restassured.response.Response;
import org.testng.annotations.Test;
import org.tests.utils.DatabaseManager;

import java.sql.SQLException;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class PostDeleteTests extends BaseTest {

    @Test(description = "TC-41: Удаление поста (с параметром force=true)")
    public void testDeletePostWithForce() throws SQLException {
        String title = generateUniqueTitle();
        String content = generateUniqueContent();
        Integer postId = createTestPost(title, content, "publish");

        Map<String, Object> dbPostBefore = DatabaseManager.getPostById(postId);
        assertNotNull(dbPostBefore, "Post should exist in database before deletion");

        Response getResponseBefore = getPostById(postId);
        getResponseBefore.then()
                .statusCode(200)
                .body("id", equalTo(postId));

        Response response = deletePost(postId, true);

        response.then()
                .statusCode(200)
                .body("deleted", equalTo(true))
                .body("previous.id", equalTo(postId));

        assertFalse(DatabaseManager.isPostExists(postId), "Post should not exist in database after deletion");

        Response getResponseAfter = getPostById(postId);
        getResponseAfter.then()
                .statusCode(404);
    }

    @Test(description = "TC-42: Проверка в БД: запись удалена из wp_posts")
    public void testDatabaseAfterDeletion() throws SQLException {
        String title = generateUniqueTitle();
        String content = generateUniqueContent();
        Integer postId = createTestPost(title, content, "publish");

        assertTrue(DatabaseManager.isPostExists(postId), "Post should exist in database before deletion");

        Response getResponseBefore = getPostById(postId);
        getResponseBefore.then()
                .statusCode(200);

        Response deleteResponse = deletePost(postId, true);
        deleteResponse.then()
                .statusCode(200)
                .body("deleted", equalTo(true));

        assertFalse(DatabaseManager.isPostExists(postId), "Post should not exist in database after deletion");

        Response getResponseAfter = getPostById(postId);
        getResponseAfter.then()
                .statusCode(404);
    }
}