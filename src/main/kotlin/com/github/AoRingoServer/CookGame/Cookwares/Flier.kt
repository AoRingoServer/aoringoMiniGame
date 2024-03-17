package com.github.AoRingoServer.CookGame.Cookwares

import com.github.AoRingoServer.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class Flier(private val plugin: Plugin) : UseItemFrameCookware, Cookware {
    private val cookwareManager = CookwareManager(plugin)
    private val itemManager = ItemManager()
    override val menuItem: ItemStack = itemManager.make(Material.CAULDRON, "${ChatColor.YELLOW}揚げる", customModelData = 1)
    override fun cooking(itemFrame: ItemFrame, food: ItemStack) {
        val flySound = Sound.BLOCK_LAVA_POP
        val flyParticle = Particle.LAVA
        cookwareManager.bakeItemFrameCooking(itemFrame, food, "fly", 10, 20, flySound, flyParticle)
    }
}
