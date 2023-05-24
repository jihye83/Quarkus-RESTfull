package org.acme.quarkussocial.rest;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.quarkussocial.domain.model.Post;
import org.acme.quarkussocial.domain.model.User;
import org.acme.quarkussocial.domain.repository.PostRepository;
import org.acme.quarkussocial.domain.repository.UserRepository;
import org.acme.quarkussocial.rest.dto.CreatePostRequest;
import org.acme.quarkussocial.rest.dto.PostResponse;

import java.util.stream.Collectors;

@Path("/users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    private UserRepository userRepository;
    private PostRepository postRepository;

    @Inject
    public PostResource(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest request) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Post post = new Post();
        post.setText(request.getText());
        post.setUser(user);

        postRepository.persist(post);

        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    public Response listPosts(@PathParam("userId") Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
//colocando Sort por dateTime
        var query = postRepository.find("user", Sort.by("dateTime", Sort.Direction.Descending) , user);
        var list = query.list();
// listando somento o texto e a data no insomnia
        var postResponseList =  list.stream()
//                .map(post -> PostResponse.fromEntity(post)) pode ser usada assim ou (ele funciona pq é estatico
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());

        return Response.ok(postResponseList).build();
    }

}
