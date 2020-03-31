package fraug.droid.features

import fraug.droid.Attempt
import fraug.droid.Fish

data class Score(
    val found: Int,
    val errors: Int,
    val first: Int
)

object Leaderboard {
    fun asString(scores: Map<String, Score>, limit: Int = Int.MAX_VALUE): String {
        return "position. username: score (found - errors - first)\n" + scores.entries.sortedByDescending {
            val r = it.value.found - it.value.errors
            if (r != 0) {
                return@sortedByDescending r
            }
            it.value.first
        }.mapIndexed { index, entry ->
            "$index. ${entry.key}: ${entry.value.found - entry.value.errors} (${entry.value.found}-${entry.value.errors}-${entry.value.first})"
        }.take(limit)
            .joinToString("\n")
    }
    fun asString(): String {
        return asString(compute())
    }

    fun summary(scores: Map<String, Score>, limit: Int = Int.MAX_VALUE): String {
        return scores.entries.sortedByDescending {
            (it.value.found - it.value.errors) * 1000 + it.value.first
        }.mapIndexed { index, entry ->
            "$index. ${entry.key} (${entry.value.found - entry.value.errors})"
        }.take(limit)
            .joinToString(", ")
    }

    fun summary(): String {
        return summary(compute(), 10)
    }
    fun compute(): Map<String, Score> {
        val fishes = fishQueries.selectAllFish().executeAsList().sortedBy { it.start }
        val attempts = fishQueries.selectAllAttempts().executeAsList().sortedBy { it.timestamp }
        return compute(fishes, attempts)
    }

    fun compute(fishes: List<Fish>, attempts: List<Attempt>): Map<String, Score> {
        val scores = mutableMapOf<String, Score>()
        val lastFish = mutableMapOf<String, Long>()

        var currentFishIndex = 0
        var first = 1

        for (currentAttemptIndex in attempts.indices) {
            val attempt = attempts[currentAttemptIndex]

            while (currentFishIndex < fishes.size) {
                val fish = fishes[currentFishIndex]

                if (attempt.timestamp < fish.start) {
                    // error
                    scores.compute(attempt.username) { key, old ->
                        old?.copy(errors = old.errors + 1) ?: Score(0, 1, 0)
                    }
                    break
                } else if (
                    fish.end == null && attempt.timestamp >= fish.start
                    || attempt.timestamp >= fish.start && attempt.timestamp <= fish.end!!
                ) {
                    // good
                    val lastFishId = lastFish.get(attempt.username)
                    if (lastFishId != null && lastFishId == fish.id) {
                        // duplicate fish
                        break
                    }
                    lastFish.put(attempt.username, fish.id)

                    scores.compute(attempt.username) { key, old ->
                        if (old == null) {
                            Score(1, 0, first)
                        } else {
                            old.copy(
                                found = old.found + 1,
                                first = old.first + first
                            )
                        }
                    }
                    first = 0
                    break
                } else {
                    currentFishIndex++
                    first = 1
                }
            }
            if (currentFishIndex == fishes.size) {
                // error
                scores.compute(attempt.username) { key, old ->
                    old?.copy(errors = old.errors + 1) ?: Score(0, 1, 0)
                }
            }
        }
        return scores
    }
}