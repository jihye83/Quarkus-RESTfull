package org.acme.quarkussocial.rest.dto;

import lombok.Data;
import org.acme.quarkussocial.domain.model.Follower;

import java.util.List;

@Data
public class FollowersPerUserResponse {
    private Integer followersCount;
    private List<FollowerResponse> content;

}
