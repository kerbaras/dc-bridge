package com.kerbaras.dcbridge.events

import com.kerbaras.dcbridge.discrod.Bridge
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents

object ServerStarting : ChannelSubscriber {

    override fun subscribe(channel: TextChannel) {
        ServerLifecycleEvents.SERVER_STARTED.register({
            Bridge.tell(channel,"Server has started")
        })
    }
}