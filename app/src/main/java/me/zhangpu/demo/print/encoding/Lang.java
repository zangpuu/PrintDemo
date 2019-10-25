package me.zhangpu.demo.print.encoding;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by virgil on 2018/1/3.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@IntDef(value = {Lang.CHINESE_SIMPLIE, Lang.CHINESE_TRANDITIONAL}, flag = false)
public @interface Lang {
    /**
     * 简体中文
     */
    int CHINESE_SIMPLIE = 0;
    /**
     * 繁体中文
     */
    int CHINESE_TRANDITIONAL = 1;

}
