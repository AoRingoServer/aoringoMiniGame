package com.github.AoRingoServer.CookGame.Cookwares

import com.github.AoRingoServer.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Batter(private val cookwareManager: CookwareManager) : Cookware {
    private val itemManager = ItemManager()
    private val foodManager = cookwareManager.foodManager
    val batterItem = itemManager.make(Material.PAPER, "${ChatColor.YELLOW}衣", customModelData = 4)
    override val menuItem: ItemStack = itemManager.make(Material.CAULDRON, "${ChatColor.YELLOW}衣", customModelData = 2)
    fun cover(food: ItemStack, player: Player, itemFrame: ItemFrame) {
        val amount = food.amount
        val completionGoodsItem = foodManager.acquisitionCookingCompletionGoodsData(food, "batter") ?: return
        completionGoodsItem.amount = amount
        itemFrame.isVisible = false
        player.playSound(player, Sound.BLOCK_BREWING_STAND_BREW, 1f, 1f)
        player.inventory.setItemInMainHand(completionGoodsItem)
    }
}
