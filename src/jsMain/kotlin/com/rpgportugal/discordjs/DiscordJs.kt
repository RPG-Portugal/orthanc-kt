package com.rpgportugal.discordjs

import kotlin.js.Promise

@JsModule("discordjs")
external class Client(clientOptions: ClientOptions) {

    // returns Token
    fun login(token: String) : Promise<String>

    fun channels(): ChannelManager
}

@JsModule("discordjs")
external interface ClientOptions {}

@JsModule("discordjs")
external class ChannelManager