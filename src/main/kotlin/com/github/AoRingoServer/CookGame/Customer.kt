package com.github.AoRingoServer.CookGame

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Villager

class Customer {
    val name = "${ChatColor.YELLOW}お客様"
    fun summon(location: Location) {
        val world = location.world
        val villager: Villager = world!!.spawn(location, org.bukkit.entity.Villager::class.java)
        villager.customName = name
        villager.setAI(false)
    }
}
