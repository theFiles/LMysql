package lmysql;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import ljson.ILJson;
import lmysql.query.*;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

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
    private static List<String> errorList;

    /**
     * 错误提示存储量
     */
    private static final byte ERROR_COUNT = 4;

    /**
     * 数据库连接
     */
    private Connection conn = null;

    /**
     * 事务状态
     */
    private boolean isTransaction = false;

    /**
     * 事务保存点
     */
    private Map<String,Savepoint> savePoint;

    /**
     * 连接池对象
     */
    private static DataSource dataSource;

    /**
     * 配置文件读取
     */
    static {
        InputStream is = Mysql.class.getClassLoader().getResourceAsStream(PROPSPATH);
        props = new Properties();
        try {
            props.load(is);
            dataSource = DruidDataSourceFactory.createDataSource(props);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Mysql(boolean isTransaction){
        this();
        if(isTransaction){begin();}
    }

    public Mysql(){
        this.getConn();
    }

    /**
     * 配置数据库连接
     */
    private boolean getConn(){
        try {
            conn = dataSource.getConnection();
            return true;
        } catch (SQLException e) {
            setErrorList(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 记录该次连接的sql运行错误
     * @param err       sql错误提示
     */
    public static void setErrorList(String err){
        if(errorList == null){errorList = new ArrayList<>();}

        if(errorList.size() > ERROR_COUNT){errorList.remove(0);}

        errorList.add(err);
    }

    /**
     * 获取索引错误提示
     * @return      错误提示,默认5条上限
     */
    public List<String> getErrorList() {
        return errorList;
    }

    /**
     * 执行对象（查询）
     * @param sql           sql语句
     * @param replaceVal    换位符替换值
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
     * 开启事务
     */
    public boolean begin(){
        if(!isTransaction){
            isTransaction = true;
            savePoint = new HashMap<>();
            try {
                conn.setAutoCommit(false);
                return true;
            } catch (SQLException e) {
                setErrorList(e.getMessage());
            }
        }
        return false;
    }


    /**
     * 提交事务
     */
    public boolean commit(){
        // 是否需要关闭
        if(isTransaction){
            try {
                conn.commit();
                return true;
            } catch (SQLException e) {
                setErrorList(e.getMessage());
            }
        }

        return false;
    }

    /**
     * 创建保存点
     * @param savePoint         保存点名称
     * @return                  true 设置成功
     */
    public boolean save(String savePoint){
        if(isTransaction && this.savePoint.get(savePoint) == null){
            try {
                Savepoint savepoint = conn.setSavepoint(savePoint);
                this.savePoint.put(savePoint,savepoint);
                return true;
            } catch (SQLException e) {
                setErrorList(e.getMessage());
            }
        }
        return false;
    }

    /**
     * 事务回滚
     * @param savePoint     保存点名称
     * @return              true 回滚成功
     */
    public boolean back(String savePoint){
        Savepoint point = this.savePoint.get(savePoint);
        // 是否需要关闭
        if(isTransaction && point != null){

            try {
                conn.rollback(point);
                return true;
            } catch (SQLException e) {
                setErrorList(e.getMessage());
            }
        }

        return false;
    }

    public boolean back(){
        if(isTransaction){
            try {
                conn.rollback();
                return true;
            } catch (SQLException e) {
                setErrorList(e.getMessage());
            }
        }

        return false;
    }

    /**
     * 关闭所有对象
     */
    public void close(){
        try {
            commit();

            if(conn != null){conn.close();}



        } catch (SQLException e) {
            setErrorList(e.getMessage());
        }
    }

}
