package lmysql.query;

import lmysql.LMysql;

import java.sql.Connection;
import java.util.Map;

/**
 * 修改操作类
 * @author lidaye
 */
public class Update extends LMysql<Update,Integer> {
    private StringBuffer setInfo = new StringBuffer();
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
    public Update set(Map<String, Object> info,String[] updateField){

        int len = updateField.length;

        // 有指定修改内容
        if(len > 0) {
            for (int i = 0; i < len; i++) {
                String key = updateField[i];
                set(key, info.get(key));
            }
        }
        // 全部修改
        else{
            for (String k : info.keySet()) {
                set(k,info.get(k));
            }
        }

        return this;
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
