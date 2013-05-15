package com.eqbridges.dw;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public class DateDimensionGenerator {

    private LocalDateRange interval;

    public DateDimensionGenerator(String startTime, String endTime) throws IOException {
        LocalDate start = new LocalDate(startTime);
        LocalDate end = new LocalDate(endTime);
        interval = new LocalDateRange(start, end);
    }

    public List<DateInfo> generate() {
        List<DateInfo> dateList = new ArrayList<>();
        for(LocalDate date : interval) {
            dateList.add(new DateInfo(date));
        }
        return dateList;
    }
}

class DateInfo {
    private static final String[] QUARTERS = {
            "Q0", "Q1", "Q2", "Q3", "Q4"
    };

    private LocalDate date;

    DateInfo(LocalDate date) {
        this.date = date;
    }

    public int getDateId() {
        return Util.formatDateId(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());
    }

    public int getYear() {
       return date.getYear();
    }

    public int getMonthOfYear() {
        return date.getMonthOfYear();
    }

    public int getDayOfMonth() {
        return date.getDayOfMonth();
    }

    public int getDayOfYear() {
        return date.getDayOfYear();
    }

    public int getDayOfWeek() {
        return date.getDayOfWeek();
    }

    public int getQuarter() {
        if(getMonthOfYear() > 0 && getMonthOfYear() < 4) {
            return 1;
        }
        if(getMonthOfYear() < 7) {
            return 2;
        }
        if(getMonthOfYear() < 10) {
            return 3;
        }
        if(getMonthOfYear() < 13) {
            return 4;
        }
        throw new IllegalArgumentException("monthof year must be between 1 & 12");
    }

    public String yyyymm() {
        return DateTimeFormat.forPattern("yyyy-MM").print(date);
    }

    public String yyyymmdd() {
        return DateTimeFormat.forPattern("yyyy-MM-dd").print(date);
    }

    public String quarterName() {
        return QUARTERS[getQuarter()];
    }

    public String dayName() {
        return DateTimeFormat.forPattern("EEEE").print(date);
    }

    public String monthName() {
        return DateTimeFormat.forPattern("MMMM").print(date);
    }

    public boolean isWeekend() {
        return date.getDayOfWeek() > 5;
    }

    public LocalDate getDate() {
        return date;
    }
}

class LocalDateRange implements Iterable<LocalDate>
{
    private final LocalDate start;
    private final LocalDate end;

    public LocalDateRange(LocalDate start,
                          LocalDate end)
    {
        this.start = start;
        this.end = end;
    }

    public Iterator<LocalDate> iterator()
    {
        return new LocalDateRangeIterator(start, end);
    }

    private static class LocalDateRangeIterator implements Iterator<LocalDate>
    {
        private LocalDate current;
        private final LocalDate end;

        private LocalDateRangeIterator(LocalDate start,
                                       LocalDate end)
        {
            this.current = start;
            this.end = end;
        }

        public boolean hasNext()
        {
            return current != null;
        }

        public LocalDate next()
        {
            if (current == null)
            {
                throw new NoSuchElementException();
            }
            LocalDate ret = current;
            current = current.plusDays(1);
            if (current.equals(end) || current.isAfter(end))
            {
                current = null;
            }
            return ret;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
