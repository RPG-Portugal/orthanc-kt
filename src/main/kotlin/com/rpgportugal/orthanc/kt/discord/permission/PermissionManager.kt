package com.rpgportugal.orthanc.kt.discord.permission

import arrow.core.Either
import com.rpgportugal.orthanc.kt.error.DbError
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Role

interface PermissionManager {
    fun hasPermission(permission: Permission, roles: List<Role>): Either<DbError, Boolean>

    fun hasPermission(permission: Permission, member: Member): Either<DbError, Boolean> {
        return hasPermission(permission, member.roles)
    }
}