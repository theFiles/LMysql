package lmysql.query;

import ljson.ILJson;
import ljson.annotation.Table;
import lmysql.*;

import java.lang.annotation.Annotation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        from((ILJson)obj);
        List<E> returnList = new ArrayList<>();
        List<Map> res = execute();
        if(res != null && res.size() > 0) {
            Class iLjsonClass = obj.getClass();
            for (Map r : res) {
                try {
                    ILJson nowObj = (ILJson)iLjsonClass.newInstance();
                    nowObj.set(r,true);
                    returnList.add((E)nowObj);
                } catch (Exception e) {
                    e.printStackTrace();
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
