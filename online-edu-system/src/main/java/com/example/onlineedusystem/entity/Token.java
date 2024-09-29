package com.example.onlineedusystem.entity;

import com.example.onlineedusystem.entity.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType tokenType = TokenType.BEARER;

    private boolean revoked;

    private boolean expired;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;



}
