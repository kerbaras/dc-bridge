package com.kerbaras.dcbridge

import com.kerbaras.dcbridge.commands.DiscordCommand
import com.kerbaras.dcbridge.discrod.Bridge
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.MinecraftServer


class DCBridge : ModInitializer {

    override fun onInitialize() {
        ConfigManager.load()

        ServerLifecycleEvents.SERVER_STARTING.register(ServerStarting { server: MinecraftServer ->
            Bridge.server = server
            Bridge.initialize()
        })

        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _, _ ->
            dispatcher.register(DiscordCommand())
        })

        ServerTickEvents.END_SERVER_TICK.register({ ticker ->
            if (ticker.ticks % 100 == 0)
                Bridge.tick()
        })
    }
}
