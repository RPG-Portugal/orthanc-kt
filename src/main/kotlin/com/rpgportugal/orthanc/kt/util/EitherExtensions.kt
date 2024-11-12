package com.rpgportugal.orthanc.kt.util

import arrow.core.Either

object EitherExtensions {
    fun <T> T.toLeft(): Either.Left<T> {
        return Either.Left(this)
    }

    fun <T> T.toRight(): Either.Right<T> {
        return Either.Right(this)
    }
}