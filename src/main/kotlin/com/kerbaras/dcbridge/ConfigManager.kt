package com.kerbaras.dcbridge

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File

object ConfigManager {
    private val file = File("./config/dcbridge.json")
    var configs: Configs = Configs(channel = 0)

     fun load() {
         if (!file.exists())
             file.createNewFile()

         val json = Json { prettyPrint = true }
         try {
             configs = json.decodeFromString<Configs>(file.readText())
         } catch(ignored: Exception) {}
    }

    fun save(){
        if (!file.exists()) {
            file.createNewFile()
        }
        val json = Json { prettyPrint = true }
        file.writeText(json.encodeToString(configs))
    }
}