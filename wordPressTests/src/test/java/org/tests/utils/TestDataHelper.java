package org.tests.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class TestDataHelper {

    public static String generateUniqueTitle() {
        return "Test Post " + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateUniqueContent() {
        return "Test content " + UUID.randomUUID().toString().substring(0, 8);
    }

    public static Date getFutureDate(int daysToAdd) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
        return calendar.getTime();
    }

    public static String formatDateForAPI(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return sdf.format(date);
    }

}
