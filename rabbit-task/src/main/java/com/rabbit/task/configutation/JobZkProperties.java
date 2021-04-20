package com.rabbit.task.configutation;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @author Evan
 * @create 2021/2/22 10:45
 */
@ConditionalOnProperty(prefix = "elastic.job.zk")
@Data
public class JobZkProperties {

    private String namespace;

    private String serverList;

    private int maxRetries = 3;

    private int connectionTimeoutMilliseconds = 10000;

    private int sessionTimeoutMilliseconds = 60000;

}
