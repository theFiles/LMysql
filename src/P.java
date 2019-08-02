import ljson.ILJson;
import ljson.annotation.DbField;
import ljson.annotation.Table;

@Table("P")
public class P implements ILJson {
    @DbField("id")
    private int id;
    @DbField("pno")
    private String pon;
    @DbField("p_name")
    private String pname;
    @DbField("kind")
    private int kind;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPon() {
        return pon;
    }

    public void setPon(String pon) {
        this.pon = pon;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    @Override
    public String toString() {
        return "P{" +
                "id=" + id +
                ", pon='" + pon + '\'' +
                ", pname='" + pname + '\'' +
                ", kind=" + kind +
                '}';
    }
}
