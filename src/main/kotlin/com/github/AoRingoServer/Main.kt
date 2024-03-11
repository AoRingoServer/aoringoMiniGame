package com.github.AoRingoServer

import com.github.AoRingoServer.Commands.AoringoCommand
import com.github.AoRingoServer.Commands.LobbyCommand
import com.github.AoRingoServer.CookGame.CookGameCommand
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onEnable() {
        super.onEnable()
        makePluginFolder()
        saveDefaultConfig()
        getCommand("lobby")!!.setExecutor(LobbyCommand())
        getCommand("aoringo")!!.setExecutor(AoringoCommand(this))
        getCommand("cookgame")!!.setExecutor(CookGameCommand(this))
        server.pluginManager.registerEvents(Events(this), this)
        saveResource("FoodInfo.yml", true)
        saveResource("FinishedProductList.yml", true)
        saveResource("cookGameConfig.yml", false)
    }
    private fun makePluginFolder() {
        val dataFolder = this.dataFolder
        if (dataFolder.exists()) { return }
        dataFolder.mkdirs()
    }
}
