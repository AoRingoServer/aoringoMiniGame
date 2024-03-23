package com.github.AoRingoServer

import org.bukkit.configuration.file.YamlConfiguration

class PluginData {
    object DataManager {
        var foodInfo: YamlConfiguration? = null
        var cookingMethodData: YamlConfiguration? = null
        var finishedProduclist: MutableList<String> = mutableListOf()
    }
}
