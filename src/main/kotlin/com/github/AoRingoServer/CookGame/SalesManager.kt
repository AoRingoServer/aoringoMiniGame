package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.AoringoPlayer
import com.github.AoRingoServer.CookGame.DataClasses.FoodInfo
import com.github.AoRingoServer.Datas.Yml
import com.github.Ringoame196.Scoreboard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class SalesManager(private val plugin: Plugin) {
    private fun acquisitionScoreboardName(): String? {
        val config = Yml(plugin).acquisitionYml("", "cookGameConfig")
        return config.getString("rewardScoreboardName")
    }
    fun addition(foodInfo: FoodInfo, player: Player) {
        val price = foodInfo.price
        val percent = 1.3
        val sales = (price * percent).toInt()
        val aoringoPlayer = AoringoPlayer(player)
        val scoreboardName = acquisitionScoreboardName()
        if (scoreboardName == null) {
            Bukkit.broadcastMessage("${ChatColor.RED}[クックゲーム] スコアボード名が未設定です")
            return
        }
        val scoreboard = Scoreboard(scoreboardName)
        val target = aoringoPlayer.scoreboardTargetName
        scoreboard.add(target, sales)
    }
    fun acquisitionPossessionGold(player: Player): Int {
        val aoringoPlayer = AoringoPlayer(player)
        val scoreboardName = acquisitionScoreboardName()
        if (scoreboardName == null) {
            Bukkit.broadcastMessage("${ChatColor.RED}[クックゲーム] スコアボード名が未設定です")
            return 0
        }
        val scoreboard = Scoreboard(scoreboardName)
        val target = aoringoPlayer.scoreboardTargetName
        return scoreboard.getValue(target)
    }
    fun reduce(player: Player, foodInfo: FoodInfo) {
        val aoringoPlayer = AoringoPlayer(player)
        val scoreboardName = acquisitionScoreboardName()
        if (scoreboardName == null) {
            Bukkit.broadcastMessage("${ChatColor.RED}[クックゲーム] スコアボード名が未設定です")
            return
        }
        val scoreboard = Scoreboard(scoreboardName)
        val target = aoringoPlayer.scoreboardTargetName
        val price = foodInfo.price
        return scoreboard.reduce(target, price)
    }
}
