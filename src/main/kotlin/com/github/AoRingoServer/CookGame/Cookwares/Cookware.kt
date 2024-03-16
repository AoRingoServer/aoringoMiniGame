package com.github.AoRingoServer.CookGame.Cookwares

import com.github.AoRingoServer.CookGame.FoodManager
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

class Cookware(private val plugin: Plugin) {
    private val foodManager = FoodManager(plugin)
    fun bakeItemFrameCooking(itemFrame: ItemFrame, food: ItemStack, cookingMethod: String, completionTime: Int, overTime: Int, sound: Sound, particle: Particle) {
        val completionGoodsItem = foodManager.acquisitionCookingCompletionGoodsData(food, cookingMethod) ?: return
        val completionSound = Sound.BLOCK_ANVIL_USE
        val burnedSound = Sound.BLOCK_LAVA_EXTINGUISH
        val particleLocation = itemFrame.location.clone()
        val overParticle = Particle.EXPLOSION_LARGE
        itemFrame.isVisible = false
        var time = 0
        object : BukkitRunnable() {
            override fun run() {
                val itemFrameItem = itemFrame.item
                val putParticle = if (time <= completionTime) {
                    particle
                } else {
                    overParticle
                }
                itemFrame.world.playSound(itemFrame.location, sound, 1f, 1f)
                itemFrame.world.spawnParticle(putParticle, particleLocation, 10, 0.5, 0.5, 0.5, 0.1)
                time ++
                if (itemFrameItem != food && itemFrameItem != completionGoodsItem) {
                    this.cancel()
                    return
                }
                when (time) {
                    completionTime -> changeItemFrameItem(itemFrame, completionGoodsItem, completionSound)
                    overTime -> {
                        this.cancel()
                        val airItem = ItemStack(Material.AIR)
                        changeItemFrameItem(itemFrame, airItem, burnedSound)
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L) // 1秒間隔 (20 ticks) でタスクを実行
    }
    private fun changeItemFrameItem(itemFrame: ItemFrame, setItem: ItemStack, installationSound: Sound) {
        itemFrame.setItem(setItem)
        itemFrame.world.playSound(itemFrame.location, installationSound, 1f, 1f)
    }
}
