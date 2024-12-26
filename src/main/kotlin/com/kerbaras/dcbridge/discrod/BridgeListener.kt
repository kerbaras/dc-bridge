package com.kerbaras.dcbridge.discrod

import net.dv8tion.jda.api.entities.Member
import net.minecraft.server.network.ServerPlayerEntity

interface BridgeListener {
    fun onMessageReceived(member: Member, message: String)
    fun onMessageSent(player: ServerPlayerEntity, message: String)
    fun onPlayerJoined(player: ServerPlayerEntity)
    fun onPlayerLeave(player: ServerPlayerEntity)
    fun onPlayerDeath(player: ServerPlayerEntity, reason: String)
}