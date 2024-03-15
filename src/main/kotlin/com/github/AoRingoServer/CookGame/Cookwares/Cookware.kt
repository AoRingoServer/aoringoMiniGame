package com.github.AoRingoServer.CookGame.Cookwares

import com.github.AoRingoServer.CookGame.FoodManager
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

class Cookware(private val plugin: Plugin) {
    private val foodManager = FoodManager(plugin)
    fun bakeItemFrameCooking(itemFrame: ItemFrame, food: ItemStack, player: Player, cookingMethod: String, completionTime: Int, overTime: Int, bakeSound: Sound) {
        val completionGoodsItem = foodManager.acquisitionCookingCompletionGoodsData(food, cookingMethod) ?: return
        val completionSound = Sound.BLOCK_ANVIL_USE
        val burnedSound = Sound.BLOCK_LAVA_EXTINGUISH
        itemFrame.isVisible = false
        var time = 0
        object : BukkitRunnable() {
            override fun run() {
                val itemFrameItem = itemFrame.item
                player.playSound(player, bakeSound, 1f, 1f)
                time ++
                if (itemFrameItem != food && itemFrameItem != completionGoodsItem) {
                    this.cancel()
                    itemFrame.isVisible = true
                    return
                }
                when (time) {
                    completionTime -> changeItemFrameItem(itemFrame, completionGoodsItem, completionSound, player)
                    overTime -> {
                        this.cancel()
                        itemFrame.isVisible = true
                        val airItem = ItemStack(Material.AIR)
                        changeItemFrameItem(itemFrame, airItem, burnedSound, player)
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L) // 1秒間隔 (20 ticks) でタスクを実行
    }
    private fun changeItemFrameItem(itemFrame: ItemFrame, setItem: ItemStack, installationSound: Sound, player: Player) {
        itemFrame.setItem(setItem)
        player.playSound(player, installationSound, 1f, 1f)
    }
}
