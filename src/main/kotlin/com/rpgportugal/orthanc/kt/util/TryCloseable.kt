package com.rpgportugal.orthanc.kt.util

import com.rpgportugal.orthanc.kt.error.DomainError

fun interface TryCloseable {
    fun tryClose(): DomainError?
}