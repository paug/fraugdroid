package fraug.droid.features

import com.github.philippheuer.events4j.annotation.EventSubscriber
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import java.util.*
import kotlin.math.absoluteValue

object WriteChannelChatToConsole {
    val codeOfConduct = """
Le FRAUG est fier d’être une communauté ouverte et engagée, respectant les différences et la diversité. Ceci implique qu'aucun comportement ou propos déplacé n'est accepté lors de nos meetups. 
Voici un rappel de ce que nous n'accepterons pas : blagues ou offenses à propos de la sexualité / race / religion / nationalité / morphologie / âge.
""".trimIndent()

    /** Subscribe to the ChannelMessage Event and write the output to the console */
    @EventSubscriber
    fun onChannelMessage(event: ChannelMessageEvent) {
        //println("${event.user.name}: ${event.message}")
        if (!event.message.startsWith("!")) {
            return
        }
        val command = event.message.substring(1)
        val response = when {
            command.startsWith("help") || command.startsWith("commands") -> "https://fraug.fr/help"
            command.startsWith("zoom") -> "https://fraug.fr/zoom"
            command.startsWith("remo") -> "https://fraug.fr/remo"
            command.startsWith("slido") -> "https://fraug.fr/slido"
            command.startsWith("cfp") -> "https://fraug.fr/cfp"
            command.startsWith("gather") -> "https://fraug.fr/gather"
            command.contains("feedback") -> "https://fraug.fr/feedback"
            command.startsWith("charte") || command.contains("conduct") -> codeOfConduct
            //command.startsWith("fish") -> addAttempt(event)
            //command.startsWith("leaderboard") -> Leaderboard.summary()
            command.startsWith("hug") -> hug(event)
            command.startsWith("pizza") -> pizza(event)
            else -> null
        }

        if (response != null) {
            event.twitchChat.sendMessage(event.channel.name, response)
        }
    }

    private fun hug(event: ChannelMessageEvent): String? {
        var username = event.message.substringAfter("!hug").trim()
        if (username.isBlank()) {
            username = event.user.name
        }
        return "FraugDroid envoie un hug à ${username} <3 <3"
    }

    private fun addAttempt(event: ChannelMessageEvent): String? {
        fishQueries.addAttempt(event.user.name, Date().time)
        val visibleFish = try {
            fishQueries.selectLastVisibleFish().executeAsOne()
        } catch (e: Exception) {
            null
        }
        return when {
            visibleFish != null -> listOf("🎣", "🐟", "🐠", "🐡").random()
            else -> listOf("👞", "👠", "🥾", "⛸").random()
        }
    }

    private fun pizza(event: ChannelMessageEvent): String? {
        val jokes = listOf(
            "Want to hear a joke about pizza? Never mind, it's too cheesy.",
            "What do you call a sleeping pizza? a piZZZZZZa!",
            "Why did the man go into the pizza business? He wanted to make some dough!",
            "Waiter, will my pizza be long? No sir, it will be round!",
            "What is a dog's favorite pizza? PUParonni!",
            "What's the difference between a pizza and my pizza jokes? My pizza jokes can't be topped!"
        )

        return jokes.get(Random().nextInt().absoluteValue.rem(jokes.size))
    }
}
