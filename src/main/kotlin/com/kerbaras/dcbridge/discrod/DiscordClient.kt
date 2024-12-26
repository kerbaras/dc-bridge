package com.kerbaras.dcbridge.discrod

import club.minnced.discord.webhook.WebhookCluster
import club.minnced.discord.webhook.external.JDAWebhookClient
import club.minnced.discord.webhook.send.WebhookMessageBuilder
import com.kerbaras.dcbridge.ConfigManager
import com.kerbaras.dcbridge.discrod.commands.WhitelistSlash
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent

class DiscordClient(val jda: JDA): ListenerAdapter() {
    val webhooks: WebhookCluster = WebhookCluster()
    lateinit var channel: TextChannel

    companion object {
        fun setup(): DiscordClient {
            val jda = JDABuilder.createLight(System.getenv("DISCORD_TOKEN"))
                .enableIntents(GatewayIntent.GUILD_MESSAGES)
                .enableIntents(GatewayIntent.GUILD_WEBHOOKS)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build()
            val client = DiscordClient(jda)
            jda.addEventListener(client)
            return client
        }
    }

    fun register(){
        try {
            channel = jda.getTextChannelById(ConfigManager.configs.channel)!!
        } catch (ignored: Exception){
            return
        }

        var webhook = channel.retrieveWebhooks().complete().find { webhook -> webhook?.name == "mc-chat" }
        if (webhook == null) {
            webhook = channel.createWebhook("mc-chat").complete()
        }
        webhooks.addWebhooks(JDAWebhookClient.from(webhook!!))
    }

    fun say(message: String, name: String, avatar: String) {
        val msg = WebhookMessageBuilder()
            .setUsername(name) // use this username
            .setContent(message)
            .setAvatarUrl(avatar)
            .build()
        webhooks.broadcast(msg)
    }

    fun say(message: String) {
        val msg = WebhookMessageBuilder()
            .setUsername("Minecraft Server")
            .setContent(message)
            .build()
        webhooks.broadcast(msg)
    }

    fun setStatus(message: String) {
        channel.manager.setTopic(message).queue()
    }

    private fun updateCommands(){
        jda.guilds.forEach { guild ->
            WhitelistSlash.register(guild)
        }
    }

    override fun onReady(event: ReadyEvent) {
        register()
        updateCommands()
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot || event.message.member == null)
            return

        Bridge.onMessageReceived(event.message.member!!, event.message.contentRaw)
    }
}