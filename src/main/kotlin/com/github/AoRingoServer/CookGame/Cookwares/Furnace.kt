package com.github.AoRingoServer.CookGame.Cookwares

import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.Smoker
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class Furnace(private val plugin: Plugin) {
    private val cookware = Cookware(plugin)
    fun bake(itemFrame: ItemFrame, player: Player, food: ItemStack) {
        val bakeSound = Sound.BLOCK_FIRE_AMBIENT
        val bakeParticle = Particle.SMOKE_LARGE
        val smoker = itemFrame.location.clone().add(0.0, -1.0, 0.0).block.state as? Smoker ?: return
        smoker.burnTime = 300
        smoker.update()
        cookware.bakeItemFrameCooking(itemFrame, food, "bake", 10, 20, bakeSound, bakeParticle)
    }
}
