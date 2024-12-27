package com.kerbaras.dcbridge.events

import com.kerbaras.dcbridge.discrod.Bridge
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents

object ChatMessage : ChannelSubscriber {

    override fun subscribe(channel: TextChannel) {
        ServerMessageEvents.CHAT_MESSAGE.register({ message, player, _ ->
            Bridge.tell(channel, message.content.literalString!!, player.name.string!!, "https://mc-heads.net/head/${player.uuidAsString}")
        })
    }
}