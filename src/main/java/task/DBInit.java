package task;

import util.DBUtil;

import java.io.*;
import java.sql.Connection;
import java.sql.Statement;

/**
 * @ClassName DBInit
 * @Description TODO
 * @Author 付小雷
 * @Date 2020/3/215:59
 * @Version1.0
 **/
public class DBInit {
    public static void init(){
        try {
            InputStream is = DBInit.class.getClassLoader().getResourceAsStream("init.sql");
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            String line;
            StringBuilder sb = new StringBuilder();
            while((line=br.readLine())!=null){
                int index = line.indexOf("--");
                if (index!=-1){
                    line=line.substring(0,index);
                }
                sb.append(line);
            }
            String[] sqls = sb.toString().split(";");
            Connection connection = null;
            Statement statement = null;
            try{
                for (String sql: sqls) {
                    connection=DBUtil.getConnection();
                    statement = connection.createStatement();
                    statement.executeUpdate(sql);
                }
            }finally {
                DBUtil.close(connection,statement,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
