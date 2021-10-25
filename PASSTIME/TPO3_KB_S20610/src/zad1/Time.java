package zad1;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Time {
    public static String passed(String from, String to) {
        Locale.setDefault(Locale.ENGLISH);
        try {
            if (from.contains("T") && to.contains("T")) {
                return getStringResult(LocalDateTime.parse(from), LocalDateTime.parse(to));
            } else {
                return getStringResult(LocalDate.parse(from), LocalDate.parse(to));
            }
        } catch (DateTimeParseException ex) {
            return "*** java.time.format.DateTimeParseException: " + ex.getMessage();
        }
    }

    private static final Map<DayOfWeek, String> DAYS_OF_WEEK =
            Stream.of(new Object[][]{
                    {DayOfWeek.SUNDAY, "(niedziela)"},
                    {DayOfWeek.MONDAY, "(poniedziałek)"},
                    {DayOfWeek.TUESDAY, "(wtorek)"},
                    {DayOfWeek.WEDNESDAY, "(środa)"},
                    {DayOfWeek.THURSDAY, "(czwartek)"},
                    {DayOfWeek.FRIDAY, "(piątek)"},
                    {DayOfWeek.SATURDAY, "(sobota)"},
            }).collect(Collectors.toMap(data -> (DayOfWeek) data[0], data -> (String) data[1]));
    private static final Map<Month, String> MONTHS = Stream.of(new Object[][]{
            {Month.JANUARY, "stycznia"},
            {Month.FEBRUARY, "lutego"},
            {Month.MARCH, "marca"},
            {Month.APRIL, "kwietnia"},
            {Month.MAY, "maja"},
            {Month.JUNE, "czerwca"},
            {Month.JULY, "lipca"},
            {Month.AUGUST, "sierpnia"},
            {Month.SEPTEMBER, "września"},
            {Month.OCTOBER, "października"},
            {Month.NOVEMBER, "listopada"},
            {Month.DECEMBER, "grudnia"}
    }).collect(Collectors.toMap(data -> (Month) data[0], data -> (String) data[1]));

    private static String getStringResult(LocalDate from, LocalDate to) {
        return getStringZ(from) + getStringDo(to) + getStringUplywa(from, to);
    }

    private static String getStringZ(LocalDate from) {
        String fromFull = "Od " + from.getDayOfMonth() + " " + MONTHS.get(from.getMonth()) + " " + from.getYear() + " " + DAYS_OF_WEEK.get(from.getDayOfWeek());
        return fromFull;
    }

    private static String getStringDo(LocalDate to) {
        String toFull = " do " + to.getDayOfMonth() + " " + MONTHS.get(to.getMonth()) + " " + to.getYear() + " " + DAYS_OF_WEEK.get(to.getDayOfWeek());
        return toFull;
    }

    private static String getStringUplywa(LocalDate from, LocalDate to) {
        long daysBetween = ChronoUnit.DAYS.between(from, to);
        double weeksBetween = daysBetween / 7.0;
        String durationFull = "\n- mija: " + daysBetween + (daysBetween == 1 ? " dzień, " : " dni, ") + "tygodni " + (daysBetween % 7.0 == 0 ? String.valueOf(weeksBetween).split("\\.")[0] : String.format("%.2f", weeksBetween)) + getCalendarString(from, to);
        return durationFull;
    }

    private static String getStringResult(LocalDateTime from, LocalDateTime to) {
        ZonedDateTime Z = ZonedDateTime.of(from, ZoneId.of("Europe/Warsaw"));
        ZonedDateTime Do = ZonedDateTime.of(to, ZoneId.of("Europe/Warsaw"));
        long dniPomiedzy = ChronoUnit.DAYS.between(Z.toLocalDate(), Do.toLocalDate());
        double tygodniePomiedzy = dniPomiedzy / 7.0;
        if (dniPomiedzy % 7.0 == 0) {
            return getStringZ(Z) + getStringDo(Do, dniPomiedzy) +
                    (dniPomiedzy == 1 ? " dzień, " : " dni, ") + "tygodni " + String.valueOf(tygodniePomiedzy).split("\\.")[0] +
                    getStringCzas(Z, Do);
        }
        if (dniPomiedzy == 1) {
            return getStringZ(Z) + getStringDo(Do, dniPomiedzy) +
                    " dzień, " + "tygodni " + String.format("%.2f", tygodniePomiedzy) +
                    getStringCzas(Z, Do);
        }
        return getStringZ(Z) + getStringDo(Do, dniPomiedzy) +
                " dni, " + "tygodni " + String.format("%.2f", tygodniePomiedzy) +
                getStringCzas(Z, Do);
    }

    private static String getStringZ(ZonedDateTime from) {
        String Z = "Od " + from.getDayOfMonth() + " " + MONTHS.get(from.getMonth()) + " " + from.getYear() + " " + DAYS_OF_WEEK.get(from.getDayOfWeek()) +
                " godz. " + from.format(DateTimeFormatter.ofPattern("hh:mm"));
        return Z;
    }

    private static String getStringDo(ZonedDateTime to, long dniPomiedzy) {
        String Do = " do " + to.getDayOfMonth() + " " + MONTHS.get(to.getMonth()) + " " + to.getYear() + " " + DAYS_OF_WEEK.get(to.getDayOfWeek()) +
                " godz. " + to.format(DateTimeFormatter.ofPattern("hh:mm")) + "\n- mija: " + dniPomiedzy;
        return Do;
    }


    private static String getCalendarString(LocalDate Z, LocalDate Do) {
        long daysBetween = ChronoUnit.DAYS.between(Z, Do);
        String result = "";
        if (daysBetween != 0) {
            int lata = Period.between(Z, Do).getYears();
            int miesiace = Period.between(Z, Do).getMonths();
            int dni = Period.between(Z, Do).getDays();
            result = "\n- kalendarzowo: ";
            if (lata != 0) {
                if (lata == 1) {
                    result += lata + " rok, ";
                } else if (lata < 5) {
                    result += lata + " lata, ";
                } else {
                    result += lata + " lat, ";
                }
            }
            if (miesiace != 0) {
                if (miesiace == 1) {
                    result += miesiace + " miesiąc, ";
                } else if (miesiace < 5) {
                    result += miesiace + " miesiące, ";
                } else {
                    result += miesiace + " miesięcy, ";
                }
            }
            if (dni != 0) {
                if (dni == 1) {
                    result += dni + " dzień, ";
                } else {
                    result += dni + " dni, ";
                }
            }
            result = result.substring(0, result.length() - 2);
        }
        return result;
    }

    private static String getStringCzas(ZonedDateTime Z, ZonedDateTime Do) {
        long godziny = Duration.between(Z, Do).toHours();
        long minuty = Duration.between(Z, Do).toMinutes();
        return "\n- godzin: " + godziny + ", minut: " + minuty + getCalendarString(Z.toLocalDate(), Do.toLocalDate());
    }
}