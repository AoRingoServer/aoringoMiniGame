package com.github.AoRingoServer

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta

class AdminBookManager(private val book: ItemStack) {
    private val adminAuthor = "${ChatColor.YELLOW}青りんご運営"
    private val bookMeta = book.itemMeta as BookMeta
    fun isAdminBook(): Boolean {
        val author = bookMeta.author
        return author == adminAuthor
    }
    fun adminHolderChange(player: Player) {
        if (book.type != Material.WRITTEN_BOOK && book.type != Material.WRITABLE_BOOK) {
            return
        }
        bookMeta.author = adminAuthor
        book.setItemMeta(bookMeta)
        player.inventory.setItemInMainHand(book)
        player.playSound(player, Sound.BLOCK_ANVIL_USE, 1f, 1f)
    }
}
