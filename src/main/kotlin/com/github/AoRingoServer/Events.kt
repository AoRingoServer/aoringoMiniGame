package com.github.AoRingoServer

import com.github.AoRingoServer.CookGame.Customer
import com.github.Ringoame196.ResourcePack
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
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
        val player = e.whoClicked
        if (player !is Player) { return }
        val clickItem = e.currentItem ?: return
        val slot = e.slot
        val inventory = e.clickedInventory ?: return
        val entity = inventory.holder
        val customer = Customer(plugin)
        val customerTag = customer.customerTag
        if (entity !is Villager) { return }
        if (clickItem.type == Material.AIR) { return }
        if (!entity.scoreboardTags.contains(customerTag)) { return }
        if (slot != 2) { return }
    }
}
