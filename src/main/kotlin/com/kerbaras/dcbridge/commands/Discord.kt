package com.kerbaras.dcbridge.commands

import com.kerbaras.dcbridge.ConfigManager
import net.minecraft.server.command.CommandManager.*
import com.mojang.brigadier.arguments.LongArgumentType.*
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.server.command.ServerCommandSource

fun DiscordCommand(): LiteralArgumentBuilder<ServerCommandSource> {
    return literal("discord")
            .requires({ source -> source.hasPermissionLevel(2) })
            .then(literal("channel")
                .then(argument("channelId", longArg())
                    .executes({ ctx ->
                        val channelId = getLong(ctx, "channelId")
                        ConfigManager.configs.channel = channelId
                        ConfigManager.save()
                        0
                    })))
}