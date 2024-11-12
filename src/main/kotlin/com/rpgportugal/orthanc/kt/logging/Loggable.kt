package com.rpgportugal.orthanc.kt.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface Loggable

fun getLogger(clazz: Class<*>): Logger = LoggerFactory.getLogger(clazz)
fun getLogger(name: String): Logger = LoggerFactory.getLogger(name)

inline fun <reified T : Loggable> T.logger(): Logger = getLogger(T::class.java)
inline val <reified T : Loggable> T.log: Logger
    get() = getLogger(T::class.java)