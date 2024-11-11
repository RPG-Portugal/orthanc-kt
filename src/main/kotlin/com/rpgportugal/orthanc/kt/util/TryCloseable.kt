package com.rpgportugal.orthanc.kt.util

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DomainError

fun interface TryCloseable {
    fun tryClose(): DomainError?
    fun asResult(): Either.Right<TryCloseable> = Either.Right(this)
}