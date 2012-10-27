package com.eqbridges.dw;

import static java.lang.String.format;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;

/**
 * User: ebridges
 * Date: 10/27/12
 * Time: 5:23 PM
 */
public class SQLGenerator {
    private DateDimensionGenerator dateDimensionGenerator;
    private HolidayListing holidayListing;

    public static void main(String[] args) throws IOException {
        SQLGenerator g = new SQLGenerator();
        g.generateSQL(System.out);
    }

    public SQLGenerator() throws IOException {
        String start = "2000-01-01";
        String end = "2020-01-01";

        this.dateDimensionGenerator = new DateDimensionGenerator(start, end);
        this.holidayListing = new HolidayListing();
    }

    public void generateSQL(OutputStream os) throws IOException {
        generateSQL(new OutputStreamWriter(os));
    }

    public void generateSQL(Writer writer) throws IOException {
        try (BufferedWriter w = new BufferedWriter(writer)) {
            Map<LocalDate, HolidayInfo> holidays = holidayListing.getHolidayList();
            List<DateInfo> dateInfo = dateDimensionGenerator.generate();
            generateDateTableSQL(w, dateInfo, holidays);
            generateHolidayTableSQL(w, holidays);
        }
    }

    private void generateHolidayTableSQL(BufferedWriter w, Map<LocalDate, HolidayInfo> holidayMap) throws IOException {
        String sql = "INSERT INTO t_holidays( date_id, country, name, is_bank_holiday, is_actual, is_observed)" +
                " VALUES ('%s', '%s', '%s', %d, %d, %d);\n";
        for(HolidayInfo holiday : holidayMap.values()) {
            String insert = format(
                sql,
                holiday.getId(),
                holiday.getCountry(),
                holiday.getName(),
                holiday.isBankHoliday(),
                holiday.isActual(),
                holiday.isObserved()
            );
            w.write(insert);
        }
    }

    private void generateDateTableSQL(BufferedWriter w, List<DateInfo> dates, Map<LocalDate, HolidayInfo> holidays) throws IOException {
        String sql = "INSERT INTO t_dates(date_id, year, month_of_year, day_of_month, day_of_year, day_of_week," +
                " quarter, yyyy_mm_dd, yyyy_mm, quarter_name, month_name, day_name, is_weekend)" +
                " VALUES (%s, %s, %s, %s, %s, %s, %s, '%s', '%s', '%s', '%s', '%s', %s);\n";
        for(DateInfo date : dates) {
            String insert = format(
                sql,
                date.getDateId(),
                date.getYear(),
                date.getMonthOfYear(),
                date.getDayOfMonth(),
                date.getDayOfYear(),
                date.getDayOfWeek(),
                date.getQuarter(),
                date.yyyymmdd(),
                date.yyyymm(),
                date.quarterName(),
                date.monthName(),
                date.dayName(),
                date.isWeekend(),
                holidays.containsKey(date.getDate())
            );
            w.write(insert);
        }
    }
}
