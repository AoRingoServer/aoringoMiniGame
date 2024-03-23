package com.github.AoRingoServer.CookGame.Customer

import com.github.AoRingoServer.CookGame.FoodManager
import com.github.AoRingoServer.CookGame.SalesManager
import com.github.AoRingoServer.Datas.Yml
import com.github.AoRingoServer.ItemManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class CustomerManager(private val plugin: Plugin) {
    private val name = "${ChatColor.YELLOW}お客様"
    private val customerTag = "cookGameCustomer"
    private val customorRecipManager = CustomorRecipManager(plugin)
    private val foodManager = FoodManager(plugin)
    private val itemManager = ItemManager()
    val tray = itemManager.make(Material.BOWL, "${ChatColor.GOLD}おぼん", customModelData = 3)
    private val yml = Yml(plugin)
    private val cookGameConfig = yml.acquisitionYml("", "cookGameConfig")

    fun summonCustomor(location: Location, level: Int) {
        val world = location.world
        val villager: Villager = world!!.spawn(location, org.bukkit.entity.Villager::class.java)
        villager.customName = name
        villager.setAI(false)
        villager.scoreboardTags.add(customerTag)
        villager.isInvulnerable = true
        villager.villagerLevel = level
        villager.isCustomNameVisible = true
        reset(villager)
    }
    fun isCustomer(villager: Villager): Boolean {
        return villager.scoreboardTags.contains(customerTag)
    }
    private fun reset(villager: Villager) {
        villager.equipment?.setItemInMainHand(ItemStack(Material.AIR))
        val professions = Villager.Profession.values()
        val randomProfession = professions.random()
        villager.profession = randomProfession
        setHelmetItem(villager, tray)
    }
    private fun setHelmetItem(villager: Villager, item: ItemStack) {
        villager.equipment?.helmet = item
        villager.customName = "${item.itemMeta?.displayName}…"
    }
    fun passTray(villager: Villager, player: Player) {
        if (villager.equipment?.helmet != tray) { return }
        villager.equipment?.setItemInMainHand(tray)
        player.playSound(player, Sound.UI_BUTTON_CLICK, 1f, 1f)
        player.inventory.removeItem(tray)
        val orderFood = customorRecipManager.determineOrderFood(villager)
        setHelmetItem(villager, orderFood)
    }
    fun receiveProducts(villager: Villager, player: Player) {
        val orderFood = villager.equipment?.helmet ?: return
        val provisionFood = player.inventory.itemInMainHand.clone()
        provisionFood.amount = 1
        if (orderFood != provisionFood) { return }
        player.inventory.removeItem(provisionFood)
        val foodID = foodManager.acquisitionFoodID(orderFood) ?: return
        val foodInfo = foodManager.makeFoodInfo(foodID)
        val dirtyTray = customorRecipManager.makeTray(foodInfo)
        player.inventory.addItem(dirtyTray)
        SalesManager(plugin).addition(foodInfo, player)
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
        setDuringMeal(villager, orderFood)
        Bukkit.getScheduler().runTaskLater(
            plugin,
            Runnable {
                reset(villager)
            },
            120L
        ) // 20Lは1秒を表す（1秒 = 20ticks）
    }
    private fun setDuringMeal(villager: Villager, food: ItemStack) {
        villager.customName = "${ChatColor.RED}食事中…"
        villager.equipment?.setItemInMainHand(food)
        villager.equipment?.helmet = ItemStack(Material.AIR)
    }
}
