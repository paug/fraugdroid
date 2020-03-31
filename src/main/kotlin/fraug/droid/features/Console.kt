package fraug.droid.features

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintHelpMessage
import com.github.ajalt.clikt.core.UsageError
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.types.long
import fraug.droid.Bot
import fraug.droid.Fish
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.io.File
import java.util.*
import kotlin.system.exitProcess

object Console : Runnable {
    fun start() {
        println("starting console")
        Thread(this).start()
    }

    fun hhmmssToDate(hhmmss: String): Date {
        val (hours, minutes, seconds) = hhmmss.split(":")
            .map { it.toInt() }

        val calendar = GregorianCalendar.getInstance()
        calendar.set(Calendar.HOUR, hours)
        calendar.set(Calendar.MINUTE, minutes)
        calendar.set(Calendar.SECOND, seconds)

        return calendar.time
    }

    private fun printFish(fish: Fish?) {
        if (fish == null) {
            println("no fish")
            return
        }

        val endDate = fish.end?.let {
            Date(it)
        }
        println("${fish.id}: ${Date(fish.start)} - $endDate")
    }

    private fun printFish(id: Long) {
        val fish = fishQueries.selectFish(id).executeAsOneOrNull()

        printFish(fish)
    }

    private val addCommand = object : CliktCommand(name = "add") {
        val start by argument()
        val end by argument()

        override fun run() {
            fishQueries.addFish(hhmmssToDate(start).time, hhmmssToDate(end).time)
            val id = fishQueries.lastRowId().executeAsOne()
            printFish(id)
        }
    }

    private val startCommand = object : CliktCommand(name = "s") {
        override fun run() {
            fishQueries.addFish(Date().time, null)
            val id = fishQueries.lastRowId().executeAsOne()
            printFish(id)
        }
    }

    private val endCommand = object : CliktCommand(name = "e") {
        override fun run() {
            val fish = fishQueries.selectLastVisibleFish().executeAsOne()
            fishQueries.updateFishEnd(id = fish.id, end = Date().time)
            printFish(fish.id)
        }
    }

    private val editCommand = object : CliktCommand(name = "edit") {
        val id by argument().long()
        val start by argument().optional()
        val end by argument().optional()

        override fun run() {

            if (start != null) {
                fishQueries.updateFishStart(id = id, start = hhmmssToDate(start!!).time)
            }
            if (end != null) {
                fishQueries.updateFishEnd(id = id, end = hhmmssToDate(end!!).time)
            }
            printFish(id)
        }
    }

    val listCommand = object : CliktCommand(name = "list") {
        override fun run() {
            fishQueries.selectAllFish()
                .executeAsList()
                .forEach { printFish(it) }
        }
    }

    val deleteCommand = object : CliktCommand(name = "delete") {
        val id by argument().long()
        override fun run() {
            fishQueries.deleteFish(id)
        }
    }

    val deleteAllCommand = object : CliktCommand(name = "deleteAll") {
        override fun run() {
            fishQueries.deleteAllFishes()
            fishQueries.deleteAllAttempts()
        }
    }

    val quitCommand = object : CliktCommand(name = "quit") {
        override fun run() {
            exitProcess(0)
        }
    }

    val attemptCommand = object : CliktCommand(name = "attempt") {
        val username by argument()
        override fun run() {
            fishQueries.addAttempt(username, Date().time)
        }
    }

    val messageCommand = object : CliktCommand(name = "message") {
        val message by argument().multiple()
        override fun run() {
            Bot.sendMessage(message.joinToString(" "))
        }
    }

    val leaderboardCommand = object : CliktCommand(name = "leaderboard") {
        override fun run() {
            println(Leaderboard.asString())
        }
    }

    val exportCommand = object : CliktCommand(name = "export") {
        override fun run() {
            val fishes = fishQueries.selectAllFish().executeAsList()
            val attempts = fishQueries.selectAllAttempts().executeAsList()

            val jsonObject = JsonObject(mapOf(
                "fishes" to JsonArray(
                    fishes.map {
                        JsonObject(
                            mapOf(
                                "id" to JsonPrimitive(it.id),
                                "start" to JsonPrimitive(it.start),
                                "end" to JsonPrimitive(it.end)
                            )
                        )
                    }
                ),
                "attempts" to JsonArray(
                    attempts.map {

                        JsonObject(
                            mapOf(
                                "id" to JsonPrimitive(it.id),
                                "timestamp" to JsonPrimitive(it.timestamp)
                            )
                        )
                    }
                )
            ))

            File("export.json").writeText(jsonObject.toString())
        }
    }

    private val mainCommand = object : CliktCommand("unused") {
        override fun aliases() = mapOf(
            "l" to listOf("list"),
            "d" to listOf("delete"),
            "q" to listOf("quit"),
            "a" to listOf("attempt")
        )

        override fun run() {
        }
    }.subcommands(
        addCommand,
        listCommand,
        editCommand,
        startCommand,
        endCommand,
        deleteCommand,
        quitCommand,
        deleteAllCommand,
        messageCommand,
        attemptCommand,
        leaderboardCommand,
        exportCommand
    )

    override fun run() {
        while (true) {
            print(">")
            System.out.flush()
            val line = readLine()
            if (line == null) {
                break
            }
            if (line.isBlank()) {
                continue
            }
            try {
                mainCommand.parse(line.split(" "))
            } catch (e: UsageError) {
                println(e.helpMessage())
            } catch (e: PrintHelpMessage) {
                println(e.command.getFormattedHelp())
            } catch (e: Exception) {
                println("Ooopsssiie")
                e.printStackTrace()
            }
        }
    }
}