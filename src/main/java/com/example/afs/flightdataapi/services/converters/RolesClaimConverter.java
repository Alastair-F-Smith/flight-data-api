package com.example.afs.flightdataapi.services.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Arrays;
import java.util.Collection;

public class RolesClaimConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter;
    private final Logger logger = LoggerFactory.getLogger(RolesClaimConverter.class);

    public RolesClaimConverter(JwtGrantedAuthoritiesConverter wrappedConverter) {
        this.grantedAuthoritiesConverter = wrappedConverter;
    }

    /**
     * Convert the source object of type {@code S} to target type {@code T}.
     *
     * @param jwt the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     * @throws IllegalArgumentException if the source cannot be converted to the desired target type
     */
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> grantedAuthorities = grantedAuthoritiesConverter.convert(jwt);
        String roles = (String) jwt.getClaims().get("roles");
        if (roles != null) {
            Arrays.stream(roles.split(" "))
                  .map(SimpleGrantedAuthority::new)
                  .forEach(grantedAuthorities::add);
        }
        logger.info("Applying granted authorities for user {}: {}", jwt.getSubject(), grantedAuthorities);
        return new JwtAuthenticationToken(jwt, grantedAuthorities);
    }
}
