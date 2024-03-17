package com.github.AoRingoServer.CookGame.Cookwares

import com.github.AoRingoServer.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.Smoker
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class Furnace(private val plugin: Plugin) : UseItemFrameCookware, Cookware {
    private val cookwareManager = CookwareManager(plugin)
    private val itemManager = ItemManager()
    override val menuItem: ItemStack = itemManager.make(Material.SMOKER, "${ChatColor.YELLOW}焼く")

    override fun cooking(itemFrame: ItemFrame, food: ItemStack) {
        val bakeSound = Sound.BLOCK_FIRE_AMBIENT
        val bakeParticle = Particle.SMOKE_LARGE
        val smoker = itemFrame.location.clone().add(0.0, -1.0, 0.0).block.state as? Smoker ?: return
        smoker.burnTime = 300
        smoker.update()
        cookwareManager.bakeItemFrameCooking(itemFrame, food, "bake", 10, 20, bakeSound, bakeParticle)
    }
}
