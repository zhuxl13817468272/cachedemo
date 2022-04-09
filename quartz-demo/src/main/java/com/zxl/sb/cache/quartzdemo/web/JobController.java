package com.zxl.sb.cache.quartzdemo.web;

import com.github.pagehelper.PageInfo;
import com.zxl.sb.cache.quartzdemo.common.Result;
import com.zxl.sb.cache.quartzdemo.entity.QuartzJob;
import com.zxl.sb.cache.quartzdemo.service.IJobService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job")
@Slf4j
public class JobController {

    @Autowired
    private IJobService jobService;
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/add")
	public Result save(QuartzJob quartz){
		log.info("新增任务");
		Result result = jobService.saveJob(quartz);
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping("/edit")
	public Result edit(QuartzJob quartz){
		log.info("编辑任务");
		Result result = jobService.updateJob(quartz);
		return result;
	}
	@PostMapping("/list")
	public PageInfo list(String jobName,Integer pageNo,Integer pageSize){
		log.info("任务列表");
		PageInfo pageInfo = jobService.listQuartzJob(jobName, pageNo, pageSize);
		return pageInfo;
	}

	@PostMapping("/trigger")
	public  Result trigger(String jobName, String jobGroup) {
		log.info("触发任务");
		Result result = jobService.triggerJob(jobName, jobGroup);
		return result;
	}

	@PostMapping("/pause")
	public  Result pause(String jobName, String jobGroup) {
		log.info("停止任务");
		Result result = jobService.pauseJob(jobName, jobGroup);
		return result;
	}

	@PostMapping("/resume")
	public  Result resume(String jobName, String jobGroup) {
		log.info("恢复任务");
		Result result = jobService.resumeJob(jobName, jobGroup);
		return result;
	}

	@PostMapping("/remove")
	public  Result remove(String jobName, String jobGroup) {
		log.info("移除任务");
        Result result = jobService.removeJob(jobName, jobGroup);
        return result;
	}
}
