package com.kerbaras.dcbridge.discrod

import club.minnced.discord.webhook.WebhookClient
import com.kerbaras.dcbridge.ChannelConfig
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel

data class ChannelEntry(
    val textChannel: TextChannel,
    val webhook: WebhookClient,
    val config: ChannelConfig
)