package com.github.AoRingoServer.CookGame.Cookwares

import com.github.AoRingoServer.CookGame.FoodManager
import com.github.AoRingoServer.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class ChoppingBoard(private val plugin: Plugin) {
    private val foodManager = FoodManager(plugin)
    val knifeItem = ItemManager().make(Material.IRON_SWORD, "${ChatColor.GOLD}包丁", customModelData = 1)
    fun process(itemFrame: ItemFrame, player: Player) {
        val food = itemFrame.item
        val completionGoodsItem = foodManager.acquisitionCookingCompletionGoodsData(food, "cut")
        if (completionGoodsItem == null) {
            // 切ったあとのデータが見つからなかったとき
            player.playSound(player, Sound.ITEM_SHIELD_BREAK, 1f, 1f)
        } else { // 切れたとき
            player.playSound(player, Sound.ENTITY_SHEEP_SHEAR, 1f, 1f)
            itemFrame.setItem(completionGoodsItem)
        }
    }
}
