package org.projectforge.framework.time

import org.projectforge.framework.persistence.user.api.ThreadLocalUserContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields

class PFDateTimeUtils {
    companion object {
        @JvmStatic
        fun getBeginOfWeek(date: ZonedDateTime): ZonedDateTime {
            val field = WeekFields.of(getFirstDayOfWeek(), 1).dayOfWeek()
            return getBeginOfDay(date.with(field, 1))
        }

        @JvmStatic
        fun getFirstDayOfWeek(): DayOfWeek {
            val firstDayOfWeek = ThreadLocalUserContext.getJodaFirstDayOfWeek()
            return getDayOfWeek(firstDayOfWeek)!!
        }

        @JvmStatic
        fun getBeginOfDay(dateTime: ZonedDateTime): ZonedDateTime {
            return dateTime.toLocalDate().atStartOfDay(dateTime.getZone())
        }

        @JvmStatic
        fun getEndOfDay(dateTime: ZonedDateTime): ZonedDateTime {
            return dateTime.truncatedTo(ChronoUnit.DAYS).plusDays(1)
        }

        @JvmStatic
        fun convertToLocalDate(dateMidnight: org.joda.time.DateMidnight?): java.time.LocalDate? {
            if (dateMidnight == null)
                return null
            return java.time.LocalDate.of(dateMidnight.year, dateMidnight.monthOfYear, dateMidnight.dayOfMonth)
        }

        /**
         * dayNumber 1 - Monday, 2 - Tuesday, ..., 7 - Sunday
         */
        @JvmStatic
        fun getDayOfWeek(dayNumber: Int): DayOfWeek? {
            return when (dayNumber) {
                1 -> DayOfWeek.MONDAY
                2 -> DayOfWeek.TUESDAY
                3 -> DayOfWeek.WEDNESDAY
                4 -> DayOfWeek.THURSDAY
                5 -> DayOfWeek.FRIDAY
                6 -> DayOfWeek.SATURDAY
                7 -> DayOfWeek.SUNDAY
                else -> null
            }
        }

    }
}
