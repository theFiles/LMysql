package lmysql.query;

import ljson.ILJson;
import ljson.LJson;
import lmysql.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

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
    public List<ILJson> query(ILJson obj){
        List<ILJson> returnList = new ArrayList<>();
        List<Map> res = execute();
        if(res != null && res.size() > 0) {
            Set<String> keys = res.get(0).keySet();
            Class<? extends ILJson> iLjsonClass = obj.getClass();
            for (Map r : res) {
                try {
                    ILJson nowObj = iLjsonClass.newInstance();
                    nowObj.set(r,keys);
                    returnList.add(nowObj);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
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
