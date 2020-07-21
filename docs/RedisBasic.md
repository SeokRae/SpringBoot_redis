## Redis?

- REmote Dictionary Server의 약어인 Redis
- 데이터베이스, 캐시, 메시지 브로커 및 대기열로 사용하는 빠르고 오픈 소스,
- 인 메모리 키-값 데이터 스토어

## Redis 용도

## Redis DataType

1. Collections
2. Strings - key / value
3. List
4. Set
    1. Sorted Sets
5. Hash

## Redis 주의사항

- 메모리 관리
- SingleThread로 인한  문제
- Redis Replication

## 권장 설정

1. redis.conf
    1. maxclient → 5000
    2. RDB, AOF 설정 OFF
    3. 특정 commands  설정 OFF
        - Keys
        - AWS Elastic Cache에서는 이미 disable
        - 전체 장애 90% 이상이 keys, save로 발생
    4. 적절한 ziplist 설정

## Redis 데이터 분산

## Redis Cluster

## Redis FailOver

## Redis Monitoring

참고

[Redis](https://www.slideshare.net/charsyam2/redis-196314086)