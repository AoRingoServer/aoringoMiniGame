package com.github.AoRingoServer.CookGame.Cookwares

import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class Flier(private val plugin: Plugin) {
    private val cookware = Cookware(plugin)
    fun fry(itemFrame: ItemFrame, player: Player, food: ItemStack) {
        val flySound = Sound.BLOCK_LAVA_POP
        cookware.bakeItemFrameCooking(itemFrame, food, player, 10, 20, flySound)
    }
}
