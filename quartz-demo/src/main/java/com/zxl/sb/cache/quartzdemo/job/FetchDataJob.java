package com.zxl.sb.cache.quartzdemo.job;

import com.zxl.sb.cache.quartzdemo.entity.User;
import com.zxl.sb.cache.quartzdemo.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Random;
import java.util.stream.IntStream;

@Slf4j
public class FetchDataJob extends QuartzJobBean {

    @Autowired
    private UserMapper userMapper;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        // TODO 业务处理

        Random random = new Random();
        IntStream intStream = random.ints(18, 100);
        int first = intStream.limit(1).findFirst().getAsInt();
        int count = userMapper.saveUser(new User("zhangsan" + first, first));
        if (count == 0) {
            log.error("用户保存失败！");
            return;
        }
        log.info("用户保存成功");
    }
}