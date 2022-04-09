package com.zxl.sb.cache.quartzdemo.web;

import com.zxl.sb.cache.quartzdemo.entity.TestEntity;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest")
@Slf4j
public class RestTestController {

    @RequestMapping("/getEntity")
    @ResponseBody
    public TestEntity getEntity() {
        log.info("收到请求...");
        return new TestEntity("zhangsan", 22);
    }

}
