package com.example.glitter.domain.Auth;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
  @Autowired
    private CognitoService cognitoService;

    @Override
    public Optional<JwtTokenDto> login(UserIdentity identity) {
        return cognitoService.login(identity);
    }
}
