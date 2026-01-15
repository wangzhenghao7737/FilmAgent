package com.xiaosa.filmagent.service;

import com.xiaosa.filmagent.component.RegionsService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;


@SpringBootTest
class RegionsServiceTest {
    @Resource
    private RegionsService regionsService;
    @Test
    void getAdcode() {
        String[] inputs = {"haha","北京","河北省" ,"上海市", "广州", "深圳", "成都", "天津", "厦门", "青岛", "济南", "郑州市", "合肥"};
        Arrays.stream(inputs)
                .forEach(input -> {
                    System.out.println(input+regionsService.getAdcode( input));
                });
    }

}