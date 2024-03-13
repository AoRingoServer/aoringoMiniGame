package com.github.AoRingoServer

import com.github.AoRingoServer.CookGame.CustomerManager
import com.github.Ringoame196.ResourcePack
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.Plugin

class Events(private val plugin: Plugin) : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        val aoringoPlayer = AoringoPlayer(player)
        ResourcePack(plugin).adaptation(player)
        aoringoPlayer.setPrefix()
    }
    @EventHandler
    fun onVillagePeopleAndTrading(e: InventoryClickEvent) {
        val obtainedItem = e.currentItem ?: return
        val itemName = obtainedItem.itemMeta?.displayName ?: return
        val slot = e.slot
        val completionGoodsSlot = 2
        val inventory = e.clickedInventory ?: return
        val player = e.whoClicked as? Player ?: return
        val aoringoPlayer = AoringoPlayer(player)
        val customerManager = CustomerManager(plugin)
        val villager = customerManager.acquisitionCustomer(inventory) ?: return
        val recipeCount = villager.recipeCount
        val customerTag = customerManager.customerTag
        if (obtainedItem.type == Material.AIR) { return }
        if (!villager.scoreboardTags.contains(customerTag)) { return }
        if (slot != completionGoodsSlot) { return }

        val message = "${ChatColor.GOLD}村人のインベントリを閉じ 取引内容を更新してください"
        player.sendMessage(message)
        if (obtainedItem == customerManager.customorRecipManager.skipItem) {
            e.isCancelled = true
            customerManager.customorRecipManager.reduceMaterial(inventory)
            player.playSound(player, Sound.BLOCK_BELL_USE, 1f, 1f)
            customerManager.setCustomorInfo(villager, "skip")
        } else if (obtainedItem.type == Material.PAPER && itemName == customerManager.customorRecipManager.receiptName) {
            customerManager.takeOrder(villager, obtainedItem, aoringoPlayer, recipeCount)
            player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
            customerManager.setCustomorInfo(villager, "next")
        } else if (obtainedItem.type == Material.MOJANG_BANNER_PATTERN) {
            player.playSound(player, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f)
        }
    }
    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        val customerManager = CustomerManager(plugin)
        val inventory = e.inventory
        val villager = customerManager.acquisitionCustomer(inventory) ?: return
        when (customerManager.acquisitionCustomorInfo(villager)) {
            "skip" -> customerManager.skipTrade(villager)
            "next" -> customerManager.newTradingPreparation(villager)
        }
    }
}
