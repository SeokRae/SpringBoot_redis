package com.sample;

import com.sample.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.*;

/**
 * 기본 Reids 문법 테스트
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisApplication.class)
public class SpringBootRedisApplicationTests {

    @Resource(name = "stringRedisTemplate")
    private ListOperations<String, String> listOperations;

    @Resource(name = "redisTemplate")
    private HashOperations<String, Object, User> hashOperations;

    @Resource(name = "redisTemplate")
    private SetOperations<String, String> setOperations;

    @Resource(name="redisTemplate")
    private ZSetOperations<String, String> zSetOperations;

    @Resource(name = "redisTemplate")
    ValueOperations<String, String> valueOperations;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Before
    public void init() {

        //list put
        valueOperations.set("test:value", "valueOperations");

        listOperations.rightPush("test:user", "detail");
        listOperations.rightPush("test:user", "set");
        listOperations.rightPush("test:user", "zset");

        //hash put
        hashOperations.put("test:user:detail", "name", User.builder().name("seok").build());
        hashOperations.put("test:user:detail", "salary", User.builder().salary(10000).build());

        //set put
        setOperations.add("test:user:detail:set", "상세set1");
        setOperations.add("test:user:detail:set", "상세set2");
        setOperations.add("test:user:detail:set", "상세set3");
        //zset
        zSetOperations.add("test:user:detail:zset", "최우선사항", 1);
        zSetOperations.add("test:user:detail:zset", "차선책", 2);
        zSetOperations.add("test:user:detail:zset", "차차선책", 3);

        redisTemplate.expireAt("test:value", dueDate());

    }

    private Date dueDate() {
        return Date.from(LocalDateTime.now().plusSeconds(30).atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    public void redisTest1() {

        String user = listOperations.leftPop("test:user");
        StringBuffer stringBuffer = new StringBuffer();

        System.out.println(valueOperations.get("test:value"));

        while (user != null) {
            switch (user) {
                case "detail":
                    Map<Object, User> intro = hashOperations.entries("test:user:detail");
                    for(Object key : intro.keySet()) {
                        System.out.println("key : " + intro.get(key));
                    }
                    break;

                case "set":
                    Set<String> sets = setOperations.members("test:user:detail:set");
                    assert sets != null;

                    for (String set : sets) {
                        System.out.println("set : " + set);
                    }
                    break;

                case "zset":
                    Set<String> zsetes = zSetOperations.range("test:user:detail:zset", 0, 2);
                    int rank = 1;
                    assert zsetes != null;

                    for (String zset : zsetes){
                        System.out.println("zset : " + zset);
                    }
                    break;

                default:
                    System.out.println("end");
            }
            user = listOperations.leftPop("test:user");
        }
    }

    @Test
    public void commonCommand() {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        valueOps.set("key1", "key1value");
        valueOps.set("key2", "key2value");
        // Key 타입 조회.
        assertEquals(DataType.STRING, redisTemplate.type("key1"));

        // 존재하는 Key의 개수를 반환.
        assertSame(2L, redisTemplate.countExistingKeys(Arrays.asList("key1", "key2", "key3")));
        // Key가 존재하는지 확인
        Boolean hasKey = redisTemplate.hasKey("key1");
        System.out.println("hasKey('key1')" + hasKey);
        assertTrue(hasKey);

        // Key 만료 날짜 세팅
        Boolean expireAt = redisTemplate.expireAt("key1", dueDate());
        assertTrue(expireAt);
        // Key 만료 시간 세팅
        Boolean expire = redisTemplate.expire("key1", 60, TimeUnit.SECONDS);
        assertTrue(expire);

        // Key 만료 시간 조회
        assertThat(redisTemplate.getExpire("key1"), greaterThan(0L));

        // Key 만료 시간 해제
        Boolean persist = redisTemplate.persist("key1");
        System.out.println("persist : " + persist);
        assertTrue(persist);

        // Key 만료시간이 세팅 안되어있는경우 -1 반환
        assertSame(-1L, redisTemplate.getExpire("key1"));
        // Key 삭제
        assertTrue(redisTemplate.delete("key1"));
        // Key 일괄 삭제
        assertThat(redisTemplate.delete(Arrays.asList("key1", "key2", "key3")), greaterThan(0L));
    }

    @Test
    public void testHash() {
        String hashKey = "user";

        hashOperations = redisTemplate.opsForHash();
        hashOperations.put(hashKey, "hashKey", User.builder().id("id").name("seok").build());
        hashOperations.put(hashKey, "hashKey2", User.builder().id("id").name("seok2").build());

        /* hashOperations의 entry를 조회*/
        Map<Object, User> hashMap = hashOperations.entries("user");

        /* redis 의 데이터의 유효기간을 설정 */
        redisTemplate.expireAt(hashKey, dueDate());
        System.out.println("key : " + hashKey + " -> expiredAt : " + redisTemplate.getExpire(hashKey));

        /* hgetall {key}로 검색 */
        for(Object key : hashMap.keySet()) {
            String k = (String) key;
            User user = (User) hashMap.get(k);
            System.out.println("key : " + key + "\t\t value : " + user.getName());
        }

        /* 해당 키 값의 expire 값을 확인 */
        System.out.println("expireAt : " + hashOperations.getOperations().getExpire("key") + " 초");
    }

    @Test
    public void testSet() {
        String setKey = "setKey";
        setOperations = redisTemplate.opsForSet();
        setOperations.add(setKey, "setValue01");
        setOperations.add(setKey, "setValue02");
        setOperations.add(setKey, "setValue03");
        /* redisTemplate 통해 특정 key 값의 유효기간을 설정 */
        redisTemplate.expireAt(setKey, dueDate());

        /* smembers * */
        Set<String> sets = setOperations.members(setKey);

        /* 입력한 key 값 내에 리스트 형태로 확인 */
        for(String key : sets) {
            System.out.println(key);
        }
    }

    /* 가중치 값을 포함하는 데이터 형태 */
    @Test
    public void testZSet() {
        zSetOperations = redisTemplate.opsForZSet();

        zSetOperations.add("zSet", "tuple", 1);
        zSetOperations.add("zSet", "tuple1", 2);
        zSetOperations.add("zSet", "tuple2", 3);
        redisTemplate.expireAt("zSet", dueDate());

        /* zSet에 담겨 있는 값의 크기 확인 */
        Long size = zSetOperations.size("zSet");
        System.out.println("zSet Size : " + size);

        /* range 라는 메서드로 slice 해서 Set<String>에 보관 가능 */
        Set<String> sets = zSetOperations.range("zSet", 0, size);
        for( String set : sets) {
            System.out.println(set);
        }
    }
}