package com.imooc.pan.server.common.annotation;

import java.lang.annotation.*;

/**
 * 该注解主要影响那些不需要登录的接口
 * 标注该注解的方法会自动屏蔽统一的登录拦截校验逻辑
 */

/**
 *
 * @Documented:
 * 这个元注解表示LoginIgnore注解应该被javadoc或类似的工具记录。这意味着当你生成API文档时，LoginIgnore注解会被包含在内。
 * @Retention(RetentionPolicy.RUNTIME):
 * 这个元注解用于指定LoginIgnore注解的保留策略。RUNTIME意味着这个注解不仅会被保留在类文件中，还会在运行时被JVM保留，因此你可以通过反射机制读取它。
 * @Target({ElementType.METHOD}):
 * 这个元注解用于限制LoginIgnore注解可以应用的Java元素类型。在这里，它只能被应用于方法（ElementType.METHOD）。
 * public @interface LoginIgnore {}:
 * 这定义了一个名为LoginIgnore的公共注解接口。由于它是一个注解，所以它的定义是空的，不需要任何方法或字段。
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface LoginIgnore {
}
