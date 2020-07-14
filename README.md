A Twitch chatbot based on [twitch4j-chatbot-kotlin](https://github.com/twitch4j/twitch4j-chatbot-kotlin)

To run, type:

```bash
cp src/main/resources/config.yaml.example src/main/resources/config.yaml
# edit credentials.irc with a token from https://twitchapps.com/tmi/
# connect with the account you want to use as bot
# edit channels, add `frenchandroidusergroup`

# run the bot
./gradlew shadowJar && java -jar build/libs/fraugdroid.jar
```