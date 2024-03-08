package com.github.AoRingoServer

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

class Server(private val plugin: Plugin) {
    fun countDownServerSotp(count: Int?) {
        var c = count ?: 60
        val message = "${ChatColor.YELLOW}[サーバー] ${c}秒後にサーバーはシャットダウンします"
        Bukkit.broadcastMessage(message)
        object : BukkitRunnable() {
            override fun run() {
                c --
                if (!isPlayerPresent()) {
                    stopServer()
                    this.cancel()
                }
                when (c) {
                    0 -> {
                        stopServer()
                        this.cancel()
                    }
                    2 -> kickAllPlayer()
                    in 1..10 -> countDownMessage(c)
                    else -> {
                        if (c % 10 == 0) {
                            countDownMessage(c)
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L) // 1秒間隔 (20 ticks) でタスクを実行
    }
    private fun countDownMessage(c: Int) {
        val countDownMessage = "${ChatColor.YELLOW}[サーバー] シャットダウンまで残り${c}秒"
        Bukkit.broadcastMessage(countDownMessage)
    }
    private fun isPlayerPresent(): Boolean {
        return Bukkit.getServer().onlinePlayers.isNotEmpty()
    }
    private fun kickAllPlayer() {
        val players = Bukkit.getOnlinePlayers()
        val message = "${ChatColor.YELLOW}サーバーはシャットダウンしました"
        for (player in players) {
            player.kickPlayer(message)
        }
    }
    private fun stopServer() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop")
    }
}
