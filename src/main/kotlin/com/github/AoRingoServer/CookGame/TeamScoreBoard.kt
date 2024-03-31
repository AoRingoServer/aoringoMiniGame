package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.CookGame.Cookwares.CookwareManager
import com.github.Ringoame196.Scoreboard
import org.bukkit.block.Block
import org.bukkit.plugin.Plugin

class TeamScoreBoard(private val plugin: Plugin, private val teamName: String) {
    private val scoreboard = Scoreboard(teamName)
    private val underBlockMap = CookwareManager(plugin).underBlockMap
    fun acquisitionTime(block: Block): Int {
        val timeKey = underBlockMap[block.type]?.timeKey ?: return 10
        val value = scoreboard.getValue(timeKey)
        return if (value <= 0) { 10 } else { value }
    }
}
