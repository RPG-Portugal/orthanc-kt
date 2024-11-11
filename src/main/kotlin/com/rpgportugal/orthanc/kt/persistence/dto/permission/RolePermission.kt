package com.rpgportugal.orthanc.kt.persistence.dto.permission

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "otc_role_permission")
open class RolePermission {

    @EmbeddedId
    open var id: RolePermissionId = RolePermissionId()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RolePermission) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}