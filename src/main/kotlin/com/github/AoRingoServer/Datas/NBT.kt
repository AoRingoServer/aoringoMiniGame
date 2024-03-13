package com.github.AoRingoServer.Datas

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

class NBT(private val plugin: Plugin) {
    private val foodIDKey = "foodID"
    fun set(entity: org.bukkit.entity.Entity, key: String, value: String?) {
        entity.setMetadata(key, FixedMetadataValue(plugin, value))
    }
    fun acquisition(entity: org.bukkit.entity.Entity, keyName: String): String? {
        val metadataList: List<MetadataValue> = entity.getMetadata(keyName)
        for (metadata in metadataList) {
            if (metadata.owningPlugin === plugin) {
                return metadata.value().toString()
            }
        }
        return null
    }
    fun set(item: ItemStack, keyName: String, value: String) {
        val itemMeta = item.itemMeta
        val key = makeKey(keyName)
        itemMeta?.persistentDataContainer?.set(key, PersistentDataType.STRING, value)
        item.setItemMeta(itemMeta)
    }
    fun acquisition(item: ItemStack, keyName: String): String? {
        val itemMeta = item.itemMeta
        val key = makeKey(keyName)
        return itemMeta?.persistentDataContainer?.get(key, PersistentDataType.STRING)
    }
    private fun makeKey(keyName: String): NamespacedKey {
        return NamespacedKey(plugin, keyName)
    }
}
