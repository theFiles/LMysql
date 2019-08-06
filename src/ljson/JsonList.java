package ljson;


import java.util.ArrayList;
import java.util.List;

/**
 * json转list
 * @author lidaye
 */
public class JsonList extends Json{

    /**
     * json转list
     * @param json  json字符串
     * @param errRep    错误值替换
     * @return      list集合
     */
    public static LJson decode(String json, String errRep){
        LJson obj;

        // 把字符串转成map格式
        try{
            obj = new LJson<List>(getJsonList(json,errRep));
        }
        catch(RuntimeException e){
            obj = new LJson<List>(e.getMessage());
        }

        return obj;
    }

    public static LJson decode(String json){

        return decode(json,"");
    }

    /**
     * list转json
     * @param list  list集合
     * @return      json字符串
     */
    public static String encode(List list){
        String res = getJsonStr(list);
        return res.substring(0,res.length()-1);
    }

    /**
     * 把json字符串转成list
     * @param json      json 字符串
     * @param beCheck   list类型检测开关
     * @return          list
     */
    public static List getJsonList(String json, boolean beCheck,String errRep){
        List list = new ArrayList();
        if(!beCheck || strType(json,4)){
            List<String> arr = split(subStr(json), ',',0);
            int len = arr.size();
            for (int i=0; i<len; i++){
                String value = arr.get(i);
                switch (strType(value)) {
                    // list
                    case 4:
                        list.add(getJsonList(value,false,errRep));
                        break;

                    // module.ljson,递归
                    case 3:
                        list.add(JsonMap.getJsonMap(value, false,errRep));
                        break;

                    // 字符串，去掉双引号赋值
                    case 2:
                        list.add(filterStr(value));
                        break;

                    // 数值，转为整数后赋值
                    case 1:
                        list.add(Integer.valueOf(value));
                        break;

                    // 异常，赋值异常字符串
                    default:
                        list.add(errRep);
                }
            }
        }

        return list;
    }

    public static List getJsonList(String json,String errRep){
        return getJsonList(json,true,errRep);
    }

}
