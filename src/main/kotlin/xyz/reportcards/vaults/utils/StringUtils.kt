package xyz.reportcards.vaults.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import java.text.DecimalFormat

private val MINI_MESSAGE = MiniMessage.builder().build()

operator fun String.not(): Component {
    return MINI_MESSAGE.deserialize(this)
}

private val FORMATTER_WHOLE_NUMBER = DecimalFormat("#,###")
private val FORMATTER_DECIMAL = DecimalFormat("#,###.##")

fun Number.commas(): String {
    val isWholeNumber = this.toDouble() % 1 == 0.0
    if (isWholeNumber) return if (this.toDouble() < 1000) this.toString() else FORMATTER_WHOLE_NUMBER.format(this.toDouble())
    return if (this.toDouble() < 1000) this.toString() else FORMATTER_DECIMAL.format(this.toDouble())
}