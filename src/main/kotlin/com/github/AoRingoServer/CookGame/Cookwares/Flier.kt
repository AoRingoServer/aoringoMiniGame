package com.github.AoRingoServer.CookGame.Cookwares

import com.github.AoRingoServer.CookGame.FoodManager
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

class Flier(private val plugin: Plugin) {
    private val foodManager = FoodManager(plugin)
    fun fry(itemFrame: ItemFrame, player: Player, food: ItemStack) {
        val completionGoodsItem = foodManager.acquisitionCookingCompletionGoodsData(food, "fly") ?: return
        val justTime = 10
        val overTime = 20
        var time = 0
        object : BukkitRunnable() {
            override fun run() {
                val itemFrameItem = itemFrame.item
                player.playSound(player, Sound.BLOCK_LAVA_POP, 1f, 1f)
                time ++
                if (itemFrameItem != food && itemFrameItem != completionGoodsItem) {
                    this.cancel()
                    return
                }
                when(time){
                    justTime -> {
                        itemFrame.setItem(completionGoodsItem)
                        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
                    }
                    overTime -> {
                        this.cancel()
                        itemFrame.setItem(ItemStack(Material.AIR))
                        player.playSound(player, Sound.BLOCK_LAVA_EXTINGUISH, 1f, 1f)
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L) // 1秒間隔 (20 ticks) でタスクを実行
    }
}
