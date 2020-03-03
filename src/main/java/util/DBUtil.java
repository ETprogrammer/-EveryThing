package util;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @ClassName DBUtil
 * @Description TODO
 * @Author 付小雷
 * @Date 2020/3/214:23
 * @Version1.0
 **/
public class DBUtil {
    private DBUtil(){

    }
    private static volatile DataSource DATA_SOURCE;

    private static DataSource getDataSource() throws URISyntaxException {
        if(DATA_SOURCE==null){
            synchronized (DBUtil.class){
                if(DATA_SOURCE==null){
                    SQLiteConfig config = new SQLiteConfig();
                    config.setDateStringFormat(Util.DATE_PATTERN);
                    DATA_SOURCE=new SQLiteDataSource();
                    ((SQLiteDataSource)DATA_SOURCE).setUrl(getUrl());
                }
            }
        }
        return DATA_SOURCE;
    }

    public static Connection getConnection(){
        try {
            return getDataSource().getConnection();
        } catch (Exception e) {
            throw new RuntimeException("数据库获取连接失败");
        }
    }
    private static String getUrl() throws URISyntaxException {
        String dbName = "huoyanjinjing.db";
        URL url = DBUtil.class.getClassLoader().getResource(".");
        return "jdbc:sqlite://"+new File (url.toURI()).getParent()+File.separator+dbName;
    }

    public static void close(Connection c , Statement s , ResultSet r){
        try {
            if(r!=null){
                r.close();
            }
            if(s!=null){
                s.close();
            }
            if(c!=null){
                c.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        getConnection();
    }

}
