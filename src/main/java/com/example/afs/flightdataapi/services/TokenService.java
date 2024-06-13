package com.example.afs.flightdataapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder encoder;

    @Autowired
    public TokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generateToken(Authentication auth) {
        Instant now = Instant.now();
        String roles = auth.getAuthorities()
                           .stream()
                           .map(grantedAuthority -> grantedAuthority.getAuthority())
                           .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                                          .issuer("self")
                                          .issuedAt(now)
                                          .expiresAt(now.plus(20, ChronoUnit.MINUTES))
                                          .subject(auth.getName())
                                          .claim("roles", roles)
                                          .build();
        return encoder.encode(JwtEncoderParameters.from(claims))
                      .getTokenValue();
    }
}
