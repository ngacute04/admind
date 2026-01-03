package com.example.usermanagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_media")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PostMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    
    private String url;
}