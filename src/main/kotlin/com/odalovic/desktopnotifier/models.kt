package com.odalovic.desktopnotifier

import kotlinx.serialization.Serializable

@Serializable
data class Config(val content: Map<String, Content>, val notificationFrequencyMillis: Long)

@Serializable
data class Content(val items: List<Item>)

@Serializable
data class Item(val t: String, val m: String)
