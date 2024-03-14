package com.github.AoRingoServer.Datas

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException

class Yml(private val plugin: Plugin) {
    private fun acquisitionYml(path: String, fileName: String):YamlConfiguration{
        val playerDataFolder = File(plugin.dataFolder, path)
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs()
        }

        val filePath = File(playerDataFolder, "$fileName.yml")
        return YamlConfiguration.loadConfiguration(filePath)
    }
    fun makePluginFolder() {
        val dataFolder = plugin.dataFolder
        if (dataFolder.exists()) { return }
        dataFolder.mkdirs()
    }
    fun getList(path: String, fileName: String, key: String): MutableList<String>? {
        val yml = acquisitionYml(path, fileName)
        return yml.getStringList(key)
    }
}
