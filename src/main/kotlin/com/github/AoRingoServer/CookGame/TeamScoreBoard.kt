package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.CookGame.Cookwares.Flier
import com.github.AoRingoServer.CookGame.Cookwares.Furnace
import com.github.AoRingoServer.CookGame.Cookwares.Pot
import com.github.AoRingoServer.CookGame.Cookwares.UseItemFrameCookware
import com.github.Ringoame196.Scoreboard
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.plugin.Plugin

class TeamScoreBoard(private val plugin: Plugin, private val teamName: String) {
    private val scoreboard = Scoreboard(teamName)
    private val underBlockMap = mapOf<Material, UseItemFrameCookware>(
        Material.LAVA_CAULDRON to Flier(plugin),
        Material.SMOKER to Furnace(plugin),
        Material.WATER_CAULDRON to Pot(plugin)
    )
    fun acquisitionTime(block: Block): Int {
        val timeKey = underBlockMap[block.type]?.timeKey ?: return 10
        val value = scoreboard.getValue(timeKey)
        return if (value <= 0) { 10 } else { value }
    }
}
