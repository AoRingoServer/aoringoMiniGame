package com.github.AoRingoServer.CookGame.Cookwares

import com.github.AoRingoServer.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack

class Flier(private val cookwareManager: CookwareManager) : UseItemFrameCookware, Cookware {
    private val itemManager = ItemManager()
    override val menuItem: ItemStack = itemManager.make(Material.CAULDRON, "${ChatColor.YELLOW}揚げる", customModelData = 1)
    override val timeKey: String = "flierTime"
    override fun cooking(itemFrame: ItemFrame, containItem: ItemStack, cookTime: Int) {
        val flySound = Sound.BLOCK_LAVA_POP
        val flyParticle = Particle.LAVA
        cookwareManager.bakeItemFrameCooking(itemFrame, containItem, "fly", cookTime, flySound, flyParticle)
    }
}
