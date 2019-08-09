import lidaye.lmysql.Mysql;

import java.util.List;

public class Demo {
    public static void main(String[] args) {
        Mysql mysql = new Mysql();
        P p = new P();
        p.setId(111);
        p.setKind(1);
        p.setPname("测试");
        p.setPon("\"p9\"");

        List<P> res = mysql.select().query(p);
//        Integer res = mysql.update(p).where("id", 111).query();
//        Integer res = mysql.delete(p,"id").query(1);
//        Integer res = mysql.insert(p).query();
        System.out.println(res);

        mysql.close();
    }
}
