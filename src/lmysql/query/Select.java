package lmysql.query;

import ljson.ILJson;
import lmysql.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 查询操作类
 * @author lidaye
 */
public class Select extends LMysql<Select,List<Map>>{
    /**
     * 默认查询结果
     */
    public final String DEFAULT_RES = "*";

    /**
     * 设置结果集
     */
    private String[] select;

    /**
     * 查询字段长度
     */
    private int selectLen;

    /**
     * 设置结果集
     */
    public Select(Connection conn,String[] arr){
        setConn(conn);
        select = arr;
        selectLen = arr.length;
    }

    /**
     * 取结果集sql
     */
    private String getSelect(){
        if(selectLen == 0){return DEFAULT_RES;}

        StringBuffer selSql = new StringBuffer("`"+select[0]+"`");

        for (int i=1; i<selectLen; i++){
            selSql.append(",`"+select[i]+"`");
        }

        return selSql.toString();
    }

    @Override
    public Select getThis(){
        return this;
    }


    /**
     * 运行sql
     */
    @Override
    public List<Map> query(){
        return execute();
    }

    /**
     * 运行结果存到指定类中
     */
    public <E> List<E> query(E obj){
        // 根据对象的注解设值类名
        from((ILJson)obj);
        // 返回的对象集合
        List<E> returnList = new ArrayList<>();
        // 查询结果
        List<Map> res = execute();

        // 查询结果判断
        if(res != null && res.size() > 0) {
            // 反射取对象的类对象
            Class iLjsonClass = obj.getClass();
            // 遍历查询结果
            for (Map r : res) {
                try {
                    // 动态实例化一个新对象
                    ILJson nowObj = (ILJson)iLjsonClass.newInstance();
                    // 给对象赋值
                    nowObj.set(r,true);
                    // 给对象集合赋值
                    returnList.add((E)nowObj);
                } catch (Exception e) {
                    // 不可能会报错
                }
            }

            return returnList;
        }
        return null;
    }

    /**
     * 取出通过设定参数拼接的sql
     */
    @Override
    public String getSql(){
        return "SELECT "+getSelect()+" FROM "+getFrom()+" WHERE "+ getWhereAll();
    }

}
