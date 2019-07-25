package lmysql.query;

import lmysql.LMysql;

import java.sql.Connection;

public class Delete extends LMysql<Delete,Integer> {
    public Delete(Connection conn, String table){
        from(table);
        setConn(conn);
    }
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
