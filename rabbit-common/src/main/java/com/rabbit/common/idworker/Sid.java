package com.rabbit.common.idworker;

import com.rabbit.common.idworker.strategy.DefaultWorkIdStrategy;
import com.rabbit.common.utils.Utils;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 分布式全局ID
 *
 * @author Evan
 * @create 2021/3/4 9:09
 */
@Component
public class Sid {

    private static WorkIdStrategy workIdStrategy;

    private static IdWorker idWorker;

    static {
        configure(DefaultWorkIdStrategy.instance);
    }

    public static synchronized void configure(WorkIdStrategy custom){
        if (workIdStrategy != null){
            workIdStrategy.release();
        }
        workIdStrategy = custom;
        idWorker = new IdWorker(workIdStrategy.availableWorkerId()){
            @Override
            public long getEpoch() {
                return Utils.midNightMills();
            }
        };
    }

    public static String nextId(){
        long id = idWorker.nextId();
        String yyMMdd = new SimpleDateFormat("yyMMdd").format(new Date());
        return yyMMdd + String.format("%14d", id);
    }

    public static String nextShort(){
        long id = idWorker.nextId();
        String yyMMdd = new SimpleDateFormat("yyMMdd").format(new Date());
        return yyMMdd + Utils.padLeft(Utils.encode(id), 10, '0');
    }

}
