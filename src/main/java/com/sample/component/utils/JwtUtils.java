package com.sample.component.utils;

import com.sample.domain.dtos.AccountBasicInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.sample.component.utils.Constant.getDate;

@Slf4j
public class JwtUtils {

    // 만료 시간 사용
    @Value("${jwt.expiredDate}")
    private Long expiredDate;

    private static final String ISSUER = "seok";
    private static final String SUBJECT = "/auth/login";
    private static final String AUDIENCE = "client";

    private Key key;

    public JwtUtils(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * JWT 토큰 생성 메서드
     * TODO public claim인 URL을 어떻게 넣는지 찾아봐야 함
     *
     * @param account 현 프로젝트의 사용자 Entity
     * @return JWT { Header.Payload.Signature } 반환
     */
    public String generateToken(AccountBasicInfo account, Integer plusMinutes) {
        return Jwts.builder()
                // JWT -> Header 생성 부분
                .setHeader(createHeader()) // 토큰의 타입 명시
                // JWT -> Signature 암호화 부분
                .signWith(key, SignatureAlgorithm.HS256) // 해싱 알고리즘으로 암호화 -> 서버에서 토큰 검증 시 signature에서 사용함
                // JWT -> Payload 생성 부분
                .setClaims(createClaims(account)) // private claim -> 사용자 정보
                // JWT -> Payload -> public Claims
                .setIssuer(ISSUER) // 토큰 발급
                .setSubject(SUBJECT) // 토큰 제목
                .setAudience(AUDIENCE) // 토큰 대상자
                .setIssuedAt(createDate(Constant.RedisConst.DEFAULT_EXPIRED)) // 토큰 발생 시간
                .setExpiration(createDate(plusMinutes)) // 만료시간

                .compact();
    }

    /**
     * @return 만료일자 반환
     */
    private Date createDate(int plusTime) {
        Date date = getDate(plusTime);
        log.info("Token 3: create {}: {}", (plusTime == Constant.RedisConst.DEFAULT_EXPIRED ? "IssueAt Time" : "ExpireAt Time"), date.getTime());
        return date;
    }

    /**
     * @return 헤더 설정
     */
    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();

        header.put("typ", Header.JWT_TYPE);// 토큰의 타입 명시
        header.put("alg", SignatureAlgorithm.HS256); // 해싱 알고리즘으로 암호화 -> 서버에서 토큰 검증 시 signature에서 사용함

        log.info("Token 1: createHeader : {}", header);
        return header;
    }

    /**
     * @return 로그인시 Member의 값을 JWT 토큰을 만들기 위한 값을 추출하여 비공개 Claims으로 파싱
     * 현재 {"id": "userName"} 로 만듬
     */
    private Map<String, Object> createClaims(AccountBasicInfo account) {
        // 비공개 클레임으로 사용자의 이름과 이메일을 설정, 세션 처럼 정보를 넣고 빼서 쓸 수 있다.
        Map<String, Object> claims = new HashMap<>();

        claims.put("id", account.getUserName());
        claims.put("role", account.getRole());

        log.info("Token 2: {}", claims);
        return claims;
    }

    /**
     * @param token 사용자의 Request 값의 Header에 존재하는 Authorization: Bearer {JWT}
     * @return JWT의 payload decode
     */
    private Claims getClaimsFormToken(String token) {
        log.info("[Token] parse: {}", Jwts.parser().setSigningKey(key).parseClaimsJws(token));
        log.info("[Token] Header: {}", Jwts.parser().setSigningKey(key).parseClaimsJws(token).getHeader());
        log.info("[Token] Body: {}", Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody());
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token).getBody();
    }

    /**
     * @param token JWT의 Payload 값
     * @return payload에서 현재 프로젝트에서 설정한 사용자 값을 구분하는 값인 userEmail을 반환
     */
    public String getUserNameFromToken(String token) {
        /* 복호화된 Payload로 id 확인 */
        Claims claims = getClaimsFormToken(token);
        return (String) claims.get("id");
    }

    /**
     * @param token 사용자 정보를 HS256로 암호화한 JWT
     * @return token 복호화하여 Payload 값 반환
     */
    public Claims getClaims(String token) {
        // Jws는 sign이 포함된 JWT를 말함
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * @param token 토큰의 유효성 검사
     */
    public boolean isValidToken(String token) {
        try {
            log.info("=================== Claims ===================");
            log.info("[Token] requestToken : {}", token);
            Claims claims = getClaimsFormToken(token);
            log.info("[Token Claims] {}", claims);
            log.info("[Token Registered Claims] expireTime : {}", Date.from(claims.getExpiration().toInstant())); // time은 정해진 포맷이 있어야 할 것 같은데 ?
            log.info("[Token Registered Claims] Audience :" + claims.getAudience());
            log.info("[Token Registered Claims] Issuer :" + claims.getIssuer());
            log.info("[Token Registered Claims] IssuedAt :" + claims.getIssuedAt().getTime());
            log.info("[Token Registered Claims] Subject :" + claims.getSubject());
            log.info("[Token Public Claims] Id :" + claims.get("id"));
            log.info("=================== Claims ===================");
            return true;
        } catch (PrematureJwtException exception) {
            log.error("[Token PrematureJwtException] 접근이 허용되기 전인 JWT가 수신된 경우");
        } catch (SignatureException exception) {
            log.error("[Token SignatureException] 오류");
        } catch (MalformedJwtException exception) {
            log.error("[Token MalformedJwtException] 구조적인 문제가 있는 JWT인 경우");
//        } catch (ClaimJwtException exception) {
//            log.error("[Token ClaimJwtException] JWT Claim 검사가 실패했을 때");
        } catch (UnsupportedJwtException exception) {
            log.error("[Token UnsupportedJwtException] 암호화된 JWT를 사용하는 애프리케이션에 암호화되지 않은 JWT가 전달되는 경우");
        /* access Token 예외 발생으로 인해 refreshToken 체크 시점 */
//        } catch (ExpiredJwtException exception) {
//            log.error("Token ExpiredJwtException");
//        } catch (JwtException exception) {
//            log.error("[Token JwtException] Token Tampered");
        } catch (NullPointerException exception) {
            log.error("[Token NullPointerException] Token is null");
            throw new RuntimeException("Token is null");
        }
        return false;
    }
}
