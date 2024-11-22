package com.rpgportugal.orthanc.kt.dependencies.bot

import com.rpgportugal.orthanc.kt.dependencies.DepModule
import com.rpgportugal.orthanc.kt.discord.module.BotModule
import com.rpgportugal.orthanc.kt.discord.modules.warn.new.member.response.WarnNewMemberRespondedModule
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

object WarmNewMemberRespondedDepModule : DepModule {
    override val module: Module = module {
        factoryOf(::WarnNewMemberRespondedModule).bind(BotModule::class)
    }
}