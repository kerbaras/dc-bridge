package com.kerbaras.dcbridge

import com.kerbaras.dcbridge.events.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class Watchers(val events: List<ChannelSubscriber>) {

    @SerialName("joined")
    PLAYER_JOINED(listOf(PlayerJoined)),

    @SerialName("disconnect")
    PLAYER_LEFT(listOf(PlayerLeft)),

    @SerialName("death")
    PLAYER_DEATH(listOf(PlayerDeath)),

    @SerialName("player")
    PLAYER(listOf(PLAYER_DEATH, PLAYER_LEFT, PLAYER_JOINED).flatMap { it.events }),

    @SerialName("system")
    SYSTEM(listOf()),

    @SerialName("info")
    INFO(listOf(PLAYER, SYSTEM).flatMap { it.events }),

    @SerialName("chat")
    CHAT(listOf(ChatMessage))
}

@Serializable
data class ChannelConfig(
    val id: Long,
    val watch: Collection<Watchers> = listOf(Watchers.CHAT, Watchers.INFO),
    val muted: Boolean = false,
)

@Serializable
data class Features(
    val whitelist: Boolean = false,
    val status: Boolean = false,
)

@Serializable
data class Configs(
    val token: String,
    val channels: List<ChannelConfig> = listOf(),
    val features: Features = Features(),
)


