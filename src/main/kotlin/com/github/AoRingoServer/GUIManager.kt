package com.github.AoRingoServer

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class GUIManager {
    private fun autoGUISize(itemList: MutableList<*>): Int {
        val listSize = itemList.size
        val column = listSize / 9
        val size = (column + 2) * 9
        return if (size <= 54) { size } else { 54 }
    }
    fun makeMultiplePageSupportedGUI(itemList: MutableList<ItemStack>, pageNumber: Int, guiName: String): Inventory {
        val newList = processListSize(itemList, pageNumber)
        val guiSize = autoGUISize(newList)
        val gui = Bukkit.createInventory(null, guiSize, guiName)
        for (item in newList) {
            if (gui.contains(item)) { continue }
            gui.addItem(item)
        }
        selectButton(gui)
        return gui
    }
    private fun processListSize(itemIDList: MutableList<ItemStack>, pageNumber: Int): MutableList<ItemStack> {
        val maxSize = 45
        val startNumber = (pageNumber - 1) * maxSize
        if (itemIDList.size < startNumber) {
            return mutableListOf()
        }
        val lastNumber = if (itemIDList.size >= (startNumber + maxSize)) {
            startNumber + maxSize
        } else {
            itemIDList.size
        }
        return itemIDList.subList(startNumber, lastNumber)
    }
    private fun selectButton(gui: Inventory) {
        val itemManager = ItemManager()
        val guiNumber = gui.size - 9
        for (i in guiNumber..guiNumber + 8) {
            val number = i - guiNumber + 1
            val button = itemManager.make(Material.RED_STAINED_GLASS_PANE, "${ChatColor.GOLD}${number}番目")
            gui.setItem(i, button)
        }
    }
    fun acquisitionSelectButton(button: ItemStack): Int? {
        val itemName = button.itemMeta?.displayName
        return try {
            itemName?.replace("${ChatColor.GOLD}", "")?.replace("番目", "")?.toInt() ?: 1
        } catch (e: NumberFormatException) {
            null
        }
    }
}
