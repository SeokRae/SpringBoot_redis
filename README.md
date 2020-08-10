# Redis & SpringBoot
- 레디스 기본 (Redis Basic)
- 레디스 서버 구축 (Docker)
- 레디스 사용하기 (TestCase 작성으로 명령어 익히기)
- 레디스 활용하기 (JWT 사용)

## Redis Basic
- 레디스 

## Build Redis Server
- docker로 redis image 받아서 구축

## Redis command & SpringDataRedis

- String 타입 대신 hash 타입을 사용하여 메모리 사용량과 수행시간을 줄인다. [(참고)](https://www.joinc.co.kr/w/man/12/REDIS/RedisWithJoinc/part05)
    - hash 타입의 내부 데이터에는 expireAt 설정을 할 수 없다.
    - hash의 key, hashKey, Object의 값을 어떻게 넣을 것인지 결정해야 한다.
    ex) user:{id}

- key에 expireAt 설정 가능, hashKey에 expireAt 설정 불가능

```shell script
# 전체 키 확인
keys *

# hash 타입 저장 데이터 확인 
hgetall {key}

# set 타입 데이터 확인
smember {key}

# SortedSet(zSet) 타입 데이터 확인 range 로 순서대로 확인 가능
zrange {key} 0 3
```

## Redis & JWT Architecture
- docs
    [Presentation](https://docs.google.com/presentation/d/1RpUnnnr9dNuPOJTS11RpR02qErl2GBBfiUr7JeBtsLs/edit?usp=sharing)
    [Blog](https://seokr.tistory.com/833)
- Authentication
- Authorization

## Redis 활용
- Redis의 로그는 AOF 로 파일로 저장해야 함
    - 일일 통계 Batch를 통해 레디스의 로그 파일을 DB로 집계 처리 해야 함

- Authentication / Authorization 시나리오

    1. Authentication
        1.1 클라이언트의 로그인 요청 (ID / PASSWORD)
        1.2 Interceptor에서 id와 Password의 유효성 검사
        1.3 사용자가 존재하고 권한이 확인되면 JWT 발급 (AccessToken, RefreshToken)
            1.3.1 type, algorithm으로 header를 생성
            1.3.2 DB조회 하여 사용자의 username, role으로 Claim 설정
            1.3.3 Registered Claim의 종류인 Issuer(발급자), Subject(토큰제목), Audience(대상자), IssueAt(발급일자), Expiration(만료시간) 설정
            1.3.4 위 Claim들로 Payload 생성
            1.3.5 Header와 Payload를 서버에서 임의로 설정한 값으로 해싱알고리즘을 통해 Signature 생성 
        1.4 서버에서 AccessToken은 Redis에 저장, RefreshToken은 DB에 저장하도록 한다.

        * Redis에 Token의 유효기간을 설정할 것인지 중요 (최소 AccessToken의 유효시간이상)

    2. Authorization (인가)
        2.1 Client는 AccessToken과 RefreshToken을 모두 갖고 있는 상태
        2.2 Client는 Resource에 접근 시, AccessToken과 함께 요청한다.
        2.3 Interceptor에서 특정 Resource 접근에 대한 요청을 캐치하여 인가 프로세스를 실시한다.
        2.4 AccessToken의 유효성 검사 (Signature, Malformed, UnsupportedJwt, NullPoint, Expired)
            2.4.1 AccessToken에서 Expired 예외가 발생하는 경우, RefreshToken의 유효성을 검사
                - RefreshToken이 유효하지 않은 경우 로그인 필요 에러로그 반환
            2.4.2 RefreshToken이 정상인 경우 RefreshToken의 Payload 값을 통해 AccessToken을 재발급
        2.5 AccessToken이 정상인경우 Redis의 AccessToken을 확인, 정상인 경우 resource에 접근 할 수 있도록 허용

    - 위 프로세스를 통해 사용자의 사이트 방문 통계를 확인할 수 있다.
        1. RefreshToken의 이력을 통해 일일 특정 시간의 방문자 통계를 알 수 있다.
        2. RefreshToken당 AccessToken 발급 횟수를 통해 사용자의 사이트 체류시간을 알 수 있다.
        3. AccessToken의 이력을 통해 접속량 확인 가능 

## JWT 사용 시 궁금한 점
1. 클라이언트에 AccessToken, RefreshToken 을 모두 넘겨주는 경우 토큰 탈취에 대한 문제는 없는 것인가?

## Redis 사용 시 궁금한 점
1. Redis에서 AccessToken 을 보관하는 유효기간은 얼마나 ? 