import lmysql.Mysql;

public class Demo {
    public static void main(String[] args) {
        Mysql mysql = new Mysql();

        int res = mysql
                .update("goodsinfo")
                .set("goods_name","测试1")
                .where("id",7)
                .query(1);


        mysql.close();
    }
}
