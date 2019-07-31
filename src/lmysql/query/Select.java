package lmysql.query;

import lmysql.*;

import java.sql.*;
import java.util.List;
import java.util.Map;

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
     * 取出通过设定参数拼接的sql
     */
    @Override
    public String getSql(){
        return "SELECT "+getSelect()+" FROM "+getFrom()+" WHERE "+ getWhereAll();
    }

}
