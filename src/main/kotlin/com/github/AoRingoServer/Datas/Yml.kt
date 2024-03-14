package com.github.AoRingoServer.Datas

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File

class Yml(private val plugin: Plugin) {
    fun acquisitionYml(path: String, fileName: String): YamlConfiguration {
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
    fun acquisitionCookingMethodData(cookingKey: String): ConfigurationSection? {
        val cookingMethodData = cookingMethodData()
        return cookingMethodData.getConfigurationSection(cookingKey)
    }
    fun cookingMethodData(): YamlConfiguration {
        return acquisitionYml("", "CookingMethodData")
    }
    fun acquisitionKey(data: ConfigurationSection, value: String): String? {
        data.getKeys(false).forEach { key ->
            if (data.getString(key) == value) {
                return key
            }
        }
        return null
    }
}
