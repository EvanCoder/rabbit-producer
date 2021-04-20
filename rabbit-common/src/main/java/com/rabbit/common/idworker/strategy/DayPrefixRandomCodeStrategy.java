package com.rabbit.common.idworker.strategy;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Evan
 * @create 2021/3/8 16:23
 */
public class DayPrefixRandomCodeStrategy extends DefaultRandomCodeStrategy {

    private final String dayFormat;

    private String lastDay;

    public DayPrefixRandomCodeStrategy(String dayFormat){
        this.dayFormat = dayFormat;
    }

    @Override
    public void init() {
        String day = createDate();

        if (day.equals(lastDay)){
            throw new RuntimeException("Init failed for day unrolled");
        }

        lastDay = day;
        availableCodes.clear();
        release();

        prefixIndex = Integer.parseInt(lastDay);

        if (tryUserPrefix()){
            return;
        }

        throw new RuntimeException("Prefix is not available " + prefixIndex);
    }

    @Override
    public int next() {
        if (!lastDay.equals(createDate())){
            init();
        }
        return super.next();
    }

    private String createDate(){
        return new SimpleDateFormat(dayFormat).format(new Date());
    }

}
