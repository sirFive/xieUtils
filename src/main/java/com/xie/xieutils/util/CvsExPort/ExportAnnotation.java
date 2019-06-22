package com.xie.xieutils.util.CvsExPort;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExportAnnotation {

    /**
     * 排序--第几列
     * @return
     */
    int order() default 100;

    /**
     * 属性对应的get方法
     * @return
     */
    String method() default "";

    /**
     * 属性对应的excel 的title
     * @return
     */
    String columnTitle() default "";

    /**
     * title数组长度
     * @return
     */
    int length() default 20;

}
