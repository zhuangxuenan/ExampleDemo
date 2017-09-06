package com.xuenan.example.inter;

/**用于带泛型的接口回调 可以是String 也可以是Model
 * Author: xuenan
 * Date: 2017/02/25
 * Time: 9:34
 * Usage:
 */
public interface AppInterface <T>{
    void doSomething(T params);
}
