package com.rpgportugal.orthanc.kt.persistence.sql.permission

import arrow.core.Either
import com.rpgportugal.orthanc.kt.discord.permission.Permission
import com.rpgportugal.orthanc.kt.error.DbError
import com.rpgportugal.orthanc.kt.error.ThrowableError
import com.rpgportugal.orthanc.kt.persistence.dto.permission.RolePermission
import com.rpgportugal.orthanc.kt.persistence.repository.permission.RolePermissionRepository
import com.rpgportugal.orthanc.kt.util.EitherExtensions.toLeft
import com.rpgportugal.orthanc.kt.util.EitherExtensions.toRight
import jakarta.persistence.EntityManager
import java.sql.SQLException

class SqlRolePermissionRepository(
    private val entityManager: EntityManager
) : RolePermissionRepository {

    override fun hasPermission(permission: Permission, roleIds: List<Long>): Either<DbError, Boolean> {
        if (roleIds.isEmpty()) {
            return false.toRight()
        }

        val query =
            "select RP from RolePermission RP " +
                    "where RP.id.roleId in (:roles) " +
                    "and RP.id.permission = :permission"

        try {
            val result =
                entityManager.createQuery(query, RolePermission::class.java)
                    .setParameter("roles", roleIds)
                    .setParameter("permission", permission)
                    .resultList

            return result.isNotEmpty().toRight()
        } catch (e: SQLException) {
            return ThrowableError(e).toLeft()
        }

    }
}