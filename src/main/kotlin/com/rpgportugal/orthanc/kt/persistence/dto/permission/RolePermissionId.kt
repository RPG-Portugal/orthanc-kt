package com.rpgportugal.orthanc.kt.persistence.dto.permission

import com.rpgportugal.orthanc.kt.discord.permission.Permission
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
open class RolePermissionId {

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "permission", nullable = false)
    open var permission: Permission = Permission.None

    @Column(name = "role_id", nullable = false)
    open var roleId: Long = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RolePermissionId) return false

        if (permission != other.permission) return false
        if (roleId != other.roleId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = permission.hashCode()
        result = 31 * result + roleId.hashCode()
        return result
    }
}