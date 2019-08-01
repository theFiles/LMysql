import lmysql.Mysql;

public class Demo {
    public static void main(String[] args) {
        Mysql mysql = new Mysql(true);

        int res = mysql
                .update("goodsinfo")
                .set("goods_name","测试6")
                .where("id",7)
                .query(1);

        mysql.save("a");

        mysql
                .update("goodsinfo")
                .set("goods_name","测试7")
                .where("id",7)
                .query(1);

        mysql.back("a");

        System.out.println(mysql.getErrorList());

        mysql.close();
    }
}
