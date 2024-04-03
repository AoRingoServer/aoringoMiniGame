package com.github.AoRingoServer.CookGame.Cookwares

import org.bukkit.entity.ItemFrame
import org.bukkit.inventory.ItemStack

interface UseItemFrameCookware {
    val timeKey: String
    fun cooking(itemFrame: ItemFrame, containItem: ItemStack, cookTime: Int)
}
