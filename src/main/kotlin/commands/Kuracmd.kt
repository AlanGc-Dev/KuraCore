package com.kuraky.commands


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)

annotation class Kuracmd (
    val main: String,
    val sub: String = "",
    val aliases: Array<String> = [],
    val permissions: String = "",
    val senderType: SenderType = SenderType.Both,
    val description: String = "",
)