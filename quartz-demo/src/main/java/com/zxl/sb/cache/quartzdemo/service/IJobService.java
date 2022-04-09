package com.zxl.sb.cache.quartzdemo.service;

import com.github.pagehelper.PageInfo;
import com.zxl.sb.cache.quartzdemo.common.Result;
import com.zxl.sb.cache.quartzdemo.entity.QuartzJob;

public interface IJobService {

    PageInfo listQuartzJob(String jobName, Integer pageNo, Integer pageSize);

    /**
     * 新增job
     * @param quartz
     * @return
     */
    Result saveJob(QuartzJob quartz);

    /**
     * 触发job
     * @param jobName
     * @param jobGroup
     * @return
     */
    Result triggerJob(String jobName, String jobGroup);

    /**
     * 暂停job
     * @param jobName
     * @param jobGroup
     * @return
     */
    Result pauseJob(String jobName, String jobGroup);

    /**
     * 恢复job
     * @param jobName
     * @param jobGroup
     * @return
     */
    Result resumeJob(String jobName, String jobGroup);

    /**
     * 移除job
     * @param jobName
     * @param jobGroup
     * @return
     */
    Result removeJob(String jobName, String jobGroup);

    QuartzJob getJob(String jobName, String jobGroup);

    Result updateJob(QuartzJob quartz);

    void schedulerJob(QuartzJob job) throws Exception;
}