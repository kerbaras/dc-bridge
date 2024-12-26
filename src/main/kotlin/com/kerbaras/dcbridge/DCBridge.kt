package com.kerbaras.dcbridge

import com.kerbaras.dcbridge.commands.DiscordCommand
import com.kerbaras.dcbridge.discrod.Bridge
import com.kerbaras.dcbridge.discrod.Bridge.onMessageSent
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class DCBridge : ModInitializer {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger("dc-bridge")
    }

    override fun onInitialize() {
        ConfigManager.load()
        ServerLifecycleEvents.SERVER_STARTING.register(ServerStarting { server: MinecraftServer ->
            Bridge.server = server
            Bridge.initialize()
        })
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _, _ ->
            dispatcher.register(DiscordCommand())
        })


        ServerMessageEvents.CHAT_MESSAGE.register({ message, sender, _ ->
            Bridge.onMessageSent(sender, message.content.string!!)
        })
        ServerPlayConnectionEvents.JOIN.register({ handler, _, _ ->
            Bridge.onPlayerJoined(handler.player!!)
        })
        ServerPlayConnectionEvents.DISCONNECT.register({ handler, _ ->
            Bridge.onPlayerLeave(handler.player!!)
        })
        ServerLivingEntityEvents.ALLOW_DEATH.register({ entity, source, _ ->
            if (entity is ServerPlayerEntity)
                 Bridge.onPlayerDeath(entity, source.getDeathMessage(entity).string!!)

            true
        })
        ServerTickEvents.END_SERVER_TICK.register({ ticker ->
            if (ticker.ticks % 100 == 0)
                Bridge.tick()
        })
    }
}
