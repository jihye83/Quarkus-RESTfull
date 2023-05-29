package org.acme.quarkussocial.rest;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.quarkussocial.domain.model.User;
import org.acme.quarkussocial.domain.repository.UserRepository;
import org.acme.quarkussocial.rest.dto.CreatePostRequest;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
//utilizando o endpoint da classe PostResource
@TestHTTPEndpoint(PostResource.class)
//determinando a order dos testes
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    Long userId;

    //criando um cenario com usuario
    @BeforeEach
    @Transactional
    public void setUP() {
        var user = new User();
        user.setAge(30);
        user.setName("Ji Hye");
        userRepository.persist(user);
        userId = user.getId();
    }

    @Test
    @DisplayName("create a post for a user")
    public void createPostTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("some text");

        //definindo o valor do ID
//        var useID = 1;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", userId)
                .when()
                //como foi feito a annotation nao precisa passar nada
                .post()
                .then()
                .statusCode(201);
    }
    //Usuario nao existente
    @Test
    @DisplayName("return 404 when to make a post for an inexistent user. ")
    public void postNonexistentUserTest() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("some text");
        //pode ser criado uma variavel e passar ou valor ou colocar direto no parametro.
        //var nonexistentUser = 999;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId", 999)
                .when()
                //como foi feito a annotation nao precisa passar nada
                .post()
                .then()
                .statusCode(404);
    }

}