package org.tests.utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.tests.models.Comment;
import org.tests.models.Post;

import static io.restassured.RestAssured.given;
import static org.tests.base.BaseTest.*;

public class TestResponseHelper {

    public static Integer createTestPost(String title, String content, String status) {
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

    public static Response getPostById(Integer postId) {
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

    public static Response deletePost(Integer postId, boolean force) {
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

    public static Response updatePost(Integer postId, Post post) {
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

    public static Integer createTestComment(Integer postId, String content, String authorName, String authorEmail) {
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

    public static Response getCommentById(Integer commentId) {
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

    public static Response getAllComments() {
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

    public static Response getCommentsForPost(Integer postId) {
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

    public static Response updateComment(Integer commentId, Comment comment) {
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

    public static Response deleteComment(Integer commentId, boolean force) {
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
