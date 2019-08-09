package lidaye.lmysql.query;

import lidaye.lmysql.LMysql;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 新增操作列
 * @author lidaye
 */
public class Insert extends LMysql<Insert,Integer> {
    private String[] fields;
    private List values = new ArrayList();

    public Insert(Connection conn, Map info){
        int num = 0;
        int len = info.size();
        fields = new String[len];
        Object[] values = new String[len];

        for (Object key:info.keySet()){
            fields[num] = (String) key;
            values[num] = info.get(key).toString();
            ++num;
        }

        setConn(conn);
        value(values);
    }
    public Insert(Connection conn, String[] arr){
        setConn(conn);
        fields = arr;
    }

    /**
     * 设置插入的内容
     */
    public Insert value(Object... values){
        String valueStr = '('+arrayToString(values,"'")+')';
        this.values.add(valueStr);
        return this;
    }

    public String getFields() {
        return '('+arrayToString(fields,"`")+')';
    }

    public String getValues() {
        int len = values.size();
        StringBuffer buff = new StringBuffer();
        if(len > 0){
            buff.append(values.get(0));
            for (int i=1; i<len; i++){
                buff.append(","+values.get(i));
            }
        }
        return buff.toString();
    }

    @Override
    public Insert getThis() {
        return this;
    }

    @Override
    public String getSql() {
        return "INSERT INTO "+getFrom()+getFields()+" VALUES "+getValues();
    }

    @Override
    public Integer query() {
        return update();
    }
}
