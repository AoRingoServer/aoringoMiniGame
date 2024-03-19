package com.github.AoRingoServer

import com.github.AoRingoServer.CookGame.Cookwares.Batter
import com.github.AoRingoServer.CookGame.Cookwares.ChoppingBoard
import com.github.AoRingoServer.CookGame.Cookwares.CookwareManager
import com.github.AoRingoServer.CookGame.Cookwares.Flier
import com.github.AoRingoServer.CookGame.Cookwares.Furnace
import com.github.AoRingoServer.CookGame.Cookwares.Pot
import com.github.AoRingoServer.CookGame.Customer.CustomerManager
import com.github.AoRingoServer.CookGame.FoodMenu
import com.github.AoRingoServer.CookGame.Register
import com.github.Ringoame196.ResourcePack
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
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
        } else if (obtainedItem.type == Material.BOWL && itemName == customerManager.customorRecipManager.dirtyTrayName) {
            customerManager.takeOrder(villager, obtainedItem, aoringoPlayer, recipeCount)
            player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
            customerManager.setCustomorInfo(villager, "next")
        } else if (obtainedItem.type == Material.MOJANG_BANNER_PATTERN) {
            player.playSound(player, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1f, 1f)
        }
    }
    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player
        val makeGUIs = mapOf(
            "レシピ" to FoodMenu(plugin)
        )
        val clickNGBlocks = mutableListOf(
            Material.SMOKER,
            Material.LAVA_CAULDRON,
            Material.WATER_CAULDRON,
            Material.CAULDRON
        )
        val blockMap = mapOf(
            Material.ENCHANTING_TABLE to { player.openInventory(Register(plugin).make(player)) }
        )
        val item = e.item
        val block = e.clickedBlock
        val action = e.action
        val itemName = item?.itemMeta?.displayName
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) { return }
        if (clickNGBlocks.contains(block?.type) && player.gameMode != GameMode.CREATIVE) {
            e.isCancelled = true
        }
        if (blockMap.contains(block?.type)) {
            e.isCancelled = true
            blockMap[block?.type]?.invoke()
        } else if (makeGUIs.keys.contains(itemName)) {
            e.isCancelled = true
            val gui = makeGUIs[item?.itemMeta?.displayName]?.make(player) ?: return
            player.openInventory(gui)
        }
    }
    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val makeGUIs = mapOf(
            FoodMenu(plugin).let { foodMenu -> foodMenu.guiName to foodMenu },
            Register(plugin).let { register -> register.guiName to register }
        )
        val player = e.whoClicked as? Player ?: return
        val gui = e.view
        val isShift = e.isShiftClick
        val guiName = gui.title
        val item = e.currentItem ?: return
        if (makeGUIs.keys.contains(guiName)) {
            e.isCancelled = true
            player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
            makeGUIs[guiName]?.clickProcess(item, player, isShift)
        }
    }
    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        val customerManager = CustomerManager(plugin)
        val inventory = e.inventory
        val player = e.player as Player
        val villager = customerManager.acquisitionCustomer(inventory) ?: return
        val customInfo = customerManager.acquisitionCustomorInfo(villager)
        val aoringoPlayer = AoringoPlayer(player)
        val recipeCount = villager.recipeCount
        val updateTrading = mapOf(
            "skip" to { customerManager.skipTrade(villager) },
            "next" to { customerManager.newTradingPreparation(villager) }
        )
        if (customerManager.satisfactionLevel(recipeCount, villager, aoringoPlayer)) {
            return
        }
        updateTrading[customInfo]?.invoke() ?: return
        customerManager.setCustomorInfo(villager, null)
    }
    @EventHandler
    fun onPlayerInteractItemFrame(e: PlayerInteractEntityEvent) {
        val player = e.player
        val isSneak = player.isSneaking
        val itemFrame = e.rightClicked as? ItemFrame ?: return
        val itemFrameItem = itemFrame.item
        val item = player.inventory.itemInMainHand.clone()
        val underBlock = itemFrame.location.clone().add(0.0, -1.0, 0.0).block
        val choppingBoard = ChoppingBoard(plugin)
        val batter = Batter(plugin)
        val sneakGuidanceMessage = "${ChatColor.GOLD}スニークしながらクリックで 使用可能"
        val playerHasItemUseMap = mapOf(
            choppingBoard.knifeItem to { choppingBoard.process(itemFrame, player) },
            ItemStack(Material.SPONGE) to { CookwareManager(plugin).cleanTray(itemFrame, player, underBlock) }
        )
        val itemFrameItemUseMap = mapOf(
            batter.batterItem to { batter.cover(item, player, itemFrame) }
        )
        val underBlockMap = mapOf(
            Material.LAVA_CAULDRON to Flier(plugin),
            Material.SMOKER to Furnace(plugin),
            Material.WATER_CAULDRON to Pot(plugin)
        )
        if (playerHasItemUseMap.keys.contains(item)) {
            if (!isSneak) {
                player.sendMessage(sneakGuidanceMessage)
                return
            }
            e.isCancelled = true
            playerHasItemUseMap[item]?.invoke()
        } else if (itemFrameItemUseMap.keys.contains(itemFrameItem)) {
            if (!isSneak) {
                player.sendMessage(sneakGuidanceMessage)
                return
            }
            e.isCancelled = true
            itemFrameItemUseMap[itemFrameItem]?.invoke()
        } else if (underBlockMap.keys.contains(underBlock.type)) {
            if (itemFrame.item.type == Material.AIR) {
                underBlockMap[underBlock.type]?.cooking(itemFrame, item)
            } else {
                e.isCancelled = true
            }
        }
    }
    @EventHandler
    fun onHangingBreakByEntity(e: HangingBreakByEntityEvent) {
        val player = e.remover as? Player ?: return
        if (e.entity !is ItemFrame) return
        if (player.gameMode == GameMode.CREATIVE) { return }
        e.isCancelled = true
    }
}
