package org.acme.quarkussocial.rest;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.acme.quarkussocial.domain.model.Follower;
import org.acme.quarkussocial.domain.model.User;
import org.acme.quarkussocial.domain.repository.FollowerRepository;
import org.acme.quarkussocial.domain.repository.UserRepository;
import org.acme.quarkussocial.rest.dto.FollowerRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    FollowerRepository followerRepository;
    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    void setUp() {
        //user default
        var user = new User();
        user.setAge(30);
        user.setName("Ji Hye");
        userRepository.persist(user);
        userId = user.getId();
        //user follower
        var follower = new User();
        follower.setAge(23);
        follower.setName("Alex");
        userRepository.persist(follower);
        followerId = follower.getId();

        //create a follower
        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);
    }

    //teste do PUT
    @Test
    @DisplayName("return 409 when followerId is equal to User id")
    public void sameUserAsFollowerTest() {
        var body = new FollowerRequest();
        body.setFollowerId(userId);
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can't follow yourself."));
    }

    @Test
    @DisplayName("return 404 on follow a user when User id doesn't exist")
    public void userNotFoundWhenTryToFollowTest() {
        var body = new FollowerRequest();
        body.setFollowerId(userId);
        var nonexistentUserId = 999;
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", nonexistentUserId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("return follow a user")
    public void followUserTest() {
        var body = new FollowerRequest();
        body.setFollowerId(followerId);
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId", userId)
                .when()
                .put()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
    //Teste de GET
    @Test
    @DisplayName("return 404 on list user followers and User id doesn't exist")
    public void userNotFoundWhenListingFollowersTest(){
        var nonexistentUserId = 999;
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", nonexistentUserId)
                .when()
                .get()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("return list a users followers")
    public void listFollowersTest(){
        var response =
        given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId)
                .when()
                .get()
                .then()
                .extract().response();
        var followersCount = response.jsonPath().get("followersCount");
        var followersContent = response.jsonPath().getList("content");

        assertEquals(Response.Status.OK.getStatusCode(), response.statusCode());
        assertEquals(1, followersCount);
        assertEquals(1, followersContent.size());
    }

    //teste de DELETE
    @Test
    @DisplayName("return 404 on list user followers and User id doesn't exist")
    public void userNotFoundWhenUnfollowingAUserTest(){
        var nonexistentUserId = 999;
        given()
                .pathParam("userId", nonexistentUserId)
                .queryParam("followerId", followerId)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }
    @Test
    @DisplayName("return Unfollow an user")
    public void unfollowUserTest(){
        var nonexistentUserId = 999;
        given()
                .pathParam("userId", userId)
                .queryParam("followerId", followerId)
                .when()
                .delete()
                .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

}