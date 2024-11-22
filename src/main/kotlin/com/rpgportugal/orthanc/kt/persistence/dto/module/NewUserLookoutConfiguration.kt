package com.rpgportugal.orthanc.kt.persistence.dto.module

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Check


@Entity
@Table(name = "otc_module_new_user_lookout_configuration")
open class NewUserLookoutConfiguration() {
    @Id
    @Column(name = "id", nullable = false)
    @Check(name = "one_row", constraints = "id = 1")
    open var id: Long = 1L

    @Column(name = "warning_channel_id", nullable = false)
    open var warningChannelId: Long = 0

    @Column(name = "listen_channel_id", nullable = false)
    open var listenChannelId: Long = 0

    @Column(name = "new_user_role_id", nullable = false)
    open var newUserRoleId: Long = 0

    @Column(name = "ping_role_id", nullable = false)
    open var pingRoleId: Long = 0


}