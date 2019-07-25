package lmysql.query;

import lmysql.*;

import java.sql.*;
import java.util.List;
import java.util.Map;

public class Select extends LMysql<Select,List<Map>>{

    /**
     * 设置结果集
     */
    private String[] select;

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
        if(selectLen == 0) return "*";

        StringBuffer selSql = new StringBuffer("`"+select[0]+"`");

        for (int i=1; i<selectLen; i++){
            selSql.append(",`"+select[i]+"`");
        }

        return selSql.toString();
    }

    public Select getThis(){
        return this;
    }


    /**
     * 运行sql
     */
    public List<Map> query(){
        return execute();
    }

    /**
     * 取出通过设定参数拼接的sql
     */
    public String getSql(){
        return "SELECT "+getSelect()+" FROM "+getFrom()+" WHERE "+ getWhereAll();
    }

}
