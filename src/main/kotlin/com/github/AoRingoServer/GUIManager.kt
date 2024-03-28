package com.github.AoRingoServer

class GUIManager {
    fun autoGUISize(foodInfoList: MutableList<*>): Int {
        val listSize = foodInfoList.size
        val maxSize = 54
        val column = listSize / 9 + 1
        val size = column * 9
        return if (size > maxSize) { maxSize } else { size }
    }
}
