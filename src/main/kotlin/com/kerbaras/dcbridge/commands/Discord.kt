package com.kerbaras.dcbridge.commands

import net.minecraft.server.command.CommandManager.*
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.server.command.ServerCommandSource

fun DiscordCommand(): LiteralArgumentBuilder<ServerCommandSource> {
    return literal("discord")
            .requires({ source -> source.hasPermissionLevel(2) })
            .then(literal("channel"))
}