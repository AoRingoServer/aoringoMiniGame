package com.github.AoRingoServer

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ItemManager {
    val cookGameItemIDKey = "cookGameItemID"
    fun make(itemType: Material, displayName: String, lore: MutableList<String>? = null, amount: Int = 1, customModelData: Int? = null): ItemStack {
        val item = ItemStack(itemType, amount)
        val meta = item.itemMeta
        meta?.setDisplayName(displayName)
        meta?.lore = lore
        meta?.setCustomModelData(customModelData)
        item.setItemMeta(meta)
        return item
    }
    fun reducePlayermainitem(player: Player) {
        val item = player.inventory.itemInMainHand.clone()
        val amount = item.amount
        item.amount = amount - 1
        player.setItemInHand(item)
    }
}
