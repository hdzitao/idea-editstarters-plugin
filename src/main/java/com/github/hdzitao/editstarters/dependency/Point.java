package com.github.hdzitao.editstarters.dependency;

/**
 * 只有point()相同,判定两个依赖相同
 * 不用equals,避免和一些通用的判断相等的地方起冲突
 */
public interface Point {
    String point();
}