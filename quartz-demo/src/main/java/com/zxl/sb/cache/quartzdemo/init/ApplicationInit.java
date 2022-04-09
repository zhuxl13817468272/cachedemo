package com.zxl.sb.cache.quartzdemo.init;

import com.zxl.sb.cache.quartzdemo.entity.QuartzJob;
import com.zxl.sb.cache.quartzdemo.enums.JobStatus;
import com.zxl.sb.cache.quartzdemo.mapper.JobMapper;
import com.zxl.sb.cache.quartzdemo.service.IJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ApplicationInit implements CommandLineRunner {

    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private IJobService jobService;
    @Autowired
    private Scheduler scheduler;

    @Override
    public void run(String... args) throws Exception {
        loadJobToQuartz();
    }

    //加载所有的jobDetail交由scheduler管理
    private void loadJobToQuartz() throws Exception {
        log.info("quartz job load...");
        List<QuartzJob> jobs = jobMapper.listJob("");
        for(QuartzJob job : jobs) {
            jobService.schedulerJob(job); // 每个定时任务（每条数据）交由scheduler管理
            if (JobStatus.PAUSED.getStatus().equals(job.getTriggerState())) { //quartz 启动时，暂停任务状态判断修正
                scheduler.pauseJob(new JobKey(job.getJobName(), job.getJobGroup()));
            }
        }
    }
}
