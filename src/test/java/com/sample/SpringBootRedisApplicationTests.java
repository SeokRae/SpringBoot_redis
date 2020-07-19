package com.sample;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;

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
}
