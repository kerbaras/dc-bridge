package com.kerbaras.dcbridge

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.encodeToString
import java.io.File

object ConfigManager {
    private val file = File("./config/dcbridge.yaml")
    lateinit var configs: Configs

     fun load() {
         if (!file.exists())
             file.createNewFile()

         try {
             configs = Yaml.default.decodeFromString(Configs.serializer(), file.readText())
         } catch(ignored: Exception) {}
    }

    fun save(){
        if (!file.exists()) {
            file.createNewFile()
        }
        val content = Yaml.default.encodeToString(Configs.serializer(), configs)
        file.writeText(content)
    }
}