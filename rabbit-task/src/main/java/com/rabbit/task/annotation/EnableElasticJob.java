package com.rabbit.task.annotation;

import com.rabbit.task.configutation.JobZkProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Evan
 * @create 2021/2/22 14:22
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(JobZkProperties.class)
public @interface EnableElasticJob {

}
