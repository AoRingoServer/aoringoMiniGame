package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.AoringoPlayer
import com.github.AoRingoServer.CookGame.DataClasses.CookGameItemInfo
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
    fun sellingPriceCalculating(price: Int): Int {
        val percent = 1.3
        return (price * percent).toInt()
    }
    fun addition(cookgameItemInfo: CookGameItemInfo, player: Player) {
        val price = cookgameItemInfo.price
        val sales = sellingPriceCalculating(price)
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
    fun reduce(player: Player, price: Int) {
        val aoringoPlayer = AoringoPlayer(player)
        val scoreboardName = acquisitionScoreboardName()
        if (scoreboardName == null) {
            Bukkit.broadcastMessage("${ChatColor.RED}[クックゲーム] スコアボード名が未設定です")
            return
        }
        val scoreboard = Scoreboard(scoreboardName)
        val target = aoringoPlayer.scoreboardTargetName
        return scoreboard.reduce(target, price)
    }
}
