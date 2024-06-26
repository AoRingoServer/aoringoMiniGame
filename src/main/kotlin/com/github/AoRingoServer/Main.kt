package com.github.AoRingoServer

import com.github.AoRingoServer.CookGame.Commands.CookGameCommand
import com.github.AoRingoServer.CookGame.Commands.CustomerCommand
import com.github.AoRingoServer.CookGame.FoodManager
import com.github.AoRingoServer.Datas.Yml
import com.github.AoRingoServer.PlenaryCommands.AoringoCommand
import com.github.AoRingoServer.PlenaryCommands.LobbyCommand
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onEnable() {
        super.onEnable()
        val yml = Yml(this)
        yml.makePluginFolder()
        saveDefaultConfig()
        getCommand("lobby")!!.setExecutor(LobbyCommand(this))
        getCommand("aoringo")!!.setExecutor(AoringoCommand(this))
        getCommand("cookgame")!!.setExecutor(CookGameCommand(this))
        getCommand("customer")!!.setExecutor(CustomerCommand(this))
        server.pluginManager.registerEvents(Events(this), this)
        saveResource("FoodInfo.yml", true)
        saveResource("FinishedProductList.yml", true)
        saveResource("CookingMethodData.yml", true)
        saveResource("shopCommercialProductList.yml", true)
        saveResource("ItemInfo.yml", true)
        saveResource("cookGameConfig.yml", false)
        saveResource("World.yml", false)
        saveResource("WorldInfo.yml", true)
        PluginData.DataManager.foodInfo = yml.acquisitionYml("", "FoodInfo")
        PluginData.DataManager.cookingMethodData = yml.acquisitionYml("", "CookingMethodData")
        PluginData.DataManager.finishedProduclist = FoodManager(this).setFinishedProductList()
    }
}
