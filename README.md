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

- Redis에 key 
- redis-cli

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

## Redis Architecture
- [Authentication](https://docs.google.com/presentation/d/1RpUnnnr9dNuPOJTS11RpR02qErl2GBBfiUr7JeBtsLs/edit?usp=sharing)
- [Authorization](https://docs.google.com/presentation/d/1RpUnnnr9dNuPOJTS11RpR02qErl2GBBfiUr7JeBtsLs/edit?usp=sharing)


## Redis 활용
- Redis의 로그는 AOF 로 파일로 저장해야 함
    - 일일 통계 Batch를 통해 레디스의 로그 파일을 DB로 집계 처리 해야 함
 
- 사용자 접근 통계를 위한 시나리오
    - Client의 AccessToken의 유효기간을 1분으로 설정
    - Redis내에 AccessToken을 저장하는 시간을 3분으로 설정 (Redis에는 한 사용자에 대한 AccessToken이 3개까지 존재할 수 있다.)
    - Client는 AccessToken을 가지고 Resource 접근 요청을 수행
    - 서버는 AccessToken의 유효성을 검사하고 expired된 token임을 확인하면 Redis에 저장되어있는 hash로 저장되어 있는 accessToken을 조회
    - 조회된 accessToken의 정보는 key(user:signature), hashKey(accessToken), 사용자정보(account)를 갖고 있어, 사용자 정보를 통해 refreshToken을 조회
    - 조회된 refreshToken의 유효성 검사를 실시, 유효기간 아직 남아있으면 accessToken을 재발급하여 클라이언트에 전달
    - refreshToken의 유효기간이 끝난경우, 클라이언트에게 로그인을 할 수 있도록 알림을 반환

    - 위 프로세스 동안 생성된 accessToken과 refreshToken을 통해 사용자의 활동 통계 값을 집계할 수 있다.
        1. refreshToken의 이력을 통해 하루 로그인 통계 값 확인 가능 (사실 accessToken을 재발급 할 때, refreshToken의 유효기간이 더 길어져야 하는게 아닌지 ?)
        2. accessToken의 이력을 통해 하나의 refreshToken당 몇개의 accessToken을 재발급 했는지 통계
        3. accessToken의 재발급된 이력 + @ 로 접근 통계 (요청 URL에 대한 이력을 쌓는 경우, 해당 자원에 대한 접근 빈도 확인 가능)
         