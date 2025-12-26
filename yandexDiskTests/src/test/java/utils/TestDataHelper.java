package utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;

import static base.BaseTest.*;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;

public class TestDataHelper {

    public static void addCreatedFolder(String resourcePath) {
        if (resourcePath != null && !resourcePath.trim().isEmpty()) {
            createdResources.add(resourcePath);
        }
    }

    public static void cleanUpTestData() {
        if (createdResources.isEmpty()) {
            return;
        }

        List<String> sortedResources = new ArrayList<>(createdResources);
        sortedResources.sort((a, b) -> Integer.compare(b.length(), a.length()));


        for (String resourcePath : sortedResources) {
            try {
                deleteResourcePermanently(resourcePath);
            } catch (Exception ignored) {
            }
        }

        createdResources.clear();
    }


    public static void deleteResourcePermanently(String resourcePath) {
        try {
            RestAssured.given()
                    .spec(requestBaseSpec)
                    .queryParam("path", resourcePath)
                    .queryParam("permanently", "true")
                    .when()
                    .delete(resourceUrl)
                    .then()
                    .statusCode(anyOf(equalTo(204), equalTo(202), equalTo(200)));
        } catch (Exception ignored) {
        }
    }

    public static boolean isResourceExists(String resourcePath) {
        try {
            Response response = RestAssured.given()
                    .spec(requestBaseSpec)
                    .queryParam("path", resourcePath)
                    .when()
                    .get(resourceUrl);
            return response.getStatusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
