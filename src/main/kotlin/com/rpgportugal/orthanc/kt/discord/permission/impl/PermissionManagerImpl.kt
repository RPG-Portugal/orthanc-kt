package com.rpgportugal.orthanc.kt.discord.permission.impl

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.permission.Permission
import com.rpgportugal.orthanc.kt.discord.permission.PermissionManager
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.persistence.repository.permission.RolePermissionRepository
import net.dv8tion.jda.api.entities.Role

class PermissionManagerImpl(
    private val rolePermissionRepository: RolePermissionRepository
) : PermissionManager {

    override fun hasPermission(permission: Permission,roles: List<Role>): Either<DbError,Boolean> {
        val roleIds = roles.map { it.idLong }.toList()
        return rolePermissionRepository.hasPermission(permission, roleIds)
    }

}