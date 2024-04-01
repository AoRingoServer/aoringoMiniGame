package com.github.AoRingoServer.GUIs

import com.github.AoRingoServer.Datas.Yml
import com.github.AoRingoServer.ItemManager
import com.github.AoRingoServer.Teleporter
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class ServerMenu(private val plugin: Plugin) : GUI {
    private val itemManager = ItemManager()
    private val yml = Yml(plugin)
    val item = itemManager.make(Material.NETHER_STAR, "${ChatColor.YELLOW}サーバーメニュー")
    override val guiName: String = "${ChatColor.DARK_GREEN}サーバーメニュー"
    override fun make(player: Player): Inventory {
        val gui = Bukkit.createInventory(null, 9, guiName)
        addTeleportWorldIcon(gui)
        return gui
    }
    private fun addTeleportWorldIcon(gui: Inventory) {
        val worldList = yml.acquisitionYml("", "World").getList("worldList") ?: return
        val worldInfo = yml.acquisitionYml("", "WorldInfo")
        for (worldName in worldList) {
            val worldDisplayName = worldInfo.getString(worldName.toString()) ?: "未登録"
            val item = itemManager.make(Material.ENDER_PEARL, "${ChatColor.GOLD}$worldName", mutableListOf(worldDisplayName))
            gui.addItem(item)
        }
    }

    override fun clickProcess(item: ItemStack, player: Player, isShift: Boolean) {
        when (item.type) {
            Material.ENDER_PEARL -> {
                val worldName = item.itemMeta?.displayName?.replace("${ChatColor.GOLD}", "") ?: return
                Teleporter(plugin).teleport(player, worldName)
            }
        }
    }
}
