package com.xiaosa.filmagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FilmManusTest {
    @Resource
    private FilmManus filmManus;
    @Test
    void run() {
        String result = filmManus.run("""
                我在河南省郑州市金水区，我计划去附近的影城看一场电影，给我一份观影计划。
                该计划必须最终以PDF或txt文本保存
                """);
        System.out.println(result);
    }

}