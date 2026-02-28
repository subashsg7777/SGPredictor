package com.SG_Predictor.SG_Predictor.stocks.service;

import com.SG_Predictor.SG_Predictor.security.JwtUtil;
import com.SG_Predictor.SG_Predictor.stocks.dto.LoginReqDto;
import com.SG_Predictor.SG_Predictor.stocks.dto.LoginResDto;
import com.SG_Predictor.SG_Predictor.stocks.entity.User;
import com.SG_Predictor.SG_Predictor.stocks.repo.UserRepo;
import com.SG_Predictor.SG_Predictor.stocks.service.impl.UserImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserImpl {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;

    @Override
    public LoginResDto loginUser(LoginReqDto loginReqDto) {

        LoginResDto loginResDto = new LoginResDto();
        if (userRepo == null) {
            log.warn("User repository is not available");
            loginResDto.setMessage("User repository is not available");
            loginResDto.setDecision(false);
            return loginResDto;
        }

        User user;
        try {
            user = userRepo.findByEmail(loginReqDto.getEmail());
        } catch (DataAccessException exception) {
            log.error("Database connection failed during login: {}", exception.getMostSpecificCause() != null
                    ? exception.getMostSpecificCause().getMessage()
                    : exception.getMessage());
            loginResDto.setMessage("Error Connecting to DB");
            loginResDto.setDecision(false);
            return loginResDto;
        }

        if (user == null) {
            loginResDto.setMessage("No User Found");
            loginResDto.setDecision(false);
            return loginResDto;
        }
        if ( user.getPassword() != null && user.getPassword().equals(loginReqDto.getPassword())){
            String token = jwtUtil.generateToken(loginReqDto.getEmail());
            loginResDto.setMessage("Login Sucessfull");
            loginResDto.setDecision(true);
            loginResDto.setToken(token);
            return loginResDto;
        }

        loginResDto.setMessage("Password Don't Matches");
        loginResDto.setDecision(false);
        return loginResDto;
    }
}
