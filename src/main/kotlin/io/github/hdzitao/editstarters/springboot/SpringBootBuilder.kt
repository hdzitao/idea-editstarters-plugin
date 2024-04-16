package io.github.hdzitao.editstarters.springboot

/**
 * SpringBoot构造类
 *
 * @version 3.2.0
 */
interface SpringBootBuilder<From> {
    fun buildSpringBoot(from: From): SpringBoot
}
