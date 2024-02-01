package com.myboy.sql.util;

import com.myboy.sql.annotation.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 处理类的工具类
 */
public class ClassUtil {

    @Column
    private static final Logger log = LoggerFactory.getLogger(ClassUtil.class);

    /**
     * 扫描类
     *
     * @param basePackage 包名，如 com.spring.annotations.*
     * @param classFilter 类过滤器，只留下符合条件的类
     */
    public static Set<Class<?>> scanClass(String basePackage, Predicate<Class<?>> classFilter) {
        Set<Class<?>> classSet = new HashSet<>();
        basePackage = basePackage.replace('.', '/');
        try {
            PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = patternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + basePackage);
            String classBasePackage = basePackage.substring(0, basePackage.indexOf("*"));
            for (Resource resource : resources) {
                if (resource.getFilename() == null || !resource.getFilename().endsWith(".class")) {
                    continue;
                }
                // 拿到class的绝对路径
                String classPath = resource.getURL().getPath();
                // 截取最后一段的 classPath 类路径，经过测试，这里 maven 和 gradle 项目都可以适配，在 jar 内的路径也能适配
                classPath = classPath.substring(classPath.indexOf(classBasePackage));

                Class<?> clazz = Class.forName(classPath.replace('/','.').replace(".class", ""));
                if (classFilter.test(clazz)){
                    classSet.add(clazz);
                }
            }
        } catch (Exception e) {
            log.error("scan package error", e);
        }
        return classSet;
    }

}
