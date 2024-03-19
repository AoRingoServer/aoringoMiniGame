package com.github.AoRingoServer.CookGame

import com.github.AoRingoServer.AoringoPlayer
import com.github.AoRingoServer.GUIs
import com.github.AoRingoServer.ItemManager
import com.github.Ringoame196.Scoreboard
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class Register(plugin: Plugin) : GUIs {
    override val guiName: String = "${ChatColor.BLUE}レジ"
    private val itemManager = ItemManager()
    private val scoreboardName = SalesManager(plugin).acquisitionScoreboardName() ?: ""
    private val scoreboard = Scoreboard(scoreboardName)
    private val money = itemManager.make(Material.PAPER, "${ChatColor.YELLOW}お金", customModelData = 3)
    private val amount = 100
    override fun make(player: Player): Inventory {
        val aoringoPlayer = AoringoPlayer(player)
        val value = acquisitionSales(aoringoPlayer)
        val gui = Bukkit.createInventory(null, 9, guiName)
        gui.setItem(3, itemManager.make(Material.CHEST, "${ChatColor.GREEN}売り上げ", lore = mutableListOf("${value}円")))
        gui.setItem(5, money)
        return gui
    }

    override fun clickProcess(item: ItemStack, player: Player, isShift: Boolean) {
        if (item != money) { return }
        val aoringoPlayer = AoringoPlayer(player)
        passMoney(aoringoPlayer)
    }
    private fun passMoney(aoringoPlayer: AoringoPlayer) {
        val player = aoringoPlayer.player
        val value = acquisitionSales(aoringoPlayer)
        player.closeInventory()
        if (value < amount) {
            aoringoPlayer.sendErrorMessage("${ChatColor.RED}お金が足りません")
            return
        }
        player.inventory.addItem(money)
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f)
        scoreboard.reduce(aoringoPlayer.scoreboardTargetName, amount)
    }
    private fun acquisitionSales(aoringoPlayer: AoringoPlayer): Int {
        val targetName = aoringoPlayer.scoreboardTargetName
        return scoreboard.getValue(targetName)
    }
}
