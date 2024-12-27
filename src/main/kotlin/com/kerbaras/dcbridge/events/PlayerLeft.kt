package com.kerbaras.dcbridge.events

import com.kerbaras.dcbridge.discrod.Bridge
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents

object PlayerLeft : ChannelSubscriber {

    override fun subscribe(channel: TextChannel) {
        ServerPlayConnectionEvents.DISCONNECT.register({ handler, _ ->
            Bridge.tell(channel, "${handler.player.name.literalString} left the game")
        })
    }
}