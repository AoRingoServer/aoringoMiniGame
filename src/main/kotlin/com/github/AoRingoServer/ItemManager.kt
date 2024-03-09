package com.github.AoRingoServer

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemManager {
    fun make(itemType:Material,displayName:String,lore:MutableList<String>? = null,amount:Int = 1):ItemStack{
        val item = ItemStack(itemType,amount)
        val meta = item.itemMeta
        meta?.setDisplayName(displayName)
        meta?.lore = lore
        item.setItemMeta(meta)
        return item
    }
}