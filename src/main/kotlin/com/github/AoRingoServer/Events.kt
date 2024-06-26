package com.github.AoRingoServer

import com.github.AoRingoServer.CookGame.Cookwares.Batter
import com.github.AoRingoServer.CookGame.Cookwares.ChoppingBoard
import com.github.AoRingoServer.CookGame.Cookwares.Coalescence
import com.github.AoRingoServer.CookGame.Cookwares.CookwareManager
import com.github.AoRingoServer.CookGame.Cookwares.Mixer
import com.github.AoRingoServer.CookGame.Customer.CustomerManager
import com.github.AoRingoServer.CookGame.FoodMenu
import com.github.AoRingoServer.CookGame.Shop
import com.github.AoRingoServer.CookGame.TeamScoreBoard
import com.github.AoRingoServer.GUIs.ServerMenu
import com.github.Ringoame196.ResourcePack
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerTakeLecternBookEvent
import org.bukkit.event.player.PlayerToggleSneakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class Events(private val plugin: Plugin) : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        val aoringoPlayer = AoringoPlayer(player)
        ResourcePack(plugin).adaptation(player)
        aoringoPlayer.setPrefix()
        aoringoPlayer.giveServerMenuItem(plugin)
    }
    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player
        val serverMenu = ServerMenu(plugin)
        val serverMenuItem = serverMenu.serverMenuItem
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
            Material.ENCHANTING_TABLE to Shop(plugin)
        )
        val item = e.item
        val block = e.clickedBlock
        val action = e.action
        val itemName = item?.itemMeta?.displayName
        if (action != Action.RIGHT_CLICK_BLOCK && action != Action.RIGHT_CLICK_AIR) { return }
        if (clickNGBlocks.contains(block?.type) && player.gameMode != GameMode.CREATIVE) {
            e.isCancelled = true
        }
        if (blockMap.keys.contains(block?.type)) {
            e.isCancelled = true
            val gui = blockMap[block?.type]?.make(player) ?: return
            player.openInventory(gui)
        } else if (makeGUIs.keys.contains(itemName)) {
            e.isCancelled = true
            val gui = makeGUIs[item?.itemMeta?.displayName]?.make(player) ?: return
            player.openInventory(gui)
        } else if (item == serverMenuItem) {
            val gui = serverMenu.make(player)
            player.openInventory(gui)
        }
    }
    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val makeGUIs = mapOf(
            FoodMenu(plugin).let { foodMenu -> foodMenu.guiName to foodMenu },
            Shop(plugin).let { shop -> shop.guiName to shop },
            ServerMenu(plugin).let { serverMenu -> serverMenu.guiName to serverMenu }
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
    fun onPlayerInteractItemFrame(e: PlayerInteractEntityEvent) {
        val player = e.player
        val cookwareManager = CookwareManager(plugin)
        val aoringoPlayer = AoringoPlayer(player)
        val isSneak = player.isSneaking
        val itemFrame = e.rightClicked as? ItemFrame ?: return
        val itemFrameItem = itemFrame.item
        val item = player.inventory.itemInMainHand.clone()
        val underBlock = itemFrame.location.clone().add(0.0, -1.0, 0.0).block
        val choppingBoard = ChoppingBoard(cookwareManager)
        val batter = Batter(cookwareManager)
        val sneakGuidanceMessage = "${ChatColor.GOLD}調理する場合は スニークしながらクリックしてください"
        val playerHasItemUseMap = mapOf(
            choppingBoard.knifeItem to { choppingBoard.process(itemFrame, player) },
            ItemStack(Material.SPONGE) to { CookwareManager(plugin).cleanTray(itemFrame, player, underBlock) }
        )
        val itemFrameItemUseMap = mapOf(
            batter.batterItem to { batter.cover(item, player, itemFrame) }
        )
        val underBlockMap = CookwareManager(plugin).underBlockMap
        if (underBlockMap.keys.contains(underBlock.type)) {
            when (itemFrameItem.type) {
                Material.AIR -> {
                    val foodstuff = item.clone()
                    foodstuff.amount = 1
                    val cookTime = TeamScoreBoard(plugin, aoringoPlayer.scoreboardTargetName).acquisitionTime(underBlock)
                    underBlockMap[underBlock.type]?.cooking(itemFrame, foodstuff, cookTime)
                    return
                }
                else -> e.isCancelled = true
            }
        } else if (underBlock.type == Material.DISPENSER) {
            Mixer(cookwareManager).mix(itemFrame)
            return
        }
        if (!isSneak) {
            aoringoPlayer.sendActionBar(sneakGuidanceMessage)
            return
        }
        e.isCancelled = true
        val playerHasCheckItem = item.clone()
        playerHasCheckItem.amount = 1
        if (playerHasItemUseMap.keys.contains(playerHasCheckItem)) {
            playerHasItemUseMap[playerHasCheckItem]?.invoke()
        } else if (itemFrameItemUseMap.keys.contains(itemFrameItem)) {
            itemFrameItemUseMap[itemFrameItem]?.invoke()
        } else {
            Coalescence(cookwareManager).cooking(itemFrame, player)
        }
    }
    @EventHandler
    fun onPlayerInteractCustomer(e: PlayerInteractEntityEvent) {
        val customerManager = CustomerManager(plugin)
        val customer = e.rightClicked as? Villager ?: return
        if (!customerManager.isCustomer(customer)) { return }
        e.isCancelled = true
        val player = e.player
        val tray = customerManager.tray
        val item = player.inventory.itemInMainHand.clone()
        if (item.type == Material.AIR) { return }
        item.amount = 1
        when (item) {
            tray -> { customerManager.passTray(customer, player) }
            else -> { customerManager.receiveProducts(customer, player) }
        }
    }
    @EventHandler
    fun onHangingBreakByEntity(e: HangingBreakByEntityEvent) {
        val player = e.remover as? Player ?: return
        if (e.entity !is ItemFrame) return
        if (player.gameMode == GameMode.CREATIVE) { return }
        e.isCancelled = true
    }
    @EventHandler
    fun onPlayerToggleSneak(e: PlayerToggleSneakEvent) {
        val player = e.player
        val blockUnderTwo = player.location.clone().add(0.0, -2.0, 0.0).block
        if (!e.isSneaking) { return }
        if (blockUnderTwo.type == Material.COMMAND_BLOCK) {
            Teleporter(plugin).sneakTeleport(player)
        }
    }
    @EventHandler
    fun onPlayerTakeLecternBook(e: PlayerTakeLecternBookEvent) {
        val player = e.player
        val book = e.book ?: return
        val adminBookManager = AdminBookManager(book)
        if (!adminBookManager.isAdminBook()) { return }
        if (player.gameMode == GameMode.CREATIVE) { return }
        e.isCancelled = true
        player.inventory.addItem(book)
        player.closeInventory()
    }
    @EventHandler
    fun onEntityDamageByEntity(e: EntityDamageByEntityEvent) {
        val player = e.damager as? Player ?: return
        val entity = e.entity as? Villager ?: return
        val customerManager = CustomerManager(plugin)
        if (player.gameMode == GameMode.CREATIVE) { return }
        if (!customerManager.isCustomer(entity)) { return }
        e.isCancelled = true
    }
    @EventHandler
    fun onPlayerChangedWorld(e: PlayerChangedWorldEvent) {
        val player = e.player
        val aoringoPlayer = AoringoPlayer(player)
        aoringoPlayer.giveServerMenuItem(plugin)
    }
}
