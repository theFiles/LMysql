import lmysql.Mysql;

import java.util.List;

public class Demo {
    public static void main(String[] args) {
        Mysql mysql = new Mysql();
        P p = new P();
        p.setKind(1);
        p.setPname("测试");
        p.setPon("p9");

        Integer res = mysql.insert(p).query();

        mysql.close();
    }
}
