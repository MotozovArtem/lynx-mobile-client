package org.lynx.client.ui.util

import java.text.SimpleDateFormat
import java.util.*

class Formatters {
    companion object {
        // FIXME: Change with user changed locale
        val dateFormatter: SimpleDateFormat = SimpleDateFormat("d MMMM", Locale.UK)
        val timeFormatter: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.UK)
        val dateTimeFormatter: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.UK)
    }
}