package io.github.hdzitao.editstarters.initializr;

/**
 * SpringBoot构造类
 *
 * @version 3.2.0
 */
public interface SpringBootBuilder<From> {
    SpringBoot buildSpringBoot(From from);
}
