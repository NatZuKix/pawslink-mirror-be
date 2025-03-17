package sit.pawslink.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import sit.pawslink.entities.CustomUserDetails;
import sit.pawslink.properties.JwtProperties;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil implements Serializable {

    @Autowired
    private JwtProperties jwtProperties;

    //retrieve username from jwt token
//    public String getUsernameFromToken(String token) {
//        return getClaimFromToken(token, Claims::getSubject);
//    }

    public String getUsernameFromToken(String token) {
        try {
            String username = getClaimFromToken(token, Claims::getSubject);
            return username;
        } catch (Exception e) {
            return null;
        }
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtProperties.getSecretKey()).parseClaimsJws(token).getBody();
    }

    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //generate token for user
    public Map<String, String> generateTokens(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        Date now = new Date(System.currentTimeMillis());
        Date accessTokenExpiration = new Date(now.getTime() + jwtProperties.getAccessTokenValidityInMinutes() * 60 * 1000);
        Date refreshTokenExpiration = new Date(now.getTime() + jwtProperties.getRefreshTokenValidityInMinutes() * 60 * 1000);

        // Convert authorities to a list of role names
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Put the userId into the claims map
        if (userDetails instanceof CustomUserDetails) {
            String userId = ((CustomUserDetails) userDetails).getUserId();
            String email = ((CustomUserDetails) userDetails).getEmail();
            claims.put("userId", userId);
            claims.put("email", email);
        }

        // Put the roles into the claims map
        claims.put("roles", roles);

        claims.put("access_token", doGenerateToken(claims, userDetails.getUsername(), accessTokenExpiration));
        claims.put("refresh_token", doGenerateToken(claims, userDetails.getUsername(), refreshTokenExpiration));

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("access_token", (String) claims.get("access_token"));
        tokenMap.put("refresh_token", (String) claims.get("refresh_token"));

        return tokenMap;
    }


    //while creating the token -
    //1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    //2. Sign the JWT using the HS512 algorithm and secret key.
    //3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject, Date expirationDate) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecretKey())
                .compact();
    }

    //validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
