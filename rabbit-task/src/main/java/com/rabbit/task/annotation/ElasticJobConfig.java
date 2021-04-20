package com.rabbit.task.annotation;


import java.lang.annotation.*;

/**
 * @author Evan
 * @create 2021/2/22 14:22
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticJobConfig {

    String name();

    String cron() default "";

    int shardingTotalCount() default 1;

    String shardingItemParameters() default "";

    String jobParameter() default "";

    boolean failover() default false;

    boolean misfire() default true;

    String description() default "";

    boolean streamingProcess() default false;

    boolean overwrite() default false;

    String eventTraceRdbDataSource() default "";

    String listener() default "";

    String distributedListener() default "";

    long startedTimeoutMilliseconds() default Long.MAX_VALUE;

    long completedTimeoutMilliseconds() default Long.MAX_VALUE;

}
