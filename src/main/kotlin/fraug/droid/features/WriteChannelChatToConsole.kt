package fraug.droid.features

import com.github.philippheuer.events4j.annotation.EventSubscriber
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent
import java.util.*
import kotlin.math.absoluteValue

object WriteChannelChatToConsole {

    /** Subscribe to the ChannelMessage Event and write the output to the console */
    @EventSubscriber
    fun onChannelMessage(event: ChannelMessageEvent) {
        //println("${event.user.name}: ${event.message}")
        val response = when {
            event.message.startsWith("!help") -> "bit.ly/fraugdroid"
            event.message.startsWith("!zoom") -> "ce soir c'est sur remo.co: https://live.remo.co/e/le-point-sur-les-applis-de-traci"
            event.message.startsWith("!remo") -> "https://live.remo.co/e/le-point-sur-les-applis-de-traci"
            event.message.startsWith("!slido") -> "https://app.sli.do/event/s128b9bl"
            event.message.startsWith("!cfp") -> "https://conference-hall.io/public/event/T4ow4S9EGQaCFFNt6waP"
            event.message.startsWith("!openfeedback") -> "https://openfeedback.io/fraug4/2020-05-06/zg6s2VSWyjZqPOOU2mJD"
            event.message.startsWith("!feedback") -> "https://openfeedback.io/fraug4/2020-05-06/zg6s2VSWyjZqPOOU2mJD"
            //event.message.startsWith("!fish") -> addAttempt(event)
            //event.message.startsWith("!leaderboard") -> Leaderboard.summary()
            event.message.startsWith("!hug") -> hug(event)
            event.message.startsWith("!pizza") -> pizza(event)
            else -> null
        }

        if (response  != null) {
            event.twitchChat.sendMessage(event.channel.name, response)
        }
    }

    private fun hug(event: ChannelMessageEvent): String? {
        var username = event.message.substringAfter("!hug").trim()
        if (username.isNullOrBlank()) {
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
            else  -> listOf("👞", "👠", "🥾", "⛸").random()
        }
    }

    private fun pizza(event: ChannelMessageEvent): String? {
        val jokes = listOf("Want to hear a joke about pizza? Never mind, it's too cheesy.",
        "What do you call a sleeping pizza? a piZZZZZZa!",
        "Why did the man go into the pizza business? He wanted to make some dough!",
        "Waiter, will my pizza be long? No sir, it will be round!",
        "What is a dog's favorite pizza? PUParonni!",
        "What's the difference between a pizza and my pizza jokes? My pizza jokes can't be topped!")

        return jokes.get(Random().nextInt().absoluteValue.rem(jokes.size))
    }
}
