package com.atguigu.shardingjdbcdemo.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * 
 * CustNameGenaritor
 * @CreateDate Aug 22, 2018
 * @author Roy Zeng
 * @Version 1.0
 *
 */
public class CustNameGenaritor extends AnnotationBeanNameGenerator {

    @Override
    protected String buildDefaultBeanName(BeanDefinition definition) {
        return definition.getBeanClassName();
    }

}
