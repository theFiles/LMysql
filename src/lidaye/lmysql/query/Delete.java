package lidaye.lmysql.query;

import lidaye.lmysql.LMysql;

import java.sql.Connection;

/**
 * 删除操作类
 * @author lidaye
 */
public class Delete extends LMysql<Delete,Integer> {
    public Delete(Connection conn){
        setConn(conn);
    }

    @Override
    public Delete getThis() {
        return this;
    }

    @Override
    public String getSql() {
        return "DELETE FROM "+getFrom()+" WHERE "+getWhereAll();
    }

    @Override
    public Integer query() {
        return update();
    }
}
