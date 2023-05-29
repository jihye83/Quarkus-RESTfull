package org.acme.quarkussocial.rest;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.acme.quarkussocial.rest.dto.CreateUserRequest;
import org.acme.quarkussocial.rest.dto.ResponseError;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;


import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
//determinando a order dos testes
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserResourceTest {
    //definindo o endpoint /users como apiURL
    @TestHTTPResource("/users")
    URL apiURL;
    @Test
    @DisplayName("create an User sucessfully")
    @Order(1)
    public void createUserTest() {
        var user = new CreateUserRequest();
        user.setName("Fulano");
        user.setAge(30);

        var response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(apiURL)
                .then()
                .extract().response();
        assertEquals(201, response.statusCode());
        assertNotNull(response.jsonPath().getString("id"));
    }

    @Test
    @DisplayName("return error when json is not valid")
    @Order(2)
    public void createUserValidationErrorTest(){
        var user = new CreateUserRequest();
        user.setName(null);
        user.setAge(null);

        var response = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when()
                .post(apiURL)
                .then()
                .extract().response();
        assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.statusCode());
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        //retornando Lista de errors
        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
//        assertEquals("Age is required", errors.get(0).get("message"));
//        assertEquals("Name is required", errors.get(1).get("message"));
    }

    @Test
    @DisplayName("return list user")
    @Order(3)
    public void listAllUserTest(){
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(apiURL)
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(1));

    }

}