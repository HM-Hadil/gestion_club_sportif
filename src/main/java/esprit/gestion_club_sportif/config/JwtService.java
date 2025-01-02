package esprit.gestion_club_sportif.config;

import esprit.gestion_club_sportif.repo.IJwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService implements IJwtService {


    @Value("${jwt.secret.key}")
    private String secretKey;
    @Value("${jwt.expiration.duration}")
    private long jwtExpirationDuration;



    @Override
    public String extractUserEmail(String token){
        return extractClaim(token,Claims::getSubject);
    }

    @Override
    public <T>  T extractClaim(String token , Function<Claims,T> claimsTFunction){
        final Claims claims = extractAllClaims(token);
        return  claimsTFunction.apply(claims);

    }
    @Override
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);

    }

    @Override
    public String generateToken(Map<String,Object> extractClamis, UserDetails userDetails ){

        return Jwts
                .builder()
                .setClaims(extractClamis)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationDuration))
                .signWith(getsigninKey(), SignatureAlgorithm.HS256)
                .compact();

    }



    @Override
    public boolean isTokenValid(String token,UserDetails userDetails){
        final String username=extractUserEmail(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);

    }


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){
     return    Jwts
                .parserBuilder()
                .setSigningKey(getsigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getsigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }



}
