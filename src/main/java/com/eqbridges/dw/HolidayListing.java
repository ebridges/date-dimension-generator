package com.eqbridges.dw;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDate;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * User: ebridges
 * Date: 10/27/12
 * Time: 3:33 PM
 */
public class HolidayListing {
    private static final String HOLIDAY_LIST = "/us-holidays_2000-2020.xml";

    public HolidayListing() {
    }

    public Map<LocalDate, HolidayInfo> getHolidayList() throws IOException {
        Map<LocalDate, HolidayInfo> holidayList = new HashMap<>();
        InputStream resource = getClass().getResourceAsStream(HOLIDAY_LIST);
        if(null == resource) {
            throw new FileNotFoundException(HOLIDAY_LIST);
        }

        try(Reader reader = new BufferedReader(new InputStreamReader(resource))) {
            InputSource inputSource = new InputSource(reader);
            XPathFactory  factory= XPathFactory.newInstance();
            XPath xPath=factory.newXPath();
            // xpath tester: http://www.freeformatter.com/xpath-tester.html
//            XPathExpression xPathExpression= xPath.compile("//Holiday[BankHoliday = 'Recognized']");
            XPathExpression xPathExpression= xPath.compile("//Holiday");
            NodeList holidays = (NodeList) xPathExpression.evaluate(inputSource, XPathConstants.NODESET);

            for(int i=0; i<holidays.getLength(); i++) {
                Node holiday = holidays.item(i);
                HolidayInfo holidayInfo = newHolidayInfo(holiday);
                holidayList.put(
                        holidayInfo.getDate(),
                        holidayInfo
                );
            }

        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException(e);
        }
        return holidayList;
    }

    private HolidayInfo newHolidayInfo(Node holiday) {
        String country = getStringValue(holiday, "Country");
        String name = getStringValue(holiday, "Descriptor");
        String date = getStringValue(holiday, "Date").split("T")[0];
        String actualObserved = getStringValue(holiday, "DateType");
        String bankHoliday = getStringValue(holiday, "BankHoliday");

        LocalDate holidayDate = new LocalDate(date);
        int id = getHolidayId(holidayDate);
        String nameEscaped = name.replaceAll("'", "''");
        return new HolidayInfo(
                id,
                country.equals("UnitedStates") ? "US" : country,
                nameEscaped,
                holidayDate,
                actualObserved.contains("Actual"),
                actualObserved.contains("Observed"),
                bankHoliday.equals("Recognized")
        );
    }

    private int getHolidayId(LocalDate holiday) {
        return Util.formatDateId(holiday.getYear(), holiday.getMonthOfYear(), holiday.getDayOfMonth());
    }

    private String getStringValue(Node node, String name) {
        NodeList childs = node.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            Node child = childs.item(i);
            if(trimToEmpty(child.getNodeName()).equals(name)) {
                return trimToEmpty(child.getTextContent());
            }
        }
        return null;
    }

    private String trimToEmpty(String s) {
        if(null != s && s.trim().length()>0) {
            return s.trim();
        } else {
            return "";
        }
    }
}

class HolidayInfo implements Comparable<HolidayInfo> {
    private int id;
    private String country;
    private String name;
    private LocalDate date;
    private boolean actual;
    private boolean observed;
    private boolean bankHoliday;

    HolidayInfo(int id, String country, String name, LocalDate date, boolean actual, boolean observed, boolean bankHoliday) {
        this.id = id;
        this.country = country;
        this.name = name;
        this.date = date;
        this.actual = actual;
        this.observed = observed;
        this.bankHoliday = bankHoliday;
    }

    public int getId() {
        return id;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isActual() {
        return actual;
    }

    public boolean isObserved() {
        return observed;
    }

    public boolean isBankHoliday() {
        return bankHoliday;
    }

    @Override
    public int compareTo(HolidayInfo that) {
        return this.getName().compareToIgnoreCase(that.getName());
    }
}