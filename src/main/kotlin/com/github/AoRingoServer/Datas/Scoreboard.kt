package com.github.Ringoame196

import org.bukkit.Bukkit

class Scoreboard(private val scoreboardName: String) {
    init {
        if (!existence()) {
            make(scoreboardName)
        }
    }
    private val scoreboard: org.bukkit.scoreboard.Scoreboard? = Bukkit.getScoreboardManager()?.mainScoreboard
    fun make(name: String) {
        if (existence()) { return }
        scoreboard?.registerNewObjective(scoreboardName, "dummy", name)
    }
    fun set(name: String, value: Int) {
        val objective = scoreboard?.getObjective(scoreboardName) ?: return
        val score = objective.getScore(name)
        score.score = value
    }
    fun existence(): Boolean {
        return scoreboard?.getObjective(scoreboardName) != null
    }
    fun add(name: String, add: Int) {
        val value = getValue(name) + add
        set(name, value)
    }
    fun reduce(name: String, remove: Int) {
        val value = getValue(name) - remove
        set(name, value)
    }
    fun getValue(name: String): Int {
        val objective = scoreboard?.getObjective(scoreboardName) ?: return 0
        val scoreObject = objective.getScore(name)
        return scoreObject.score
    }
}
