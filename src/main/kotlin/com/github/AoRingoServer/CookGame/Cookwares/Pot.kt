package com.github.AoRingoServer.CookGame.Cookwares

import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class Pot(private val plugin: Plugin) {
    private val cookware = Cookware(plugin)
    fun boil(itemFrame: ItemFrame, food: ItemStack) {
        val boilSound = Sound.BLOCK_STONE_PLACE
        val boilParticle = Particle.SPIT
        cookware.bakeItemFrameCooking(itemFrame, food, "boil", 10, 20, boilSound, boilParticle)
    }
}
