package com.github.Ringoame196

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException

class Yml(private val plugin: Plugin) {
    fun makePluginFolder() {
        val dataFolder = plugin.dataFolder
        if (dataFolder.exists()) { return }
        dataFolder.mkdirs()
    }
    fun makePlayerDataFolder() {
        val path = plugin.dataFolder.path + "/playerData"
        val file = File(path)
        if (file.exists()) { return }
        file.mkdirs()
    }
    fun addToList(path: String, fileName: String, key: String, item: String) {
        val playerDataFolder = File(plugin.dataFolder, path)
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs()
        }

        val filePath = File(playerDataFolder, "$fileName.yml")
        val yamlConfiguration = YamlConfiguration.loadConfiguration(filePath)

        // 既存のリストを読み込むか新しいリストを作成
        val currentList = yamlConfiguration.getStringList(key) ?: mutableListOf()
        currentList.add(item)

        // リストを設定
        yamlConfiguration.set(key, currentList)

        try {
            yamlConfiguration.save(filePath)
            println("Item '$item' added to the list in $fileName.yml with key: $key")
        } catch (e: IOException) {
            println("Error while saving data: ${e.message}")
        }
    }
    fun getYml(path: String, fileName: String): YamlConfiguration {
        val playerDataFolder = File(plugin.dataFolder, path)
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs()
        }
        val filePath = File(playerDataFolder, "$fileName.yml")
        return YamlConfiguration.loadConfiguration(filePath)
    }
    fun getList(path: String, fileName: String, key: String): MutableList<String>? {
        val yamlConfiguration = getYml(path, fileName)
        return yamlConfiguration.getStringList(key)
    }
    fun removeToList(path: String, fileName: String, key: String, item: String) {
        val playerDataFolder = File(plugin.dataFolder, path)
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs()
        }

        val filePath = File(playerDataFolder, "$fileName.yml")
        val yamlConfiguration = YamlConfiguration.loadConfiguration(filePath)

        // 既存のリストを読み込むか新しいリストを作成
        val currentList = yamlConfiguration.getStringList(key)
        currentList.remove(item)

        // リストを設定
        yamlConfiguration.set(key, currentList)

        try {
            yamlConfiguration.save(filePath)
            println("Item '$item' added to the list in $fileName.yml with key: $key")
        } catch (e: IOException) {
            println("Error while saving data: ${e.message}")
        }
    }
    fun setList(path: String, fileName: String, key: String, item: MutableList<String>) {
        val playerDataFolder = File(plugin.dataFolder, path)
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs()
        }

        val filePath = File(playerDataFolder, "$fileName.yml")
        val yamlConfiguration = YamlConfiguration.loadConfiguration(filePath)

        // リストを設定
        yamlConfiguration.set(key, item)

        try {
            yamlConfiguration.save(filePath)
            println("Item '$item' added to the list in $fileName.yml with key: $key")
        } catch (e: IOException) {
            println("Error while saving data: ${e.message}")
        }
    }
}
