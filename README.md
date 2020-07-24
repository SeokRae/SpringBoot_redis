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



