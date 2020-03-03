package dao;

import app.FileMeta;
import util.DBUtil;
import util.Pinyin4jUtil;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FileOperatorDAO {

    public synchronized static List<FileMeta> query(String dirPath) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<FileMeta> metas = new ArrayList<>();
        try{
            connection = DBUtil.getConnection();
            String sql = "select name , path, size, last_modified, is_directory" +
                    " from file_meta where path=?";
            statement = connection.prepareStatement(sql);
            statement.setString(1,dirPath);
            resultSet = statement.executeQuery();
            while(resultSet.next()){
                String name = resultSet.getString("name");
                String path = resultSet.getString("path");
                long size = resultSet.getLong("size");
                long last_modified = resultSet.getLong("last_modified");
                boolean is_directory = resultSet.getBoolean("is_directory");
                FileMeta meta = new FileMeta(name,path,size,last_modified,is_directory);
                metas.add(meta);
            }
        }catch (Exception ignored){
            ignored.printStackTrace();
        }finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return metas;
    }

    Object o = new Object();
    public synchronized static void insert(FileMeta localMeta) {

        Connection connection = null;
        PreparedStatement statement = null;
        try{
            try {
                //获取连接
                connection = DBUtil.getConnection();
                String sql = "insert into file_meta" +
                        " (name, path, is_directory," +
                        " pinyin, pinyin_first," +
                        " size, last_modified)" +
                        " values (?,?,?,?,?,?,?)";
                //获取命令
                statement = connection.prepareStatement(sql);
                statement.setString(1,localMeta.getName());
                statement.setString(2,localMeta.getPath());
                statement.setBoolean(3,localMeta.getDirectory());
                String pinyin = null;
                String pinyin_first = null;
                if (Pinyin4jUtil.containsChinese(localMeta.getName())){
                    String[] pinyins = Pinyin4jUtil.get(localMeta.getName());
                    pinyin = pinyins[0];
                    pinyin_first = pinyins[1];
                }
                statement.setString(4,pinyin);
                statement.setString(5,pinyin_first);
                statement.setLong(6,localMeta.getSize());
                statement.setTimestamp(7,
                        new Timestamp(localMeta.getLastModified()));
                //执行sql语句
                System.out.println("insert like:"+localMeta.getPath()+File.separator+localMeta.getName());
                statement.executeUpdate();
            } finally {
                DBUtil.close(connection,statement,null);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void delete(FileMeta meta) {
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            //获取连接
            connection = DBUtil.getConnection();
            connection.setAutoCommit(false);
            String sql = "delete from file_meta where name=?" +
                    " and path=? and is_directory=?";
            //获取命令
            statement = connection.prepareStatement(sql);
            statement.setString(1,meta.getName());
            statement.setString(2,meta.getPath());
            statement.setBoolean(3,meta.getDirectory());
            statement.executeUpdate();

            if(meta.getDirectory()){
                //sql = "delete from file_meta where path like ?";
                sql = "delete from file_meta where path =? or path like ?";
                statement = connection.prepareStatement(sql);
                String path = meta.getPath()+ File.separator+meta.getName();
                statement.setString(1,path);
                statement.setString(2,path+File.separator+"%");
                System.out.println("delete like:"+meta.getPath()+File.separator+meta.getName());
                statement.executeUpdate();
            }
            connection.commit();

        }catch (SQLException e) {
            e.printStackTrace();
            try {
                if(connection!=null){
                    connection.rollback();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            DBUtil.close(connection,statement,null);
        }
    }

    public static List<FileMeta> search(String path, String text) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<FileMeta> metas = new ArrayList<>();
        boolean empty = (path==null)||(path.trim().length()==0);
        try{
            connection = DBUtil.getConnection();
            String sql = "select name , path, size, last_modified, is_directory" +
                    " from file_meta where (name like ? or pinyin like ? or pinyin_first like ?)"
                    +(empty ? "" :"and (path like ?)" );
            statement = connection.prepareStatement(sql);
            statement.setString(1,"%"+text+"%");
            statement.setString(2,"%"+text+"%");
            statement.setString(3,"%"+text+"%");
            if(!empty){
                //statement.setString(4,path);
                statement.setString(4,path+"%");
            }
            resultSet = statement.executeQuery();
            while(resultSet.next()){
                String name = resultSet.getString("name");
                String dirPath = resultSet.getString("path");
                long size = resultSet.getLong("size");
                long last_modified = resultSet.getLong("last_modified");
                boolean is_directory = resultSet.getBoolean("is_directory");
                FileMeta meta = new FileMeta(name,dirPath,size,last_modified,is_directory);
                metas.add(meta);
            }
        }catch (Exception e){

        }finally {
            DBUtil.close(connection,statement,resultSet);
        }
        return metas;
    }
}
