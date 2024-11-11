package com.rpgportugal.orthanc.kt.persistence.repository.permission

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.permission.Permission
import com.rpgportugal.orthanc.kt.error.DbError

interface RolePermissionRepository {
    fun hasPermission(permission: Permission, roleIds: List<Long>): Either<DbError, Boolean>
}