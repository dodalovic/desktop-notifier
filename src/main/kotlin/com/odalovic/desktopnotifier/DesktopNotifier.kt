package com.odalovic.desktopnotifier

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

    @CommandLine.Option(
        names = ["--content-file", "-c"],
        description = ["An absolute path to the content file containing items to be shown as notifications"]
    )
    private lateinit var contentFile: File

    @CommandLine.Option(
        names = ["--change-frequency", "-f"],
        description = ["Duration in seconds between the two notifications"]
    )
    private var changeFrequencyInSeconds: Long = 10

    @CommandLine.Option(
        names = ["--notification-duration", "-d"],
        description = ["How long (in seconds) should a single notification be displayed"]
    )
    private var notificationDurationInSeconds: Long = 5

    private val icon = Icon.create(this::class.java.getResource("/info.png"), "ok")

    private val application = Application.builder()
        .id("desktop-notifier")
        .name("Desktop notifier")
        .icon(icon)
        .timeout(TimeUnit.SECONDS.toMillis(notificationDurationInSeconds))
        .build()

    private val notifier: Notifier =
        SendNotification().setApplication(application).initNotifier()

    override fun run() = try {
        while (true) {
            for (line in contentFile.readLines()) {
                val (key, value) = line.split("=")
                notifier.send(notification(key, value))
                Thread.sleep(TimeUnit.SECONDS.toMillis(changeFrequencyInSeconds))
            }
        }
    } finally {
        notifier.close()
    }

    private fun notification(title: String, message: String) = Notification.builder()
        .icon(icon).title(title).message(message).level(Notification.Level.INFO).subtitle("subtitle").build()
}

fun main(args: Array<String>): Unit = exitProcess(CommandLine(DesktopNotifier()).execute(*args))
