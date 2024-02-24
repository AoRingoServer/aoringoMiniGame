package com.github.Ringoame196

import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onEnable() {
        super.onEnable()
        val event = Events(this)
        server.pluginManager.registerEvents(event, this)
    }
}
