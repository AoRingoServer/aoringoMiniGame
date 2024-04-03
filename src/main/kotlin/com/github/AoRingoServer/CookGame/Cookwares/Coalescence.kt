package com.github.AoRingoServer.CookGame.Cookwares

import com.github.AoRingoServer.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Coalescence(private val cookwareManager: CookwareManager) : Cookware {
    private val itemManager = ItemManager()
    private val foodManager = cookwareManager.foodManager
    override val menuItem: ItemStack = itemManager.make(Material.STICK, "${ChatColor.YELLOW}合体", customModelData = 2)
    fun cooking(itemFrame: ItemFrame, player: Player) {
        val additionalFood = player.inventory.itemInMainHand
        val foundationFood = itemFrame.item
        if (additionalFood.type == Material.AIR || foundationFood.type == Material.AIR) { return }
        val foods = mutableListOf(additionalFood, foundationFood)
        val completionGoodsItem = foodManager.acquisitionCookingCompletionGoodsData(foods, "coalescence") ?: return
        itemFrame.setItem(completionGoodsItem)
        itemManager.reducePlayermainitem(player)
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }
}
