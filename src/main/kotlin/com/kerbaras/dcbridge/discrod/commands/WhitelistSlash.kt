package com.kerbaras.dcbridge.discrod.commands

import com.kerbaras.dcbridge.discrod.Bridge
import com.mojang.authlib.GameProfile
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.minecraft.server.WhitelistEntry
import net.minecraft.util.Uuids
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.UUID

@Serializable
data class MCProfile(val id: String, val name: String)

object WhitelistSlash: ListenerAdapter() {

    private val client = OkHttpClient()

    fun register(guild: Guild) {
        guild.updateCommands().addCommands(
            Commands.slash("whitelist", "Adds a user to the whitelist")
            .addOption(OptionType.STRING, "username", "Player's username", true)
            .addOption(OptionType.BOOLEAN, "online", "Online Mode")
        ).queue()
        guild.jda.addEventListener(WhitelistSlash)
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name != "whitelist")
            return
        val username = event.getOption("username")!!.asString
        val online = event.getOption("online")?.asBoolean ?: true
        if (!online) {
            event.reply("Adding $username to the whitelist in offline mode").setEphemeral(true).queue()
            return whitelistPlayer(Uuids.getOfflinePlayerProfile(username))
        }

        val request = Request.Builder()
            .url("https://api.mojang.com/users/profiles/minecraft/$username")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                event.reply("Online user not found. Adding $username to the whitelist in offline mode").setEphemeral(true).queue()
                return whitelistPlayer(Uuids.getOfflinePlayerProfile(username))
            }

            val id = Json.decodeFromString<MCProfile>(response.body!!.string()).id
            if (id.isBlank()) {
                event.reply("Invalid UUID. Adding $username to the whitelist in offline mode").setEphemeral(true).queue()
                return whitelistPlayer(Uuids.getOfflinePlayerProfile(username))
            }

            val uid = "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w+)"
                .toRegex()
                .replace(id, "$1-$2-$3-$4-$5")
            event.reply("Adding $username to the whitelist").setEphemeral(true).queue()
            return whitelistPlayer(GameProfile(UUID.fromString(uid), username))
        }
    }

    private fun whitelistPlayer(profile: GameProfile){
        Bridge.server.playerManager.whitelist.add(WhitelistEntry(profile))
        Bridge.server.playerManager.reloadWhitelist()
    }
}