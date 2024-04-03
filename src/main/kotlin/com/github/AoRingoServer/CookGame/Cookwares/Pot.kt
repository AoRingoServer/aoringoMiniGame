package com.github.AoRingoServer.CookGame.Cookwares

import com.github.AoRingoServer.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack

class Pot(private val cookwareManager: CookwareManager) : UseItemFrameCookware, Cookware {
    private val itemManager = ItemManager()
    override val timeKey: String = "potTime"
    override val menuItem: ItemStack = itemManager.make(Material.CAULDRON, "${ChatColor.GOLD}茹でる", customModelData = 3)
    override fun cooking(itemFrame: ItemFrame, containItem: ItemStack, cookTime: Int) {
        val boilSound = Sound.BLOCK_STONE_PLACE
        val boilParticle = Particle.SPIT
        cookwareManager.bakeItemFrameCooking(itemFrame, containItem, "boil", cookTime, boilSound, boilParticle)
    }
}
