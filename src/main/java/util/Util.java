package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @ClassName Util
 * @Description TODO
 * @Author 付小雷
 * @Date 2020/3/215:18
 * @Version1.0
 **/
public class Util {
    static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String[] str = {"B","KB","MB","GB","TB"};
    private final static DateFormat DATA_FORMAT = new SimpleDateFormat(DATE_PATTERN);
    public static String parseSize(long size) {
        int i = 0 ;
        while(size>=1024){
            size=size/1024;
            i++;
        }
        return size+str[i];
    }
    public static String parseDate(long lastModified) {
        return DATA_FORMAT.format(new Date(lastModified));
    }
}
