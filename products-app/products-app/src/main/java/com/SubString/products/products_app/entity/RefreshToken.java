package com.SubString.products.products_app.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Entity
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
@Table(name = "refresh_token", indexes = {
        @Index(name = "refesh_tokens_jti_idx", columnList = "jti", unique = true),
        @Index(name = "refesh_tokens_jti_idx", columnList = "user_id")

})
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "jti", unique = true, nullable = false,updatable = false)
    private  String jti;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,updatable = false)
    private User user;


    @Column(updatable = false, nullable = false)
    private Instant createdAt;

    @Column( nullable = false)
    private Instant experiedAt;

    @Column(nullable = false)
    private boolean revoked;

    private String replacedByToken;




}
