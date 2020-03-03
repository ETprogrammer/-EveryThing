package util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName Pinyin4jUtil
 * @Description TODO
 * @Author 付小雷
 * @Date 2020/3/212:58
 * @Version1.0
 **/
public class Pinyin4jUtil {
    private static final HanyuPinyinOutputFormat FORMAT = new HanyuPinyinOutputFormat();
    static {
        FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
    }
    private static final String CHINESE_PATTERN = "[\\u4E00-\\u9FA5]";
    public static boolean containsChinese(String str) {
        return str.matches(".*" + CHINESE_PATTERN + ".*");
    }
    public static String[] get(String hanyu) {
        String[] array = new String[2];
        StringBuilder spell = new StringBuilder();
        StringBuilder firstSpell = new StringBuilder();
        for(int i= 0 ; i<hanyu.length();i++){
            try {
                String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(hanyu.charAt(i),FORMAT);
                if(pinyins==null||pinyins.length==0) {
                    spell.append(hanyu.charAt(i));
                    firstSpell.append(hanyu.charAt(i));
                }else{
                    spell.append(pinyins[0]);
                    firstSpell.append(pinyins[0].charAt(0));
                }
            } catch (Exception e) {
                spell.append(hanyu.charAt(i));
                firstSpell.append(hanyu.charAt(i));
            }

        }
        array[0]=spell.toString();
        array[1]=firstSpell.toString();
        return  array;
    }
    public static String [][] get(String hanyu,boolean fullSpell){
        String[][] array = new String[hanyu.length()][];
        for (int i = 0; i <hanyu.length() ; i++) {
            try {
                String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(hanyu.charAt(i),FORMAT);
                if(pinyin==null||pinyin.length==0){
                    array[i]=new String[]{String.valueOf(hanyu.charAt(i))};
                }else{
                    array[i]=Distinct(pinyin,fullSpell);
                }
            } catch (Exception e) {
                array[i]=new String[]{String.valueOf(hanyu.charAt(i))};
            }
        }
        return array;
    }

    private static String[] Distinct(String[] pinyin, boolean fullSpell) {
        Set<String> set = new HashSet<>();
        for (String s : pinyin) {
            if (fullSpell) {
                set.add(s);
            } else {
                set.add(String.valueOf(s.charAt(0)));
            }
        }
        return set.toArray(new String[0]);
    }


}
