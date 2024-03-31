package com.github.AoRingoServer.CookGame.Cookwares

import com.github.AoRingoServer.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.Smoker
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack

class Furnace(private val cookwareManager: CookwareManager) : UseItemFrameCookware, Cookware {
    private val itemManager = ItemManager()
    override val menuItem: ItemStack = itemManager.make(Material.SMOKER, "${ChatColor.YELLOW}焼く")
    override val timeKey: String = "furnaceTime"
    override fun cooking(itemFrame: ItemFrame, food: ItemStack, cookTime: Int) {
        val bakeSound = Sound.BLOCK_FIRE_AMBIENT
        val bakeParticle = Particle.SMOKE_LARGE
        val smoker = itemFrame.location.clone().add(0.0, -1.0, 0.0).block.state as? Smoker ?: return
        smoker.burnTime = 300
        smoker.update()
        cookwareManager.bakeItemFrameCooking(itemFrame, food, "bake", cookTime, bakeSound, bakeParticle)
    }
}
