package com.github.AoRingoServer

import com.github.AoRingoServer.CookGame.Commands.CookGameCommand
import com.github.AoRingoServer.Datas.Yml
import com.github.AoRingoServer.PlenaryCommands.AoringoCommand
import com.github.AoRingoServer.PlenaryCommands.LobbyCommand
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onEnable() {
        super.onEnable()
        Yml(this).makePluginFolder()
        saveDefaultConfig()
        getCommand("lobby")!!.setExecutor(LobbyCommand())
        getCommand("aoringo")!!.setExecutor(AoringoCommand(this))
        getCommand("cookgame")!!.setExecutor(CookGameCommand(this))
        server.pluginManager.registerEvents(Events(this), this)
        saveResource("FoodInfo.yml", true)
        saveResource("FinishedProductList.yml", true)
        saveResource("CookingMethodData.yml", true)
        saveResource("cookGameConfig.yml", false)
    }
}
