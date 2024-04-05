package com.github.AoRingoServer.CookGame.Cookwares

import com.github.AoRingoServer.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Dispenser
import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class Mixer(private val cookwareManager: CookwareManager) : Cookware {
    private val itemManager = ItemManager()
    private val foodManager = cookwareManager.foodManager
    override val menuItem: ItemStack = itemManager.make(Material.STICK, "${ChatColor.YELLOW}合体", customModelData = 2)
    fun mix(itemFrame: ItemFrame) {
        val location = itemFrame.location.clone()
        val item = itemFrame.item
        val block = itemFrame.location.clone().add(0.0, -1.0, 0.0).block.state as? Dispenser ?: return
        val inventory = block.inventory.contents.clone()
        val foods = mutableListOf<ItemStack>()
        if (item.itemMeta?.displayName != "${ChatColor.YELLOW}混ぜハンドル") { return }
        location.world?.playSound(location, Sound.BLOCK_BREWING_STAND_BREW, 1f, 1f)
        itemFrame.isVisible = false
        if (!drawing()) { return }
        for (i in inventory.indices) {
            val food = inventory[i] ?: continue
            foods.add(food)
            val reduceItem = food.clone()
            reduceItem.amount = reduceItem.amount - 1
            inventory[i] = reduceItem
        }
        val completionGoodsItem = foodManager.acquisitionCookingCompletionGoodsData(foods, "coalescence") ?: return
        block.inventory.contents = inventory
        dropFinishedProduct(itemFrame.location, completionGoodsItem)
        location.world?.playSound(location, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }
    private fun drawing(): Boolean {
        val probability = 10
        return Random.nextInt(0, probability) == 0
    }
    private fun dropFinishedProduct(location: Location, completionGoodsItem: ItemStack) {
        val world = location.world ?: return
        world.dropItem(location.clone().add(0.0, 0.5, 0.0), completionGoodsItem)
    }
}
