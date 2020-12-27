package com.odalovic.desktopnotifier

import com.charleskorn.kaml.Yaml
import fr.jcgay.notification.*
import picocli.CommandLine
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess


@CommandLine.Command(
    name = "desktop-notifier",
    mixinStandardHelpOptions = true,
    version = ["desktop-notifier 1.0"],
    description = ["Shows desktop notifications"]
)
class DesktopNotifier : Runnable {
    private var config: Config

    @CommandLine.Option(
        names = ["--content-identifier", "-c"],
        description = ["Content identifier inside config file. e.g: LEARN-GERMAN"]
    )
    private lateinit var contentIdentifier: String

    private val icon = Icon.create(
        this::class.java.getResource("/info.png"),
        "ok"
    )

    private var templateFilePath: String = "${System.getProperty("user.home")}/.desktop-notifier/config.yml"

    private val application = Application.builder()
        .id("desktop-notifier")
        .name("Desktop notifier")
        .icon(icon)
        .timeout(TimeUnit.SECONDS.toMillis(5))
        .build()

    private val notifier: Notifier =
        SendNotification().setApplication(application).setChosenNotifier("notifysend").initNotifier()

    init {
        initConfigDir()
        config = Yaml.default.decodeFromString(
            Config.serializer(),
            File(templateFilePath).readText()
        )
    }

    override fun run() = try {
        while (true) {
            val content =
                config.content[contentIdentifier] ?: error("No such content identifier $contentIdentifier")
            for (item in content.items) {
                notifier.send(notification(item))
                Thread.sleep(config.notificationFrequencyMillis)
            }
        }
    } finally {
        notifier.close()
    }

    private fun notification(item: Item) = Notification.builder()
        .icon(icon).title(item.t).message(item.m).level(Notification.Level.INFO).subtitle("subtitle").build()

    private fun initConfigDir() {
        val templateDir = File("${System.getProperty("user.home")}/.desktop-notifier")
        if (!templateDir.exists()) {
            templateDir.mkdirs()
            println("Config directory created: (${templateDir.absolutePath})")
        }
        val templateFileInConfigDir = File("${templateDir.absolutePath}/config.yml")
        if (!templateFileInConfigDir.exists()) {
            val templateContent = this::class.java.getResource("/template.yml").readText()
            templateFileInConfigDir.writeText(templateContent)
            println("Created non-existing config ${templateFileInConfigDir.absolutePath}")
        }
    }
}

fun main(args: Array<String>): Unit = exitProcess(CommandLine(DesktopNotifier()).execute(*args))
