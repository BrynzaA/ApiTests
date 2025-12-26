package org.tests.base;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tests.models.Comment;
import org.tests.utils.ConfigReader;
import org.tests.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;
import static org.tests.utils.TestDataHelper.generateUniqueContent;
import static org.tests.utils.TestDataHelper.generateUniqueTitle;
import static org.tests.utils.TestResponseHelper.*;

public class CommentTests extends BaseTest {

    private static String restRouteCommentsValue;
    private Set<Integer> createdPostIds = new HashSet<>();
    private Set<Integer> createdCommentIds = new HashSet<>();

    @BeforeClass
    public void commentSetup() {
        restRouteCommentsValue = ConfigReader.getRestRouteCommentsValue();
    }

    @AfterMethod
    public void cleanupAfterTest() {
        deleteCommentsFromDatabase(createdCommentIds);
        createdCommentIds.clear();

        deletePostsFromDatabase(createdPostIds);
        createdPostIds.clear();
    }

    private void deleteCommentsFromDatabase(Set<Integer> commentIds) {
        if (commentIds.isEmpty()) {
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < commentIds.size(); i++) {
                placeholders.append("?");
                if (i < commentIds.size() - 1) {
                    placeholders.append(",");
                }
            }

            String query = "DELETE FROM wp_comments WHERE comment_ID IN (" + placeholders + ")";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                int index = 1;
                for (Integer commentId : commentIds) {
                    stmt.setInt(index++, commentId);
                }
                stmt.executeUpdate();
            }
        } catch (SQLException ignored) {
        }
    }

    private void deletePostsFromDatabase(Set<Integer> postIds) {
        if (postIds.isEmpty()) {
            return;
        }

        try (Connection conn = DatabaseManager.getConnection()) {
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < postIds.size(); i++) {
                placeholders.append("?");
                if (i < postIds.size() - 1) {
                    placeholders.append(",");
                }
            }

            String query = "DELETE FROM wp_posts WHERE ID IN (" + placeholders + ")";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                int index = 1;
                for (Integer postId : postIds) {
                    stmt.setInt(index++, postId);
                }
                int deleted = stmt.executeUpdate();
            }
        } catch (SQLException ignored) {
        }
    }

    private Integer createPostAndTrack(String title, String content, String status) {
        Integer postId = createTestPost(title, content, status);
        if (postId != null) {
            createdPostIds.add(postId);
        }
        return postId;
    }

    private Integer createCommentAndTrack(Integer postId, String content, String authorName, String authorEmail) {
        Integer commentId = createTestComment(postId, content, authorName, authorEmail);
        if (commentId != null) {
            createdCommentIds.add(commentId);
        }
        return commentId;
    }

    @Test(description = "TC-61: Создание комментария к посту")
    public void testCreateCommentForPost() {
        String postTitle = generateUniqueTitle();
        String postContent = generateUniqueContent();
        Integer postId = createPostAndTrack(postTitle, postContent, "publish");

        String commentContent = "Great post! " + generateUniqueTitle();
        String authorName = "Test User";
        String authorEmail = "test.user@example.com";

        Integer commentId = createCommentAndTrack(postId, commentContent, authorName, authorEmail);
        assertNotNull(commentId, "Comment ID should not be null");
        assertTrue(commentId > 0, "Comment ID should be positive");

        String expectedRenderedContent = "<p>" + commentContent + "</p>\n";

        Response commentResponse = getCommentById(commentId);
        commentResponse.then()
                .statusCode(200)
                .body("id", equalTo(commentId),"post", equalTo(postId),
                        "content.rendered", equalTo(expectedRenderedContent), "author_name", equalTo(authorName),
                        "status", equalTo("approved"));
    }

    @Test(description = "TC-62: Получение списка комментариев для поста")
    public void testGetCommentsForPost() {
        Integer postId = createPostAndTrack(generateUniqueTitle(), generateUniqueContent(), "publish");

        Integer comment1Id = createCommentAndTrack(postId, "First comment", "User1", "user1@example.com");
        Integer comment2Id = createCommentAndTrack(postId, "Second comment", "User2", "user2@example.com");

        Response commentsResponse = getCommentsForPost(postId);

        commentsResponse.then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(2)));

        List<Integer> commentIds = commentsResponse.jsonPath().getList("id");
        assertTrue(commentIds.contains(comment1Id), "Should contain first comment");
        assertTrue(commentIds.contains(comment2Id), "Should contain second comment");

        List<Integer> postIds = commentsResponse.jsonPath().getList("post");
        for (Integer id : postIds) {
            assertEquals(id, postId, "All comments should belong to the test post");
        }
    }

    @Test(description = "TC-63: Ответ на комментарий (вложенные комментарии)")
    public void testReplyToComment() {
        Integer postId = createPostAndTrack(generateUniqueTitle(), generateUniqueContent(), "publish");

        Integer parentCommentId = createCommentAndTrack(postId, "Parent comment", "Parent", "parent@example.com");

        Comment reply = new Comment();
        reply.setPost(postId);
        reply.setParent(parentCommentId);
        reply.setContent(new Comment.Content("Reply to parent"));
        reply.setAuthorName("Child");
        reply.setAuthorEmail("child@example.com");
        reply.setStatus("approved");

        String commentsRoute = restRouteCommentsValue;

        Response replyResponse = RestAssured.given()
                .spec(requestSpec)
                .queryParam(restRouteName, commentsRoute)
                .body(reply)
                .when()
                .post()
                .then()
                .statusCode(201)
                .extract()
                .response();

        Integer replyId = replyResponse.jsonPath().getInt("id");
        createdCommentIds.add(replyId);

        replyResponse.then()
                .body("id", equalTo(replyId), "post", equalTo(postId), "parent", equalTo(parentCommentId),
                "content.raw", equalTo("Reply to parent"));

        Response parentResponse = getCommentById(parentCommentId);
        parentResponse.then()
                .statusCode(200)
                .body("id", equalTo(parentCommentId));
    }

    @Test(description = "TC-64: Изменение статуса комментария")
    public void testUpdateCommentStatus() {
        Integer postId = createPostAndTrack(generateUniqueTitle(), generateUniqueContent(), "publish");
        Integer commentId = createCommentAndTrack(postId, "Test comment", "User", "user@example.com");

        Response initialResponse = getCommentById(commentId);
        initialResponse.then()
                .statusCode(200)
                .body("status", equalTo("approved"));

        Comment updateComment = new Comment();
        updateComment.setStatus("hold");

        Response updateResponse = updateComment(commentId, updateComment);
        updateResponse.then()
                .statusCode(200)
                .body("status", equalTo("hold"));

        updateComment.setStatus("spam");
        updateComment(commentId, updateComment)
                .then()
                .statusCode(200)
                .body("status", equalTo("spam"));

        updateComment.setStatus("approved");
        updateComment(commentId, updateComment)
                .then()
                .statusCode(200)
                .body("status", equalTo("approved"));
    }

    @Test(description = "TC-65: Изменение содержимого комментария")
    public void testUpdateCommentContent() {
        Integer postId = createPostAndTrack(generateUniqueTitle(), generateUniqueContent(), "publish");
        String originalContent = "Original comment content";
        Integer commentId = createCommentAndTrack(postId, originalContent, "User", "user@example.com");

        String updatedContent = "Updated comment content with more details";
        Comment updateComment = new Comment();
        updateComment.setContent(new Comment.Content(updatedContent));

        Response updateResponse = updateComment(commentId, updateComment);
        updateResponse.then()
                .statusCode(200)
                .body("content.raw", equalTo(updatedContent), "author_name", equalTo("User"),
                        "post", equalTo(postId));
    }

    @Test(description = "TC-66: Удаление комментария через API")
    public void testDeleteCommentViaApi() {
        Integer postId = createPostAndTrack(generateUniqueTitle(), generateUniqueContent(), "publish");
        Integer commentId = createCommentAndTrack(postId, "Comment to delete", "User", "user@example.com");

        getCommentById(commentId).then()
                .statusCode(200);

        Response deleteResponse = deleteComment(commentId, true);
        deleteResponse.then()
                .statusCode(200)
                .body("deleted", equalTo(true));

        getCommentById(commentId).then()
                .statusCode(404);

        createdCommentIds.remove(commentId);
    }

}