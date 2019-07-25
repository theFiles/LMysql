package lmysql;

import ljson.ILJson;
import lmysql.query.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 实例化类数据库连接
 */
public class Mysql {
    /**
     * 配置地址
     */
    final private static String PROPSPATH = "lmysql/db.properties";

    /**
     * 配置对象
     */
    private static Properties props;

    /**
     * 错误提示（暂时毛用）
     */
    private static List<String> errorList = new ArrayList<>();

    /**
     * 数据库连接
     */
    private Connection conn = null;

    /**
     * 配置文件读取
     */
    static {
        InputStream is = Mysql.class.getClassLoader().getResourceAsStream(PROPSPATH);
        props = new Properties();
        try {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Mysql(){
        this.getConn();
    }

    /**
     * 配置数据库连接
     */
    private boolean getConn(){
        try {
            Class.forName(props.getProperty("mysql.driver"));
            conn = setConnect();
            return true;
        } catch (ClassNotFoundException e) {
            setErrorList(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static void setErrorList(String err){
        if(errorList.size() > 4)
            errorList.remove(0);

        errorList.add(err);
    }

    /**
     * 连接数据库
     */
    private Connection setConnect(){
        if(conn == null){
            try {
                return DriverManager.getConnection(
                    getConfigString(),
                    props.getProperty("mysql.root"),
                    props.getProperty("mysql.pwd")
                );
            } catch (SQLException e) {
                setErrorList(e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        return conn;
    }

    /**
     * 生成数据库请求
     */
    private String getConfigString(){
        return "jdbc:mysql://"
                +props.getProperty("mysql.host")
                +":"+props.getProperty("mysql.port")
                +"/"+props.getProperty("mysql.dbName")
                +"?characterEncoding="+props.getProperty("mysql.characterEncoding")
                +"&useSSL="+props.getProperty("mysql.useSSL")
                +"&serverTimezone="+props.getProperty("mysql.serverTimezone");
    }

    /**
     * 执行对象（查询）
     */
    public List query(String sql, Object... replaceVal){
        return (List)(new Query(conn,sql,false).setReplaceVal(replaceVal).query());
    }
    public List query(String sql){
        return (List)(new Query(conn,sql,false).query());
    }

    /**
     * 执行对象（修改）
     */
    public int execute(String sql, Object... replaceVal){
        return (int)(new Query(conn,sql,true).setReplaceVal(replaceVal).query());
    }
    public int execute(String sql){
        return (int)(new Query(conn,sql,true).query());
    }

    /**
     * 查 对象
     */
    public Select select(){
        return new Select(conn,new String[0]);
    }
    public Select select(String... field){
        return new Select(conn,field);
    }

    /**
     * 增 对象
     */
    public Insert insert(ILJson obj){
        return insert(obj.getParam());
    }
    public Insert insert(Map info){
        return new Insert(conn,info);
    }
    public Insert insert(String... field){
        return new Insert(conn,field);
    }

    /**
     * 改 对象
     */
    public Update update(ILJson obj,String... updateField){
        return new Update(conn,obj,updateField);
    }
    public Update update(String table){
        return new Update(conn,table);
    }
    public Update update(){
        return new Update(conn);
    }

    /**
     * 删 对象
     */
    public Delete delete(String table){
        return new Delete(conn,table);
    }
    public Delete delete(){
        return new Delete(conn);
    }


    /**
     * 关闭所有对象
     */
    public void close(){
        try {
            if(conn != null)
                conn.close();

        } catch (SQLException e) {
            setErrorList(e.getMessage());
        }
    }

}
