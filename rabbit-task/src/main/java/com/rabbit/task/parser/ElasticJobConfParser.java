package com.rabbit.task.parser;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.rabbit.task.annotation.ElasticJobConfig;
import com.rabbit.task.configutation.JobZkProperties;
import com.rabbit.task.enums.ElasticJobTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Evan
 * @create 2021/2/23 9:21
 */
@Slf4j
public class ElasticJobConfParser implements ApplicationListener<ApplicationReadyEvent> {

    private JobZkProperties jobZkProperties;

    private ZookeeperRegistryCenter zookeeperRegistryCenter;

    public ElasticJobConfParser(JobZkProperties jobZkProperties,
                                ZookeeperRegistryCenter zookeeperRegistryCenter){
        this.jobZkProperties = jobZkProperties;
        this.zookeeperRegistryCenter = zookeeperRegistryCenter;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        try {
            ApplicationContext context = applicationReadyEvent.getApplicationContext();
            Map<String, Object> map = context.getBeansWithAnnotation(ElasticJobConfig.class);
            for (Iterator<?> it = map.values().iterator(); it.hasNext();){
                Object bean = it.next();
                Class<?> clazz = bean.getClass();
                if (clazz.getName().indexOf("$") > 0){
                    String className = clazz.getName();
                    clazz = Class.forName(className.substring(0, className.indexOf("$")));
                }

                String jobTypeName  = clazz.getInterfaces()[0].getSimpleName();
                ElasticJobConfig config = clazz.getAnnotation(ElasticJobConfig.class);

                String jobClass = clazz.getName();
                String jobName = jobZkProperties.getNamespace() + "." + config.name();

                JobCoreConfiguration coreConfiguration = JobCoreConfiguration
                        .newBuilder(jobName, config.cron(), config.shardingTotalCount())
                        .build();

                JobTypeConfiguration typeConfiguration = null;
                if (ElasticJobTypeEnum.SIMPLE.getType().equals(jobTypeName)){
                    typeConfiguration = new SimpleJobConfiguration(coreConfiguration, jobClass);
                } else if (ElasticJobTypeEnum.DATAFLOW.getType().equals(jobTypeName)){
                    typeConfiguration = new DataflowJobConfiguration(coreConfiguration, jobClass, config.streamingProcess());
                } else if (ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)){
                    typeConfiguration = new ScriptJobConfiguration(coreConfiguration, jobClass);
                }

                LiteJobConfiguration jobConfiguration = LiteJobConfiguration
                        .newBuilder(typeConfiguration)
                        .overwrite(config.overwrite())
                        .build();

                BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
                factory.setInitMethodName("init");
                factory.setScope("prototype");

                if (!ElasticJobTypeEnum.SCRIPT.getType().equals(jobTypeName)){
                    factory.addConstructorArgValue(bean);
                }
                factory.addConstructorArgValue(zookeeperRegistryCenter);
                factory.addConstructorArgValue(jobConfiguration);

                if (StringUtils.isNotBlank(config.eventTraceRdbDataSource())){
                    BeanDefinitionBuilder rdbFactory = BeanDefinitionBuilder.rootBeanDefinition(JobEventRdbConfiguration.class);
                    rdbFactory.addConstructorArgReference(config.eventTraceRdbDataSource());
                    factory.addConstructorArgValue(rdbFactory);
                }

                List<?> elasticJobListeners = getElasticJobListeners(config);
                factory.addConstructorArgValue(elasticJobListeners);

                DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();

                String beanName = config.name() + "SpringJobScheduler";
                defaultListableBeanFactory.registerBeanDefinition(beanName, factory.getBeanDefinition());

                SpringJobScheduler jobScheduler = (SpringJobScheduler) context.getBean(beanName);
                jobScheduler.init();
                log.info("Elastic-Job start : {}", jobName);
            }
            log.info("Elastic-Job start size : {}", map.size());
        } catch (Exception e){
            log.error("Elastic-Job start error : {}", e);
        }
    }

    private List<BeanDefinition> getElasticJobListeners(ElasticJobConfig elasticJobConfig){
        List<BeanDefinition> result = new ManagedList<BeanDefinition>(2);
        String listeners = elasticJobConfig.listener();
        if (StringUtils.isNotBlank(listeners)){
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listeners);
            factory.setScope("prototype");
            result.add(factory.getBeanDefinition());
        }

        String distributedListeners = elasticJobConfig.distributedListener();
        if (StringUtils.isNotBlank(distributedListeners)){
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(distributedListeners);
            factory.setScope("prototype");
            factory.addConstructorArgValue(elasticJobConfig.startedTimeoutMilliseconds());
            factory.addConstructorArgValue(elasticJobConfig.completedTimeoutMilliseconds());
            result.add(factory.getBeanDefinition());
        }
        return result;
    }










}
