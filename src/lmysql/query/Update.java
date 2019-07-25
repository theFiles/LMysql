package lmysql.query;

import ljson.ILJson;
import lmysql.LMysql;

import java.sql.Connection;
import java.util.Map;

public class Update extends LMysql<Update,Integer> {
    private StringBuffer setInfo = new StringBuffer();
    public Update(Connection conn, ILJson obj,String[] updateField){
        setConn(conn);

        Map<String,Object> info = obj.getParam();
        int len = updateField.length;

        for (int i=0; i<len; i++){
            String key = updateField[i];
            Object value = info.get(key);
            if(value != null) set(key,value);
        }
    }
    public Update(Connection conn, String table){
        from(table);
        setConn(conn);
    }
    public Update(Connection conn) {
        setConn(conn);
    }

    public Update set(String key,Object value, boolean custom){
        setInfo.append('`'+key+"` = "+setValue(value,custom)+',');
        return this;
    }
    public Update set(String key,Object value){
        return set(key,value,true);
    }

    private String getSetInfo(){
        return setInfo.substring(0,setInfo.length()-1);
    }

    @Override
    public Update getThis() {
        return this;
    }

    @Override
    public String getSql() {
        return "UPDATE "+getFrom()+" SET "+getSetInfo()+" WHERE "+getWhereAll();
    }

    @Override
    public Integer query() {
        return update();
    }
}
