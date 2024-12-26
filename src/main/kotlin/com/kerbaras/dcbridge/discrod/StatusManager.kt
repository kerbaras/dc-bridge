package com.kerbaras.dcbridge.discrod

import kotlinx.coroutines.sync.Mutex
import java.util.*

class StatusManager (val client: DiscordClient) {
    private val timer = Timer(true)
    private var status = ""
    private val available = Mutex(false)
    private val delay: Long = 600000L

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

        client.setStatus(status)
        this.status = status
    }
}