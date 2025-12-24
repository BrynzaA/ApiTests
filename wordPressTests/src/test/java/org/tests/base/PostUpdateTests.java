package org.tests.base;

import io.restassured.response.Response;
import java.sql.SQLException;
import java.util.Map;
import org.testng.annotations.Test;
import org.tests.models.Post;
import org.tests.utils.DatabaseManager;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.tests.utils.TestDataHelper.generateUniqueContent;
import static org.tests.utils.TestDataHelper.generateUniqueTitle;
import static org.tests.utils.TestResponseHelper.createTestPost;
import static org.tests.utils.TestResponseHelper.updatePost;

public class PostUpdateTests extends BaseTest {

    @Test(description = "TC-31: Изменение заголовка существующего поста")
    public void testUpdatePostTitle() throws SQLException {
        String originalTitle = generateUniqueTitle();
        String content = generateUniqueContent();
        Integer postId = createTestPost(originalTitle, content, "publish");

        String newTitle = "Updated Title " + generateUniqueTitle();
        Post updatePost = new Post();
        updatePost.setTitle(new Post.Title(newTitle));

        Response response = updatePost(postId, updatePost);

        response.then()
                .statusCode(200)
                .body("id", equalTo(postId), "title.raw", equalTo(newTitle), "content.raw", equalTo(content));

        Map<String, Object> dbPost = DatabaseManager.getPostById(postId);
        assertEquals(dbPost.get("post_title"), newTitle, "Title should be updated in database");
    }


    @Test(description = "TC-32: Изменение контента поста")
    public void testUpdatePostContent() throws SQLException {
        String title = generateUniqueTitle();
        String originalContent = generateUniqueContent();
        Integer postId = createTestPost(title, originalContent, "publish");

        String newContent = "Updated content with more details " + generateUniqueTitle();
        Post updatePost = new Post();
        updatePost.setContent(new Post.Content(newContent));

        Response response = updatePost(postId, updatePost);

         response
                .then()
                .statusCode(200)
                .body("id", equalTo(postId), "title.raw", equalTo(title), "content.raw", equalTo(newContent))
                .extract()
                .response();

        Map<String, Object> dbPost = DatabaseManager.getPostById(postId);
        assertEquals(dbPost.get("post_content"), newContent, "Content should be updated in database");
    }

    @Test(description = "TC-33: Изменение статуса поста")
    public void testUpdatePostStatus() throws SQLException {
        String title = generateUniqueTitle();
        String content = generateUniqueContent();
        Integer postId = createTestPost(title, content, "draft");

        Map<String, Object> initialDbPost = DatabaseManager.getPostById(postId);
        assertEquals(initialDbPost.get("post_status"), "draft", "Initial status should be draft");

        Post updatePost = new Post();
        updatePost.setStatus("publish");

        Response response = updatePost(postId, updatePost);

        response
                .then()
                .statusCode(200)
                .body("id", equalTo(postId), "status", equalTo("publish"))
                .extract()
                .response();

        Map<String, Object> updatedDbPost = DatabaseManager.getPostById(postId);
        assertEquals(updatedDbPost.get("post_status"), "publish", "Status should be updated to publish in database");

        updatePost.setStatus("private");

        updatePost(postId, updatePost)
                .then()
                .statusCode(200)
                .body("status", equalTo("private"));
    }

    @Test(description = "TC-34: Проверка в БД: обновленные данные правильно записались")
    public void testDatabaseAfterMultipleUpdates() throws SQLException {
        String originalTitle = generateUniqueTitle();
        String originalContent = generateUniqueContent();
        Integer postId = createTestPost(originalTitle, originalContent, "draft");

        String updatedTitle = "Fully Updated Title " + generateUniqueTitle();
        String updatedContent = "Fully updated content " + generateUniqueTitle();
        String rawSlug = "fully-updated-" + generateUniqueTitle();

        Post updatePost = new Post();
        updatePost.setTitle(new Post.Title(updatedTitle));
        updatePost.setContent(new Post.Content(updatedContent));
        updatePost.setSlug(rawSlug);
        updatePost.setStatus("publish");
        updatePost.setCommentStatus("closed");
        updatePost.setPingStatus("closed");

        Response response = updatePost(postId, updatePost)
                .then()
                .statusCode(200)
                .body("title.raw", equalTo(updatedTitle), "content.raw", equalTo(updatedContent),
                        "status", equalTo("publish"), "comment_status", equalTo("closed"),
                        "ping_status", equalTo("closed"))
                .extract()
                .response();

        String actualSlug = response.jsonPath().getString("slug");

        response.then()
                .body("slug", equalTo(actualSlug));

        Map<String, Object> dbPost = DatabaseManager.getPostById(postId);
        assertNotNull(dbPost, "Post should exist in database");
        assertEquals(dbPost.get("post_title"), updatedTitle, "Title should match");
        assertEquals(dbPost.get("post_content"), updatedContent, "Content should match");
        assertEquals(dbPost.get("post_status"), "publish", "Status should match");
        assertEquals(dbPost.get("post_name"), actualSlug, "Slug should match");
        assertEquals(dbPost.get("comment_status"), "closed", "Comment status should match");
        assertEquals(dbPost.get("ping_status"), "closed", "Ping status should match");
    }
}
