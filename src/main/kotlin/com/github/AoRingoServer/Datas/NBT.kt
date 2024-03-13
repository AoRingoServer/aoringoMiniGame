package com.github.AoRingoServer.Datas

import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.MetadataValue
import org.bukkit.plugin.Plugin
import javax.swing.text.html.parser.Entity

class NBT(private val plugin: Plugin) {
    fun setCustomNBT(entity: org.bukkit.entity.Entity, key: String, value: String?) {
        entity.setMetadata(key, FixedMetadataValue(plugin, value))
    }
    fun acquisitionCustomNBT(entity: org.bukkit.entity.Entity, key: String): String? {
        val metadataList: List<MetadataValue> = entity.getMetadata(key)
        for (metadata in metadataList) {
            if (metadata.owningPlugin === plugin) {
                return metadata.value().toString()
            }
        }
        return null
    }
}
