package com.kerbaras.dcbridge.discrod

import club.minnced.discord.webhook.WebhookClient
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
    val channels: HashMap<Long, ChannelEntry> = HashMap()

    companion object {
        fun setup(): DiscordClient {
            val jda = JDABuilder.createLight(ConfigManager.configs.token)
                .enableIntents(GatewayIntent.GUILD_MESSAGES)
                .enableIntents(GatewayIntent.GUILD_WEBHOOKS)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build()
            val client = DiscordClient(jda)
            jda.addEventListener(client)
            return client
        }
    }

    private fun getOrCreate(channel: TextChannel): WebhookClient {
        var webhook = channel.retrieveWebhooks().complete().find { webhook -> webhook?.name == "mc-chat" }
        if (webhook == null)
            webhook = channel.createWebhook("mc-chat").complete()

        val client = JDAWebhookClient.from(webhook!!)
        webhooks.addWebhooks(client)
        return client
    }

    private fun register(){
        ConfigManager.configs.channels.forEach { config ->
            val channel = jda.getTextChannelById(config.id)!!
            val webhook = getOrCreate(channel)
            channels[config.id] = ChannelEntry(channel, webhook, config)

            config.watch
                .flatMap { it.events }
                .toSet()
                .forEach { it.subscribe(channel) }
        }
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

    private fun updateCommands(){
        channels
            .map { it.value.textChannel.guild }
            .forEach { guild ->
                if (ConfigManager.configs.features.whitelist)
                    WhitelistSlash.register(guild)
            }
    }

    override fun onReady(event: ReadyEvent) {
        register()
        updateCommands()
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if(!channels.containsKey(event.channel.idLong) || channels[event.channel.idLong]!!.config.muted)
            return

        if (event.author.isBot || event.message.member == null)
            return

        Bridge.say(event.message.member!!, event.message.contentRaw)
    }

    fun tell(channel: TextChannel, message: String) {
        channel.sendMessage(message).queue()
    }

    fun tell(channel: TextChannel, message: String, name: String, avatar: String) {
        val msg = WebhookMessageBuilder()
            .setUsername(name)
            .setContent(message)
            .setAvatarUrl(avatar)
            .build()
        channels[channel.idLong]!!.webhook.send(msg)
    }
}