import lmysql.Mysql;

import java.util.List;
import java.util.Map;

public class Demo {
    public static void main(String[] args) {
        Mysql mysql = new Mysql();

        List<Map> res = mysql
                .select("id","name","sex")
                .from("user")
                .query(1);
        
    }
}
