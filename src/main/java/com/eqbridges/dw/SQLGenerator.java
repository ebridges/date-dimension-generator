package com.eqbridges.dw;

import static java.lang.String.format;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
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
    private String[] dateColumns;
    private DateDimensionGenerator dateDimensionGenerator;
    private HolidayListing holidayListing;
    private String[] viewsNeeded;
    private Writer writer;

    public static void main(String[] args) throws IOException {
        String filename = "date-schema.sql";
        String startDate = "2000-01-01";
        String endDate = "2020-01-01";
        String[] viewsNeeded = new String[] {
          "order",
          "shipping",
          "origination"
        };
        SQLGenerator g = new SQLGenerator(new FileOutputStream(filename), startDate, endDate, viewsNeeded);
        g.generateSQL();
    }

    public SQLGenerator(OutputStream out, String start, String end, String[] viewsNeeded) throws IOException {
        this.dateColumns = new String[] {
                "date_id", "calendar_year", "month_of_year", "day_of_month", "day_of_year", "day_of_week," +
                "quarter", "yyyy_mm_dd", "yyyy_mm", "quarter_name", "month_name", "day_name", "is_weekend"
        };
        this.writer = new OutputStreamWriter(out);
        this.viewsNeeded = viewsNeeded;
        this.dateDimensionGenerator = new DateDimensionGenerator(start, end);
        this.holidayListing = new HolidayListing();
    }

    public void generateSQL() throws IOException {
        try (BufferedWriter w = new BufferedWriter(writer)) {
            Map<LocalDate, HolidayInfo> holidays = holidayListing.getHolidayList();
            List<DateInfo> dateInfo = dateDimensionGenerator.generate();
            generateDateTableSQL(w, dateInfo, holidays);
            generateHolidayTableSQL(w, holidays);
            generateViews(w, viewsNeeded);
        }
    }

    private void generateHolidayTableSQL(BufferedWriter w, Map<LocalDate, HolidayInfo> holidayMap) throws IOException {
        w.write("\n");
        String sql = "INSERT INTO dp_master.t_holidays( date_id, country, name, is_bank_holiday, is_actual, is_observed)" +
                " VALUES (%d, '%s', '%s', %s, %s, %s);\n";
        for(HolidayInfo holiday : holidayMap.values()) {
            String insert = format(
                sql,
                holiday.getId(),
                holiday.getCountry(),
                holiday.getName(),
                toSQLBoolean(holiday.isBankHoliday()),
                toSQLBoolean(holiday.isActual()),
                toSQLBoolean(holiday.isObserved())
            );
            w.write(insert);
        }
    }

    private void generateDateTableSQL(BufferedWriter w, List<DateInfo> dates, Map<LocalDate, HolidayInfo> holidays) throws IOException {
        w.write("\n");
        String sql = "INSERT INTO dp_master.t_dates(%s)" +
                " VALUES (%s, %s, %s, %s, %s, %s, %s, '%s', '%s', '%s', '%s', '%s', %s);\n";
        String columnList = join(dateColumns);
        for(DateInfo date : dates) {
            String insert = format(
                sql,
                columnList,
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
                toSQLBoolean(date.isWeekend())
            );
            w.write(insert);
        }
    }

    private String toSQLBoolean(boolean b) {
        if(b) {
            return "TRUE";
        } else {
            return "FALSE";
        }
    }

    private void generateViews(BufferedWriter w, String[] viewsNeeded) throws IOException {
        w.write("\n");
        for(String viewNeeded : viewsNeeded) {
            generateDateView(w, viewNeeded);
        }
    }

    private void generateDateView(BufferedWriter w, String prefix) throws IOException {
        String sql = "CREATE VIEW %s_date (%s) AS SELECT %s FROM dp_master.tp_dates;\n";
        String viewColumns = joinWithPrefix(prefix+"_", dateColumns);
        String queryColumns = join(dateColumns);
        w.write(format(sql, prefix, viewColumns, queryColumns));
    }

    private static String joinWithPrefix(String prefix, String[] strings) {
        StringBuilder sb = new StringBuilder(strings.length*10);
        for(String s : strings) {
            sb.append(prefix).append(s.trim()).append(',');
        }
        return sb.substring(0, sb.lastIndexOf(","));
    }

    private static String join(String[] strings) {
        return joinWithPrefix("", strings);
    }
}
