package lmysql.query;

import lmysql.LMysql;

import java.sql.Connection;

/**
 * 特殊操作类
 * @author lidaye
 */
public class Query extends LMysql<Query,Object> {
    private String sql;
    private boolean isUpdate;

    public Query(Connection conn, String sql, boolean isUpdate) {
        setConn(conn);
        this.sql = sql;
        this.isUpdate = isUpdate;
    }

    @Override
    public Query getThis() {
        return this;
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public Object query() {
            return isUpdate ? update() : execute();
    }

}
