package com.github.AoRingoServer.CookGame.Cookwares

import com.github.AoRingoServer.CookGame.CoalescenceRecipeData
import com.github.AoRingoServer.CookGame.FoodManager
import com.github.AoRingoServer.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class Coalescence(private val plugin: Plugin) : Cookware {
    private val itemManager = ItemManager()
    private val foodManager = FoodManager(plugin)
    override val menuItem: ItemStack = itemManager.make(Material.STICK, "${ChatColor.YELLOW}合体", customModelData = 2)
    fun cooking(itemFrame: ItemFrame, player: Player) {
        val additionalFood = player.inventory.itemInMainHand
        val foundationFood = itemFrame.item
        if (additionalFood.type == Material.AIR) { return }
        if (foundationFood.type == Material.AIR) { return }
        val coalescenceRecipeData = CoalescenceRecipeData(additionalFood, foundationFood)
        val completionGoodsItem = foodManager.acquisitionCookingCompletionGoodsData(coalescenceRecipeData, "coalescence") ?: return
        itemFrame.setItem(completionGoodsItem)
        itemManager.reducePlayermainitem(player)
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }
}
