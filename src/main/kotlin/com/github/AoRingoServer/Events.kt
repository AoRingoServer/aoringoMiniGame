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
        val tradingItem = e.currentItem ?: return
        val itemName = tradingItem.itemMeta?.displayName ?: return
        val slot = e.slot
        val completionGoodsSlot = 2
        val inventory = e.clickedInventory ?: return
        val player = e.whoClicked as? Player ?: return
        val customerManager = CustomerManager(plugin)
        val villager = customerManager.acquisitionCustomer(inventory) ?: return
        val customerTag = customerManager.customerTag
        if (tradingItem.type == Material.AIR) { return }
        if (!villager.scoreboardTags.contains(customerTag)) { return }
        if (slot != completionGoodsSlot) { return }

        val message = "${ChatColor.GOLD}村人のインベントリを閉じ 取引内容を更新してください"
        player.sendMessage(message)
        if (tradingItem == customerManager.customorRecipManager.skipItem) {
            e.isCancelled = true
            customerManager.reduceMaterial(inventory)
            customerManager.skipTrade(villager)
            player.playSound(player, Sound.BLOCK_BELL_USE, 1f, 1f)
        } else if (tradingItem.type == Material.PAPER && itemName == customerManager.customorRecipManager.receiptName) {
            customerManager.takeOrder(villager)
            player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
        }
    }
}
