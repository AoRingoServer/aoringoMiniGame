package com.github.AoRingoServer.CookGame.Cookwares

import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class Flier(private val plugin: Plugin) : UseItemFrameCookware {
    private val cookwareManager = CookwareManager(plugin)
    override fun cooking(itemFrame: ItemFrame, food: ItemStack) {
        val flySound = Sound.BLOCK_LAVA_POP
        val flyParticle = Particle.LAVA
        cookwareManager.bakeItemFrameCooking(itemFrame, food, "fly", 10, 20, flySound, flyParticle)
    }
}
