package org.acme.quarkussocial.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "post_text")
    private String text;
    @Column(name = "dateTime")
    private LocalDateTime dateTime;
    //colocando relacionamento com o User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    //chamando automaticamente antes da persistencia ocorrer. a cada evento que eu chamar
    @PrePersist
    public void dateTime(){
        setDateTime(LocalDateTime.now());
    }

}
