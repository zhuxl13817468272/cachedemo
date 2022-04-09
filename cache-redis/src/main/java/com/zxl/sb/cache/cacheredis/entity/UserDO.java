package com.zxl.sb.cache.cacheredis.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@TableName(value = "users")
public class UserDO implements Serializable {

    private Integer id; // 用户编号

    private String username; // 账号

    private String password; // 密码

    private Date createTime; // 创建时间

//    @TableLogic
    private Integer deleted; // 是否删除

}
