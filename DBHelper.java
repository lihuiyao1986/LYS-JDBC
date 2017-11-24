package com.lys.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jk on 17/11/22.
 */
public class DBHelper {

//    public static final String url = "jdbc:mysql://localhost:3306/app_user?characterEncoding=utf-8";
//    public static final String name = "com.mysql.jdbc.Driver";
//    public static final String user = "root";
//    public static final String password = "123456";

    private String url;
    private String password;
    private String user;
    private final static String name = "com.mysql.jdbc.Driver";

    private Connection conn = null;

    public DBHelper(String url,String pwd,String user){
        this.url = url;
        this.password = pwd;
        this.user = user;
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{

    }

    interface DBHelperCallBack<T>{
        public T query(ResultSet resultSet);
        public void updateBefore(PreparedStatement statement,T item);
        public void updateAfter(PreparedStatement statement,T item,int primaryKey);
    }

    /**
     * 保存
     * @param sql
     * @param item
     * @param callBack
     * @param <T>
     */
    public <T> T save(String sql,T item,DBHelperCallBack callBack){
        ResultSet myRs = null;
        PreparedStatement pst = null;
        Connection conn = null;
        try{
            conn = DriverManager.getConnection(this.url, this.user, this.password);//获取连接
            pst = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);//准备执行语句
            if (callBack !=null){
                callBack.updateBefore(pst, item);
            }
            pst.executeUpdate();
            myRs = pst.getGeneratedKeys();
            int primaryKey = 0;
            if (myRs.next()){
                primaryKey = myRs.getInt(1);
            }
            if (callBack !=null){
                callBack.updateAfter(pst,item,primaryKey);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            try{
                if (myRs!=null){
                    myRs.close();
                }
                if (pst !=null){
                    pst.close();
                }
                if (conn!=null){
                    conn.close();
                }
            }catch (Exception ex){

            }
        }
        return item;
    }

    /**
     * 查询
     * @param sql
     * @param callBack
     */
    public <T> List<T> query(String sql,DBHelperCallBack<T> callBack){
        ResultSet myRs = null;
        PreparedStatement pst = null;
        Connection conn = null;
        List list = new ArrayList();
        try{
            conn = DriverManager.getConnection(this.url, this.user, this.password);//获取连接
            pst = conn.prepareStatement(sql);//准备执行语句
            myRs = pst.executeQuery(sql);
            while (myRs.next()) {
                if (callBack !=null){
                    T result = callBack.query(myRs);
                    if (result!=null){
                        list.add(result);
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            try{
                if (myRs!=null){
                    myRs.close();
                }
                if (pst !=null){
                    pst.close();
                }
                if (conn!=null){
                    conn.close();
                }
            }catch (Exception ex){

            }
        }
        return list;
    }

}
