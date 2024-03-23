package com.github.AoRingoServer.CookGame.Customer

import com.github.AoRingoServer.Datas.Yml
import com.github.AoRingoServer.ItemManager
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Villager
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class CustomerManager(private val plugin: Plugin) {
    private val name = "${ChatColor.YELLOW}お客様"
    private val customerTag = "cookGameCustomer"
    val customorRecipManager = CustomorRecipManager(plugin)
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
        customorRecipManager.setDefaultRecipe(villager)
    }
    fun isCustomer(villager: Villager): Boolean {
        return villager.scoreboardTags.contains(customerTag)
    }
    fun reset(villager: Villager) {
        val level = villager.villagerLevel
        villager.inventory.setItem(1, ItemStack(Material.AIR))
        val professions = Villager.Profession.values()
        val randomProfession = professions.random()
        villager.profession = randomProfession
        setHelmetItem(villager, tray)
    }
    private fun setHelmetItem(villager: Villager, item: ItemStack) {
        villager.equipment?.helmet = item
        villager.customName = "${item.itemMeta?.displayName}…"
    }
    fun passTray(villager: Villager) {
        if (villager.equipment?.helmet != tray) { return }
        villager.equipment?.setItemInMainHand(tray)
    }
}
