package com.github.AoRingoServer.CookGame.Cookwares

import com.github.AoRingoServer.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class Pot(private val plugin: Plugin) : UseItemFrameCookware, Cookware {
    private val cookwareManager = CookwareManager(plugin)
    private val itemManager = ItemManager()
    override val menuItem: ItemStack = itemManager.make(Material.CAULDRON, "${ChatColor.GOLD}茹でる", customModelData = 3)
    override fun cooking(itemFrame: ItemFrame, food: ItemStack) {
        val boilSound = Sound.BLOCK_STONE_PLACE
        val boilParticle = Particle.SPIT
        cookwareManager.bakeItemFrameCooking(itemFrame, food, "boil", 10, 20, boilSound, boilParticle)
    }
}
