package com.rabbit.common.idworker;

import com.rabbit.common.idworker.strategy.DefaultWorkIdStrategy;
import com.rabbit.common.utils.Utils;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Id {

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

    public static long nextId(){
        return idWorker.nextId();
    }

    public static long getWorkerId(){
        return idWorker.getWorkerId();
    }

}
