package lidaye.lmysql;

import com.sun.istack.internal.NotNull;
import lidaye.ljson.ILJson;
import lidaye.ljson.annotation.Table;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class LMysql<T,R> {
    /**
     * 数据库连接对象
     */
    private Connection conn;

    /**
     * 语句集
     */
    private PreparedStatement db;

    /**
     * 结果集对象
     */
    private ResultSet res = null;

    /**
     * 表名
     */
    private String from = "";

    /**
     * 条件
     */
    private StringBuffer where = new StringBuffer("1");

    /**
     * 换位符对应的值
     */
    private List<String> replaceVal = new ArrayList<>();

    /**
     * limit起始值
     */
    private int limitS = 0;

    /**
     * limit查询量
     */
    private int limitL = 0;

    /**
     * 最后执行的sql
     */
    private String lastSql = "";

    private String groupField = "";

    private String asc = "";
    private String desc = "";

    private int isOrderBy = 0;

    /**
     * 获取继承的对象
     */
    abstract public T getThis();

    /**
     * 获取完整sql
     */
    abstract public String getSql();

    /**
     * 执行sql的命令
     */
    public R query(int len){
        return query(0,len);
    }
    public R query(int index, int len){
        limit(index,len);
        return query();
    }
    public R query(String table,int index,int len){
        from(table);
        return query(index,len);
    }
    public R query(String table,int len){
        from(table);
        return query(len);
    }
    public R query(String table){
        from(table);
        return query();
    }
    abstract public R query();

    /**
     * 执行 增、删、改
     * @return      返回修改条目数
     */
    protected int update(){
        int res = 0;
        try {
            PreparedStatement db = setDb();

            res = db.executeUpdate();

        } catch (SQLException e) {
            Mysql.setErrorList(e.getMessage());
        }

        // 关闭结果集和语句集
        close();

        return res;
    }

    /**
     * 执行查询
     * @return      返回结果
     */
    protected List<Map> execute(){
        List<Map> resList = null;

        try {
            // 预编译语句集
            PreparedStatement db = setDb();

            // 存储获取结果集
            res = db.executeQuery();

            // 返回值 List
            resList = resToList(res);
        } catch (SQLException e) {
            Mysql.setErrorList(e.getMessage());
        }

        // 关闭结果集和语句集
        close();

        return resList;
    }

    public void setConn(Connection conn) {

        this.conn = conn;
    }


    protected PreparedStatement setDb(String sql) throws SQLException {
        db = conn.prepareStatement(sql);
        return db;
    }
    protected PreparedStatement setDb() throws SQLException {

        int len = replaceVal.size();

        String sql = getSql();
        // 保存sql
        lastSql = sql;
        // 预编译
        db = conn.prepareStatement(sql);
        if(len > 0){
            for (int i=0; i<len; i++){
                db.setString(i+1,replaceVal.get(i));
            }
        }

        return db;
    }

    /**
     * 设置表
     */
    public T from(String table){
        from = table;
        return getThis();
    }
    public T from(ILJson obj){
        return from(obj.getTable());
    }
    public T from(Class<? extends ILJson> cls){
        return from(cls.getAnnotation(Table.class).value().toString());
    }

    protected String getFrom(){
        return '`'+from+'`';
    }

    /**
     * 拼接查询条件
     * @param key       字段名
     * @param value     字段值
     * @param compare   逻辑运算符
     * @param join      拼接符
     * @param custom    自定义模式(值)
     * @return          类自身
     */
    public T where(String key,Object value,String compare,String join,boolean custom){
        // 添加查询条件语句
        setWhere(key,setValue(value,custom),compare,join);

        return getThis();
    }
    public T where(String key, Object value, String join){
        return where(key,value,"=",join,true);
    }
    public T where(String key,Object value,boolean custom){
        return where(key,value,"=","AND",custom);
    }
    public T where(String key,Object value){
        return where(key,value,"=","AND",true);
    }

    /**
     * 取where sql
     */
    public String getWhere(){
        return where.toString();
    }

    private void setWhere(String key,String val,String compare,String join){
        where.append(" "+join+" `"+key+"` "+compare+" "+val);
    }

    /**
     * 大于查询
     */
    public T whereGt(String key,Object value){
        return whereGt(key,value,"AND");
    }
    public T whereGt(String key,Object value,boolean custom){
        return whereGt(key,value,"AND",custom);
    }
    public T whereGt(String key,Object value,String join){
        return whereGt(key,value,join,true);
    }
    public T whereGt(String key,Object value,String join,boolean custom){
        return where(key,value,">",join,custom);
    }

    /**
     * 小于查询
     */
    public T whereLt(String key,Object value){
        return whereLt(key,value,"AND");
    }
    public T whereLt(String key,Object value,boolean custom){
        return whereLt(key,value,"AND",custom);
    }
    public T whereLt(String key,Object value,String compare){
        return whereLt(key,value,compare,true);
    }
    public T whereLt(String key,Object value,String join,boolean custom){
        return where(key,value,"<",join,custom);
    }

    /**
     * 模糊查询
     */
    public T whereLike(String key,Object value){
        return whereLike(key,value,"AND");
    }
    public T whereLike(String key,Object value,boolean custom){
        return whereLike(key,value,"AND",custom);
    }
    public T whereLike(String key,Object value,String join){
        return whereLike(key,value,join,true);
    }
    public T whereLike(String key,Object value,String join,boolean custom){
        return where(key,setValue("%"+value+"%",custom),"LIKE",join,false);
    }

    /**
     * in查询
     */
    public T whereIn(String key,Object... value){
        return where(key,'('+setInVal(value)+')',"IN","AND",false);
    }
    public T whereInPre(String key,Object... value){
        return where(key,'('+setInVal(value)+')',"IN","AND",true);
    }

    /**
     * 通过OR拼接的in查询
     */
    public T whereOrIn(String key,Object... value){
        return where(key,'('+setInVal(value)+')',"IN","OR",false);
    }
    public T whereOrInPre(String key,Object... value){
        return where(key,'('+setInVal(value)+')',"IN","OR",true);
    }

    /**
     * 拼接成in语句内容
     */
    private String setInVal(Object[] values){
        int len = values.length;
        StringBuffer inVal = new StringBuffer('\''+values[0].toString()+'\'');
        for (int i=1; i<len; i++){
            inVal.append(",'"+values[i].toString()+'\'');
        }
        return inVal.toString();
    }

    /**
     * between查询
     */
    public T between(String key,Object start,Object end){
        return between(key,start,end,"AND");
    }
    public T between(String key,Object start,Object end,boolean custom){
        return between(key,start,end,"AND",custom);
    }
    public T between(String key,Object start,Object end,String join){
        return between(key,start,end,join,true);
    }
    public T between(String key,Object start,Object end,String join,boolean custom){
        String value = setValue(start,custom) +" AND "+ setValue(end,custom);
        return where(key,value,"BETWEEN",join,false);
    }


    /**
     * 设置limit
     */
    public T limit(int index,int len){
        limitS = index;
        limitL = len;
        return getThis();
    }

    public T limit(int len){
        return limit(0,len);
    }

    protected String getLimit(){
        return limitL > 0
            ? (limitS > 0
                ? " LIMIT "+limitS+','+limitL
                : " LIMIT "+limitL)
            : "";
    }

    /**
     * 设置分组
     */
    public T group(String field){
        groupField = field;
        return getThis();
    }
    public String getGroup(){
        return groupField.isEmpty()
            ? ""
            : " GROUP BY `"+groupField+'`';
    }

    /**
     * 设置排序（升序）
     */
    public T asc(String... field){

        this.asc = setOrderBy(field)+" ASC ";
        isOrderBy = 2;

        return getThis();
    }

    /**
     * 设置排序（降序）
     */
    public T desc(String... field){

        desc = setOrderBy(field)+" DESC ";
        isOrderBy = 1;

        return getThis();
    }

    /**
     * 整理排序sql
     */
    private String setOrderBy(String[] field){

        StringBuffer buff = new StringBuffer('`'+field[0]+'`');
        int len = field.length;

        for (int i=1; i<len; i++){
            buff.append(",`"+field[i]+'`');
        }

        return buff.toString();
    }

    /**
     * 取完整的排序sql
     */
    protected String getOrderBy(){
        if(isOrderBy == 0){return "";}

        return " ORDER BY " + (isOrderBy == 1
                ? asc+desc
                : desc+asc);
    }

    /**
     * 设置代替符?的值
     */
    protected T setReplaceVal(Object[] arr){
        for (Object val:arr){
            replaceVal.add(val.toString());
        }

        return getThis();
    }

    /**
     * 是否要换位符
     */
    protected String setValue(Object value,boolean custom){
        if(value == null){return "null";}

        if(custom){
            // 添加条件占位符替换值
            replaceVal.add(value.toString());

            return "?";
        }

        return value.toString();
    }

    /**
     * 取WHERE往后所有的sql
     */
    protected String getWhereAll(){
        return getWhere()+getGroup()+getOrderBy()+getLimit();
    }

    /**
     * 查看语句
     */
    public String getLastSql() {
        return lastSql;
    }


    /**
     * 关闭数据资源链接
     */
    public void close(){
        try {
            if (res != null){res.close();}

            if (db != null){db.close();}

        } catch (SQLException e) {
            Mysql.setErrorList(e.getMessage());
        }
    }

    protected <E> String arrayToString(@NotNull E[] arr, String flag){

        StringBuffer buff = new StringBuffer();
        int len = arr.length;
        buff.append(flag+arr[0]+flag);
        for (int i=1; i<len; i++){
            buff.append(arr[i]==null
                ? "null"
                : ","+flag+arr[i]+flag
            );
        }

        return buff.toString();
    }

    private List<Map> resToList(ResultSet res) throws SQLException {
        List<Map> list = new ArrayList<>();
        ResultSetMetaData data = res.getMetaData();
        int len = data.getColumnCount();
        while (res.next()){

            Map<String,Object> map = new HashMap<>();
            for (int i=0; i<len; i++){
                String key = data.getColumnName(i+1);
                map.put(key,res.getString(key));
            }

            list.add(map);
        }

        return list;
    }

}
