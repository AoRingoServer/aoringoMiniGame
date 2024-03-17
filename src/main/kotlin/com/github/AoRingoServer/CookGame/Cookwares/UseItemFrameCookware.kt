package com.github.AoRingoServer.CookGame.Cookwares

import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface UseItemFrameCookware {
    fun cooking(itemFrame: ItemFrame, food: ItemStack)
}