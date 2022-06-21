package com.nhwb.aliyun.conf;

import com.nhwb.aliyun.ddns.DDNSRefresh;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class MyApplicationRunner implements ApplicationRunner {
    @Autowired
    private DDNSRefresh DDNSRefresh;
    @Value("${dns.client.period}")
    private Integer period;

    @Override
    public void run(ApplicationArguments args) {
        //定时器
        Timer timer = new Timer();
        //第一次开始时间
        final Date date = new Date();
        //定时任务
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                DDNSRefresh.refresh();
            }
        };
        timer.scheduleAtFixedRate(task, date, 1000L * period);
    }
}
