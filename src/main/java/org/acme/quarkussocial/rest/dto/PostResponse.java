package org.acme.quarkussocial.rest.dto;

import jakarta.ws.rs.core.Response;
import lombok.Data;
import org.acme.quarkussocial.domain.model.Post;

import java.time.LocalDateTime;

@Data
public class PostResponse {
    private String text;
    private LocalDateTime dateTime;

    public static PostResponse fromEntity(Post post){
        var response = new PostResponse();
        response.setText(post.getText());
        response.setDateTime(post.getDateTime());

        return response;
    }
}
