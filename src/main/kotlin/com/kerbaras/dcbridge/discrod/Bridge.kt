package com.kerbaras.dcbridge.discrod

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.minecraft.server.MinecraftServer
import net.minecraft.text.Text


object Bridge {

    lateinit var server: MinecraftServer
    lateinit var discord: DiscordClient
    lateinit var statusManager: StatusManager

    fun initialize() {
        discord = DiscordClient.setup()
        statusManager = StatusManager(discord)
    }

    fun tell(channel: TextChannel, message: String) {
        discord.tell(channel, message)
    }

    fun tell(channel: TextChannel, message: String, username: String, avatarUrl: String) {
        discord.tell(channel, message, username, avatarUrl)
    }

    fun tick() {
        statusManager.updateStatus(server)
    }

    fun say(member: Member, message: String) {
        server.playerManager.broadcast(
            Text.empty()
                .append(
                    Text.literal("<${member.nickname}> ")
                        .styled { style -> style.withColor(member.colorRaw) })
                .append(Text.literal(message)),
            false
        )
    }

}