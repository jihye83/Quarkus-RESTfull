package org.acme.quarkussocial.rest;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.quarkussocial.domain.model.Follower;
import org.acme.quarkussocial.domain.model.Post;
import org.acme.quarkussocial.domain.model.User;
import org.acme.quarkussocial.domain.repository.FollowerRepository;
import org.acme.quarkussocial.domain.repository.PostRepository;
import org.acme.quarkussocial.domain.repository.UserRepository;
import org.acme.quarkussocial.rest.dto.CreatePostRequest;
import org.hamcrest.Matchers;
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
    @Inject
    FollowerRepository followerRepository;
    @Inject
    PostRepository postRepository;
    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    //criando um cenario com usuario
    @BeforeEach
    @Transactional
    public void setUP() {
        //user default
        var user = new User();
        user.setAge(30);
        user.setName("Ji Hye");
        userRepository.persist(user);
        userId = user.getId();

        //create the post for user
        Post post = new Post();
        post.setText("Hello");
        post.setUser(user);
        postRepository.persist(post);

        //user without follower
        var userNotFollower = new User();
        userNotFollower.setAge(25);
        userNotFollower.setName("Tester1");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();
        //user with follower
        var userFollower = new User();
        userFollower.setAge(31);
        userFollower.setName("Tester2");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);

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

    @Test
    @DisplayName("return 404 when user doesn't exist.")
    public void listPostUserNotFoundTest() {
        var nonexistentUserId = 999;
        given()
                .pathParam("userId", nonexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("return 400 when followerId header is not present.")
    public void listPostFollowerHeaderNotSendTest() {
        given()
                .pathParam("userId", userId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the header followerId"));
    }

    @Test
    @DisplayName("return 400 when followerId doesn't existe.")
    public void listPostFollowerNotFoundTest() {
        var nonexistentFollowerId = 999;
        given()
                .pathParam("userId", userId)
                .header("followerId", nonexistentFollowerId)
                .when()
                .get()
                .then()
                .statusCode(400)
                .body(Matchers.is("Inexistent followerId"));
    }

    @Test
    @DisplayName("return 403 when follower isn't a follower.")
    public void listPostNotFollowerTest() {
        given()
                .pathParam("userId", userId)
                .header("followerId", userNotFollowerId)
                .when()
                .get()
                .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts."));
    }

    @Test
    @DisplayName("return 200 list posts.")
    public void listPostsTest() {
        given()
                .pathParam("userId", userId)
                .header("followerId", userFollowerId)
                .when()
                .get()
                .then()
                .statusCode(200)
                .body("size()",Matchers.is(1));
    }
}