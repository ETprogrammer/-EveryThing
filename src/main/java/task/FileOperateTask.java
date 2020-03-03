package task;

import app.FileMeta;
import dao.FileOperatorDAO;

import util.DBUtil;
import util.Pinyin4jUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileOperateTask  {

    public void execute(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();

            List<FileMeta> localMetas = compose(children);

            List<FileMeta> metas = FileOperatorDAO.query(dir.getPath());
            for (FileMeta meta : metas) {
                if (!localMetas.contains(meta)) {
                    FileOperatorDAO.delete(meta);
                }
            }
            for (FileMeta localMeta : localMetas) {
                if (!metas.contains(localMeta)) {
                    FileOperatorDAO.insert(localMeta);
                }
            }

        }
    }

    private List<FileMeta> compose(File[] children) {
        List<FileMeta> metas = new ArrayList<>();
        if(children!=null){
            for(File child:children){
                //metas.add(new FileMeta(child.getName(),child.getPath(),child.length(),child.lastModified(),child.isDirectory()));
                metas.add(new FileMeta(child));
            }
        }
        return metas;
    }
}

