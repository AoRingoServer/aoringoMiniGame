package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.AoringoPlayer
import com.github.AoRingoServer.Datas.Yml
import com.github.Ringoame196.Scoreboard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.Plugin

class SalesManager(private val plugin: Plugin) {
    fun acquisitionScoreboardName(): String? {
        val config = Yml(plugin).acquisitionYml("", "cookGameConfig")
        return config.getString("rewardScoreboardName")
    }
    fun addition(price: Int, aoringoPlayer: AoringoPlayer) {
        val scoreboardName = acquisitionScoreboardName()
        if (scoreboardName == null) {
            Bukkit.broadcastMessage("${ChatColor.RED}[クックゲーム] スコアボード名が未設定です")
            return
        }
        val scoreboard = Scoreboard(scoreboardName)
        val target = aoringoPlayer.scoreboardTargetName
        scoreboard.add(target, price)
    }
}
