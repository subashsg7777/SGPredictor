package com.SG_Predictor.SG_Predictor.stocks.repo;

import com.SG_Predictor.SG_Predictor.stocks.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,Long> {
    User findByEmail(@NotBlank String email);
}
