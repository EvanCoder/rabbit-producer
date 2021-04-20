package com.rabbit.task.configutation;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.rabbit.task.parser.ElasticJobConfParser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Evan
 * @create 2021/2/22 10:45
 */
@Configuration
@ConditionalOnProperty(prefix = "elastic.job.zk", name = {"namespace", "serverList"},
matchIfMissing = false)
@EnableConfigurationProperties(JobZkProperties.class)
public class JobAutoConfiguration {

    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter(JobZkProperties jobZkProperties){
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(
                jobZkProperties.getNamespace(), jobZkProperties.getServerList()
        );
        zookeeperConfiguration.setMaxRetries(jobZkProperties.getMaxRetries());

        return new ZookeeperRegistryCenter(zookeeperConfiguration);
    }

    @Bean
    public ElasticJobConfParser elasticJobConfParser(JobZkProperties jobZkProperties,
                                ZookeeperRegistryCenter zookeeperRegistryCenter){
        return new ElasticJobConfParser(jobZkProperties, zookeeperRegistryCenter);
    }

}
