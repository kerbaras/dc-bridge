package com.kerbaras.dcbridge.events

import com.kerbaras.dcbridge.discrod.Bridge
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents

object PlayerJoined : ChannelSubscriber {

    override fun subscribe(channel: TextChannel) {
        ServerPlayConnectionEvents.JOIN.register({ handler, _, _ ->
            Bridge.tell(channel, "${handler.player.name.literalString} joined the game")
        })
    }
}