package com.zxl.sb.cache.quartzdemo.job;

import com.zxl.sb.cache.quartzdemo.entity.TestEntity1;
import com.zxl.sb.cache.quartzdemo.util.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Slf4j
public class MyJob extends QuartzJobBean {

    // private boolean httpRequest = false;                        // 布尔类型的变量，不要加is前缀

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String url = "http://localhost:9001/quartz/rest/getEntity";

        TestEntity1 entity = RestTemplateUtil.getRequest(url, TestEntity1.class);
        log.info("entity = {}", entity);
        System.out.println("MyJob...");
    }
}
