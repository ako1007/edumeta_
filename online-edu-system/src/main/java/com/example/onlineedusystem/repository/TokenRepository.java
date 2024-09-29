package com.example.onlineedusystem.repository;

import com.example.onlineedusystem.entity.Token;
import com.example.onlineedusystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND t.expired = false OR t.revoked=false ")
    List<Token> findAllValidTokenByUser(@Param("userId") Long userId);

    @Query(value = "SELECT t from Token t where t.user.id = :userId")
    Token findTokenByUserId(@Param("userId") Long userId);

    Optional<Token> findByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM Token t WHERE t.user.id = :userId")
    void deleteTokensByUserId(@Param("userId") Long userId);
}
