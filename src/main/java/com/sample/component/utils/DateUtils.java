package com.sample.component.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
public class DateUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    /**
     * @param date Date Type의 파라미터
     * @return LocalDate 타입의 시간을 출력
     */
    public static LocalDate dateToLocalDate(Date date) {

        log.debug("[LocalDateTime] date.toInstant() : {}", date.toInstant());
        log.debug("[LocalDateTime] date.toLocalDate() : {}", date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Date To LocalDateTime (format)
     *
     * @param date   Date Type의 파라미터
     * @param format 출력 시 원하는 포맷
     * @return LocalDateTime 타입의 시간을 Format에 맞게 파싱하여 출력
     */
    public static LocalDateTime dateToLocalDateTimeParse(Date date, String format) {
        return LocalDateTime.parse(
                dateToLocalDate(date).toString(),
                DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public static String dateToFormatChange(Date date, String format) {
        SimpleDateFormat dateFor = new SimpleDateFormat(format);
        return dateFor.format(date);
    }
}
