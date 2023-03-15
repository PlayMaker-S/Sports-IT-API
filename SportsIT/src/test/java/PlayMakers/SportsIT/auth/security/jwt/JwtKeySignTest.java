package PlayMakers.SportsIT.auth.security.jwt;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.security.Key;
import java.util.Base64;

public class JwtKeySignTest {

    Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // setSubject(): Subject가 "Test"인 Claim 등록 -> JWT 생성
    // signWith(): 해싱 알고리즘은 HS256, Secret Key는 key
    // compact(): final String 형태의 JWT(=signed JWT) 생성
    String jws = Jwts.builder().setSubject("Test").signWith(key).compact();

    Base64.Encoder base64UrlEncode = Base64.getUrlEncoder().withoutPadding();

    @Test
    public void JWT검증() {
        // Jwts.parserBuilder(): JwsParserBuilder 생성
        // setSigningKey(): Secret Key 설정
        // build(): JwsParser 생성
        // parseClaimsJws(): Jws<Claims> 생성
        // getBody(): Claims 반환
        // getSubject(): Subject 반환
        assert Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jws).getBody().getSubject().equals("Test");
    }

    @Test
    public void JWT검증_실패() {
        Key key2 = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        Assertions.assertThrows(SignatureException.class, () -> {
            Jwts.parserBuilder().setSigningKey(key2).build().parseClaimsJws(jws);
        });
    }

}
