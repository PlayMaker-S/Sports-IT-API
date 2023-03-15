package PlayMakers.SportsIT.auth.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider implements InitializingBean {

    private Key key;  // 암호화 후
    private final String secret; // 암호화 전
    private final long tokenValidityInMilliSeconds;
    private static final String AUTHORITIES_KEY = "auth";


    // secretKey 인코딩
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
        log.info("secret: {}", secret);
        System.out.println("secret = " + secret);
        this.secret = secret;
        this.tokenValidityInMilliSeconds = tokenValidityInSeconds * 1000;
    }

    // properties 에서 secretKey 를 가져와서 인코딩
    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = secret.getBytes();
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // JWT 토큰 생성
    public String createToken(Authentication authentication) {

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliSeconds); // 토큰 유효 기간

        return Jwts.builder()
                .setSubject(authentication.getName()) // 유저 정보를 담는다.
                .claim(AUTHORITIES_KEY, authorities) // 권한 정보를 담는다.
                .setIssuedAt(new Date()) // 토큰 발행 시간 정보
                .setExpiration(validity) // set Expire Time
                .signWith(key, SignatureAlgorithm.HS512)  // 사용할 암호화 알고리즘과 signature에 들어갈 key값 세팅
                .compact();
    }

    // JWT 토큰에서 인증 정보 조회 -> Authentication 객체 반환
    public Authentication getAuthentication(String jwtToken) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken).getBody();
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, jwtToken, authorities);
    }


    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            System.out.println("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
