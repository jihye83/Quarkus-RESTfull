package org.acme.quarkussocial.domain.model;

import jakarta.persistence.*;
import lombok.Data;

/*
  author: Ji Hye Koo
  course: Quarkus para criar API's RESTfull
 */
@Entity
@Table(name = "followers")
@Data
public class Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower;

}
