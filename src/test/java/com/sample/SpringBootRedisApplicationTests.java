package com.sample;

import org.junit.Before;
import org.junit.Ignore;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisApplication.class)
public class SpringBootRedisApplicationTests {

    @Resource(name = "stringRedisTemplate")
    private ListOperations<String, String> listOperations;

    @Resource(name = "stringRedisTemplate")
    private HashOperations<String, String, String> hashOperations;

    @Resource(name = "stringRedisTemplate")
    private SetOperations<String, String> setOperations;

    @Resource(name="stringRedisTemplate")
    private ZSetOperations<String, String> zSetOperations;

    @Resource(name = "stringRedisTemplate")
    ValueOperations<String, String> valueOperations;

    @Before
    @Ignore
    public void init() {

        //list put
        valueOperations.set("test:value", "valueOperations");

        listOperations.rightPush("test:user", "detail");
        listOperations.rightPush("test:user", "set");
        listOperations.rightPush("test:user", "zset");

        //hash put
        hashOperations.put("test:user:detail", "name", "seok");
        hashOperations.put("test:user:detail", "age", "34");

        //set put
        setOperations.add("test:user:detail:set", "상세set1");
        setOperations.add("test:user:detail:set", "상세set2");
        setOperations.add("test:user:detail:set", "상세set3");
        //zset
        zSetOperations.add("test:user:detail:zset", "최우선사항", 1);
        zSetOperations.add("test:user:detail:zset", "차선책", 2);
        zSetOperations.add("test:user:detail:zset", "차차선책", 3);
    }

    @Test
    @Ignore
    public void redisTest1() {
        String user = listOperations.leftPop("test:user");
        StringBuffer stringBuffer = new StringBuffer();

        System.out.println(valueOperations.get("test:value"));

        while (user != null) {
            switch (user) {
                case "detail":
                    Map<String, String> intro = hashOperations.entries("test:user:detail");
                    for(String key : intro.keySet()) {
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

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

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
        boolean hasKey = redisTemplate.hasKey("key1");
        System.out.println("hasKey('key1')" + hasKey);
        assertTrue(hasKey);
        // Key 만료 날짜 세팅
        assertTrue(redisTemplate.expireAt("key1", Date.from(LocalDateTime.now().plusDays(1L).atZone(ZoneId.systemDefault()).toInstant())));
        // Key 만료 시간 세팅
        assertTrue(redisTemplate.expire("key1", 60, TimeUnit.SECONDS));
        // Key 만료 시간 조회
        assertThat(redisTemplate.getExpire("key1"), greaterThan(0L));
        // Key 만료 시간 해제
        boolean persist = redisTemplate.persist("key1");
        System.out.println("persist : " + persist);
        assertTrue(persist);
        // Key 만료시간이 세팅 안되어있는경우 -1 반환
        assertSame(-1L, redisTemplate.getExpire("key1"));
        // Key 삭제
        assertTrue(redisTemplate.delete("key1"));
        // Key 일괄 삭제
        assertThat(redisTemplate.delete(Arrays.asList("key1", "key2", "key3")), greaterThan(0L));
    }
}
