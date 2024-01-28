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
     * @param basePackage 包名，如 com.spring.annotations
     * @param classFilter 类过滤器，只留下符合条件的类
     */
    public static Set<Class<?>> scanClass(String basePackage, Predicate<Class<?>> classFilter) {
        Set<Class<?>> classSet = new HashSet<>();
        basePackage = basePackage.replace('.', '/');
        try {
            PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = patternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + basePackage);
            for (Resource resource : resources) {
                if (resource.getFilename() == null || !resource.getFilename().endsWith(".class")) {
                    continue;
                }
                String protocol = resource.getURL().getProtocol();
                String classPath = resource.getURL().getPath();
                if ("file".equals(protocol)){
                    classPath = classPath.substring(classPath.indexOf("classes/")+8);
                } else {
                    classPath = classPath.substring(classPath.indexOf("!/")+2);
                }

                // 不处理内部类
                if (classPath.contains("$")){
                    continue;
                }

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
