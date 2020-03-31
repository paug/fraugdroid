package fraug.droid

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.FileAppender
import fraug.droid.features.Console
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Redirect the default logs to a file
 */
private fun configureLogging() {
    val lc =  LoggerFactory.getILoggerFactory() as LoggerContext
    val ple = PatternLayoutEncoder()

    ple.pattern = "%date %level [%thread] %logger{10} [%file:%line] %msg%n"
    ple.context = lc
    ple.start()

    val fileAppender = FileAppender<ILoggingEvent>();
    fileAppender.setFile("fish.log");
    fileAppender.setEncoder(ple);
    fileAppender.setContext(lc);
    fileAppender.start();

    val logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
    logger.detachAndStopAllAppenders()
    logger.addAppender(fileAppender)
    logger.level = Level.DEBUG
    logger.isAdditive = false
}
fun main() {
    configureLogging()
    Console.start()

    Bot.registerFeatures()
    Bot.start()
}
