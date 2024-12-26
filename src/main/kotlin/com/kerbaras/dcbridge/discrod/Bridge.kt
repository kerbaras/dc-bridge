package com.kerbaras.dcbridge.discrod

import net.dv8tion.jda.api.entities.Member
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import java.awt.Color
import java.util.*


object Bridge: BridgeListener {

    lateinit var server: MinecraftServer
    lateinit var discord: DiscordClient
    lateinit var statusManager: StatusManager

    fun initialize() {
        discord = DiscordClient.setup()
        statusManager = StatusManager(discord)
    }

    override fun onMessageReceived(member: Member, message: String) {
        server.playerManager.broadcast(
            Text.empty()
                .append(
                    Text.literal("<${member.nickname}> ")
                        .styled { style -> style.withColor(member.colorRaw) })
                .append(Text.literal(message)),
            false
        )
    }

    override fun onMessageSent(player: ServerPlayerEntity, message: String) {
        discord.say(message, player.name.string!!, "https://mc-heads.net/head/${player.uuidAsString}")
    }

    override fun onPlayerJoined(player: ServerPlayerEntity) {
        discord.say("${player.name.string!!}: joined the server")
    }

    override fun onPlayerLeave(player: ServerPlayerEntity) {
        discord.say("${player.name.string!!}: left the server")
    }

    fun tick() {
        val list = server.playerManager.playerList
        val plural = if (list.size == 1) "player" else "players"
        statusManager.setStatus("${list.size} $plural online")
    }

    override fun onPlayerDeath(player: ServerPlayerEntity, reason: String) {
        discord.say(":skull: $reason")
    }
}