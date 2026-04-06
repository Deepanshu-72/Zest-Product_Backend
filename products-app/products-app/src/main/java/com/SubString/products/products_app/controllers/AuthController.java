package com.SubString.products.products_app.controllers;


import com.SubString.products.products_app.dtos.LoginRequest;
import com.SubString.products.products_app.dtos.RefreshTokenRequest;
import com.SubString.products.products_app.dtos.TokenResponse;
import com.SubString.products.products_app.dtos.UserDto;
import com.SubString.products.products_app.entity.RefreshToken;
import com.SubString.products.products_app.entity.User;
import com.SubString.products.products_app.repositories.RefreshTokenRepository;
import com.SubString.products.products_app.repositories.UserRepository;
import com.SubString.products.products_app.security.CookieService;
import com.SubString.products.products_app.security.JwtService;
import com.SubString.products.products_app.services.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final RefreshTokenRepository refreshTokenRepository;

    private final AuthenticationManager authenticationManager;

    private  final UserRepository userRepository;

    private  final JwtService jwtService;

    private final ModelMapper mapper;

    private final CookieService cookieService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody LoginRequest loginRequest, HttpServletResponse response
    ){

        Authentication authentication = authentication(loginRequest);
        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(() -> new BadCredentialsException("Invalid Credentials"));

        if (!user.isEnable()){
            throw new DisabledException("User is Disabled");
        }
        // generate token
        String jti = UUID.randomUUID().toString();

        var refeshTokenOb = RefreshToken.builder()
                .jti(jti)
                .user(user)
                .createdAt(Instant.now())
                .experiedAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSecond()))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refeshTokenOb);

       String accessToken = jwtService.generateAccessToken(user);
       String refreshToken = jwtService.generateRefreshToken(user,refeshTokenOb.getJti());

       cookieService.attachRefreshCookie(response,refreshToken,(int)jwtService.getRefreshTtlSecond());
       cookieService.addNoStoreHeaders(response);




       TokenResponse tokenResponse = TokenResponse.of(accessToken,refreshToken, jwtService.getAcessTtlSecond(),mapper.map(user, UserDto.class) );

       return ResponseEntity.ok(tokenResponse);
    }

    private Authentication authentication(LoginRequest loginRequest){


        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));

        } catch (Exception e) {
            throw new BadCredentialsException("Invalid UserName or Password !!");
        }

    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
           @RequestBody(required = false) RefreshTokenRequest body,
           HttpServletResponse response,
           HttpServletRequest request


           ){

        String refreshToken = readRefreshTokenFromRequest(body, request).orElseThrow(() -> new BadCredentialsException("Invalid Refresh Token"));

        if(!jwtService.isRefreshToken(refreshToken)){
            throw  new BadCredentialsException("Inavalid Refresh Token Type");
        }

        String jti = jwtService.getJti(refreshToken);
        UUID userId = jwtService.getUserId(refreshToken);
        RefreshToken storedRefreshToken = refreshTokenRepository.findByJti(jti).orElseThrow(() -> new BadCredentialsException("Refresh token not recognized"));

        if(storedRefreshToken.isRevoked()){
            throw new BadCredentialsException("Refresh token expired or revoked");
        }

        if(storedRefreshToken.getExperiedAt().isBefore(Instant.now())){
            throw new BadCredentialsException("Refresh token expired");
        }
        
        if(!storedRefreshToken.getUser().getId().equals(userId)){
            throw new BadCredentialsException("Refresh token does not belong to this user");
        }


        // refresh token rotate or replace

        storedRefreshToken.setRevoked(true);
        String newJti = UUID.randomUUID().toString();
        storedRefreshToken.setReplacedByToken(newJti);
        refreshTokenRepository.save(storedRefreshToken);

        User user = storedRefreshToken.getUser();

        var newRefreshTokenOb  = RefreshToken.builder()
                .jti(newJti)
                .user(user)
                .createdAt(Instant.now())
                .experiedAt(Instant.now().plusSeconds(jwtService.getRefreshTtlSecond()))
                .revoked(false)
                .build();


        refreshTokenRepository.save(newRefreshTokenOb);
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user,newRefreshTokenOb.getJti());




        cookieService.attachRefreshCookie(response, newRefreshToken, (int) jwtService.getRefreshTtlSecond());
        cookieService.addNoStoreHeaders(response);
        return ResponseEntity.ok(TokenResponse.of(newAccessToken, newRefreshToken, jwtService.getAcessTtlSecond(), mapper.map(user, UserDto.class)));



    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        readRefreshTokenFromRequest(null, request).ifPresent(token -> {
            try {
                if (jwtService.isRefreshToken(token)) {
                    String jti = jwtService.getJti(token);
                    refreshTokenRepository.findByJti(jti).ifPresent(rt -> {
                        rt.setRevoked(true);
                        refreshTokenRepository.save(rt);
                    });
                }
            } catch (JwtException ignored) {
            }
        });

        // Use CookieUtil (same behavior)
        cookieService.clearRefreshCookie(response);
        cookieService.addNoStoreHeaders(response);
        SecurityContextHolder.clearContext();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    private Optional<String> readRefreshTokenFromRequest(RefreshTokenRequest body, HttpServletRequest request) {
        // 1. first orefer from cookies

        Optional<String> fromCookie;
        if (request.getCookies() != null) {
            fromCookie = Arrays.stream(request.getCookies())
                    .filter(c -> cookieService.getRefreshTokenCookieName().equals(c.getName()))
                    .map(Cookie::getValue)
                    .filter(v -> !v.isBlank())
                    .findFirst();

            if (fromCookie.isPresent() ){
                return fromCookie;
            }

        }

        if(body != null && body.refreshToken() != null && !body.refreshToken().isBlank()){
            return Optional.of(body.refreshToken());
        }


        //3. custom header
        String refreshHeader = request.getHeader("X-Refresh-Token");
        if (refreshHeader != null && !refreshHeader.isBlank()) {
            return Optional.of(refreshHeader.trim());
        }

        //Authorization = Bearer <token>
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.regionMatches(true, 0, "Bearer ", 0, 7)) {
            String candidate = authHeader.substring(7).trim();
            if (!candidate.isEmpty()) {
                try {
                    if (jwtService.isRefreshToken(candidate)) {
                        return Optional.of(candidate);
                    }
                } catch (Exception ignored) {
                }
            }
        }

        return Optional.empty();



    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUse(@RequestBody UserDto userDto){


        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(userDto));
    }
}
