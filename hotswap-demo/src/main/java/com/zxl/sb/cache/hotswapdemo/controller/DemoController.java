package com.zxl.sb.cache.hotswapdemo.controller;

import com.zxl.sb.cache.hotswapdemo.convert.UserConvert;
import com.zxl.sb.cache.hotswapdemo.entity.User;
import com.zxl.sb.cache.hotswapdemo.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class DemoController {

    @GetMapping("/HotSwap")
    public String echo(){
        return "HotSwap";
    }

    public static void main(String[] args) {
        User user = new User();
        user.setId(Integer.valueOf("1")).setName("yudaoyuanma").setPassword("nicai");
        UserVo userVo = UserConvert.INSTANCE.convert(user);
        System.out.println("userVo = " + userVo);
    }
}
