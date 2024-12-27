package com.kerbaras.dcbridge.events

import com.kerbaras.dcbridge.discrod.Bridge
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents
import net.minecraft.server.network.ServerPlayerEntity

object PlayerDeath : ChannelSubscriber {

    override fun subscribe(channel: TextChannel) {
        ServerLivingEntityEvents.ALLOW_DEATH.register({ entity, source, _ ->
            if (entity is ServerPlayerEntity)
                Bridge.tell(channel, ":skull: ${source.getDeathMessage(entity).string}")
            true
        })
    }
}