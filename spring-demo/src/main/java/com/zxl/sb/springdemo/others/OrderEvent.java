package com.zxl.sb.springdemo.others;

import org.springframework.context.ApplicationEvent;

public class OrderEvent extends ApplicationEvent {
    private String name;
    public OrderEvent(Object source,String name) {
        super(source);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
