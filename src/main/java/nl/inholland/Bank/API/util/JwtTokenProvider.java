package nl.inholland.Bank.API.util;

import io.jsonwebtoken.*;
import nl.inholland.Bank.API.model.Role;
import nl.inholland.Bank.API.service.MyUserDetailsService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    // Link to the documentation of the used JWT library:
    // https://github.com/jwtk/jjwt

    @Value("${application.token.validity}")
    private long validityInMicroseconds;
    private final MyUserDetailsService userDetailsService;
    private final JwtKeyProvider jwtKeyProvider;

    public JwtTokenProvider(MyUserDetailsService userDetailsService, JwtKeyProvider jwtKeyProvider) {
        this.userDetailsService = userDetailsService;
        this.jwtKeyProvider = jwtKeyProvider;
    }

    public String createToken(String username, Role role) throws JwtException {
      Claims claims = Jwts.claims().setSubject(username);
      claims.put("auth", List.of(role.name()));
  
      Date now = new Date();
      Date expiration = new Date(now.getTime() + validityInMicroseconds);
  
      return Jwts.builder()
              .setClaims(claims)
              .setIssuedAt(now)
              .setExpiration(expiration)
              .signWith(jwtKeyProvider.getPrivateKey(), SignatureAlgorithm.RS256)
              .compact();
    }   

    public Authentication getAuthentication(String token) {

        // We will get the username from the token
        // And then get the UserDetails for this user from our service
        // We can then pass the UserDetails back to the caller
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(jwtKeyProvider.getPrivateKey()).build().parseClaimsJws(token);
            String username = claims.getBody().getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Bearer token not valid");
        }
    }
}