package com.github.AoRingoServer

import org.bukkit.entity.Player

class AoringoPlayer(val player: Player) {
    fun setPrefix() {
    }
    private fun checkAdmin(): Boolean {
        return player.isOp
    }
}
