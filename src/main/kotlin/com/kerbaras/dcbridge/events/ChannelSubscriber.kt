package com.kerbaras.dcbridge.events

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel

interface ChannelSubscriber {
    fun subscribe(channel: TextChannel)
}