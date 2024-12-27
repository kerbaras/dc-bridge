package com.kerbaras.dcbridge.discrod

import com.kerbaras.dcbridge.ConfigManager
import kotlinx.coroutines.sync.Mutex
import net.dv8tion.jda.api.entities.Activity
import net.minecraft.server.MinecraftServer
import java.util.*

class StatusManager (val client: DiscordClient) {
    private val timer = Timer(true)
    private var status = ""
    private val available = Mutex(false)
    private val delay: Long = 600000L

    fun updateStatus(server: MinecraftServer){
        if (!ConfigManager.configs.features.status)
            return
        val manager = server.playerManager
        val playerList = manager.playerList
        setStatus("${playerList.size} / ${manager.maxPlayerCount} players")
    }

    fun setStatus(status: String) {
        if (status == this.status)
            return

        if (!available.tryLock())
            return

        timer.schedule(object : TimerTask() {
            override fun run() {
                available.unlock()
            }
        }, delay)
        client.jda.presence.activity = Activity.playing(status)
        this.status = status
    }
}