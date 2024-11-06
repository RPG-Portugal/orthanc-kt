package com.rpgportugal.orthanc.kt.discord.modules.roleAward

import com.rpgportugal.orthanc.kt.discord.CloseableListenerAdapter
import com.rpgportugal.orthanc.kt.error.DomainError

class RoleAwardListenerAdapter(
    val roleId: Long,
    val adminAwardRole: Long,
    val threshold: Long,
    val warningChannelId: Long,
    val emojiNames: List<String>
) : CloseableListenerAdapter() {



    override fun tryClose(): DomainError? {

    }

}