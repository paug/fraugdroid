package fraug.droid

import fraug.droid.features.Leaderboard
import fraug.droid.features.Score
import org.junit.Assert
import org.junit.Test

class LeaderboardTests {

    @Test
    fun test() {
        val fishes = listOf(
            Fish.Impl(id = 1, start = 10, end = 20),
            Fish.Impl(id = 1, start = 30, end = 40),
            Fish.Impl(id = 1, start = 50, end = 60)
        )
        val attempts = listOf(
            Attempt.Impl(id = 1, username = "bob", timestamp = 0),
            Attempt.Impl(id = 1, username = "bob", timestamp = 9),
            Attempt.Impl(id = 1, username = "bob", timestamp = 10),
            Attempt.Impl(id = 1, username = "bob", timestamp = 31)
        )

        // found: 2 erros: 2 first: 2
        val scores = Leaderboard.compute(fishes, attempts)
        println(scores)
    }

    @Test
    fun `multiple finds only count once`() {
        val fishes = listOf(
            Fish.Impl(id = 1, start = 10, end = 20),
            Fish.Impl(id = 1, start = 30, end = 40),
            Fish.Impl(id = 1, start = 50, end = 60)
        )
        val attempts = listOf(
            Attempt.Impl(id = 1, username = "bob", timestamp = 10),
            Attempt.Impl(id = 1, username = "bob", timestamp = 12)
        )

        // found: 2 errors: 0 first: 1
        val scores = Leaderboard.compute(fishes, attempts)
        println(scores)
    }

    @Test
    fun `fishes can be skipped`() {
        val fishes = listOf(
            Fish.Impl(id = 1, start = 10, end = 20),
            Fish.Impl(id = 1, start = 30, end = 40),
            Fish.Impl(id = 1, start = 50, end = 60)
        )
        val attempts = listOf(
            Attempt.Impl(id = 1, username = "bob", timestamp = 50)
        )

        // found: 1 errors: 0 first: 1
        val scores = Leaderboard.compute(fishes, attempts)
        println(scores)
    }

    @Test
    fun `attempts after the last fish fail`() {
        val fishes = listOf(
            Fish.Impl(id = 1, start = 10, end = 20),
            Fish.Impl(id = 1, start = 30, end = 40),
            Fish.Impl(id = 1, start = 50, end = 60)
        )
        val attempts = listOf(
            Attempt.Impl(id = 1, username = "bob", timestamp = 70)
        )

        val scores = Leaderboard.compute(fishes, attempts)
        println(scores)
        Assert.assertEquals(scores["bob"]!!, Score(0, 1, 0))
    }

    @Test
    fun `fishes can be skipped2`() {
        val fishes = listOf(
            Fish.Impl(id = 1, start = 10, end = 20),
            Fish.Impl(id = 2, start = 30, end = 40),
            Fish.Impl(id = 3, start = 50, end = 60),
            Fish.Impl(id = 4, start = 70, end = 80)
        )
        val attempts = listOf(
            Attempt.Impl(id = 1, username = "bob", timestamp = 31),
            Attempt.Impl(id = 2, username = "bob", timestamp = 71)
        )

        val scores = Leaderboard.compute(fishes, attempts)
        println(scores)
        Assert.assertEquals(scores["bob"]!!, Score(2, 0, 2))
    }

    @Test
    fun `first is working`() {
        val fishes = listOf(
            Fish.Impl(id = 1, start = 10, end = 20),
            Fish.Impl(id = 2, start = 30, end = 40),
            Fish.Impl(id = 3, start = 50, end = 60),
            Fish.Impl(id = 4, start = 70, end = 80)
        )
        val attempts = listOf(
            Attempt.Impl(id = 1, username = "bob", timestamp = 31),
            Attempt.Impl(id = 2, username = "alice", timestamp = 32),
            Attempt.Impl(id = 3, username = "alice", timestamp = 44),
            Attempt.Impl(id = 3, username = "alice", timestamp = 71),
            Attempt.Impl(id = 3, username = "bob", timestamp = 74)
        )

        val scores = Leaderboard.compute(fishes, attempts)
        println(scores)
        Assert.assertEquals(scores["bob"]!!, Score(2, 0, 1))
        Assert.assertEquals(scores["alice"]!!, Score(2, 1, 1))
    }
}