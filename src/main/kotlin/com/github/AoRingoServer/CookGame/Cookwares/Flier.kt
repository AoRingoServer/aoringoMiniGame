package com.github.AoRingoServer.CookGame.Cookwares

import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class Flier(private val plugin: Plugin):UseItemFrameCookware {
    private val cookware = Cookware(plugin)
    override fun cooking(itemFrame: ItemFrame,food: ItemStack) {
        val flySound = Sound.BLOCK_LAVA_POP
        val flyParticle = Particle.LAVA
        cookware.bakeItemFrameCooking(itemFrame, food, "fly", 10, 20, flySound, flyParticle)
    }
}
