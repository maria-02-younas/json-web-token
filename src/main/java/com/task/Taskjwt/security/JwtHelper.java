package com.task.Taskjwt.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtHelper {
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60; // 5 hours
//    public static final long JWT_TOKEN_VALIDITY = 5 * 5; // 25 seconds

    private final String secret = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token,
                                   Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //    For retrieving any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
//        Approach 1 = getSigningKey error
//        return Jwts.parserBuilder().setSigningKeyResolver(new SigningKeyResolverAdapter() {
//            @Override
//            public byte[] resolveSigningKeyBytes(JwsHeader header, Claims claims) {
//                return getSigningKey(header, claims); //implement me
//            }}).build().parseClaimsJws(token).getBody();

//        Approach 2 = setSigningKey deprecated
//        return Jwts.parserBuilder().setSigningKey(secret)
//                .build().parseClaimsJws(token).getBody();

//        Approach 3
//      parser() & setSigningKey(secret) is deprecated
//        return Jwts.parser().setSigningKey(secret)
//                .parseClaimsJws(token).getBody();

//        Approach 4
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        Jws<Claims> temp = Jwts.parserBuilder().setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token);
        System.out.println(temp);
        return temp.getBody();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername()
                ,userDetails.getAuthorities()
        );
    }

    private String doGenerateToken(Map<String, Object> claims,
                                   String subject
            , Collection<? extends GrantedAuthority> authorities
    ) {
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        return Jwts.builder().setClaims(claims).setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .claim("roles",new String[]{"ROLE_EMPLOYEE", "ROLE_MANAGER", "ROLE_ADMIN"})
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
//                .signWith(SignatureAlgorithm.HS512, secret).compact(); // signWith deprecated
                .signWith(secretKey, SignatureAlgorithm.HS512).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token));
    }
}
