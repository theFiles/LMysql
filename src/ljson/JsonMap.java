package ljson;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * json转Map
 * @author lidaye
 */
public class JsonMap extends Json{

    /**
     * json转map
     * @param json      json字符串
     * @param errRep    错误值替换
     * @return          map集合
     */
    public static LJson decode(String json, String errRep){
        LJson obj;

        // 把字符串转成map格式
        try{
            obj = new LJson<Map>(getJsonMap(json,errRep));
        }
        catch(RuntimeException e){
            obj = new LJson<Map>(e.getMessage());
        }

        return obj;
    }

    public static LJson decode(String json){
        return decode(json,"");
    }

    /**
     * map转json
     * @param map   map集合
     * @return      json字符串
     */
    public static String encode(Map map){
        String res = getJsonStr(map);
        return res.substring(0,res.length()-1);
    }

    /**
     * 把json字符串转成map
     * @param json      json字符串
     * @param beCheck   json字符串进行类型检测开关
     * @return          返回解析后的Map合集
     */
    public static Map getJsonMap(String json,boolean beCheck,String errRep){
        // 结果集
        Map<String,Object> map = new HashMap<>();

        // 检测json类型
        if(!beCheck || strType(json,3)) {

            // 字符串以 , 拆分
            List<String> arr = split(subStr(json), ',',0);
            // 数组长度
            int len = arr.size();

            for (int i = 0; i < len; i++) {
                // 字符串以 : 拆分
                List<String> tmpArr = split(arr.get(i), ':', 2);

                String tmpkey = tmpArr.get(0);
                String key    = tmpkey.substring(1,tmpkey.length()-1);
                String value  = tmpArr.get(1);

                switch (strType(value)) {
                    // list
                    case 4:
                        map.put(key,JsonList.getJsonList(value,false,errRep));
                        break;

                    // module.ljson,递归
                    case 3:
                        map.put(key, getJsonMap(value, false,errRep));
                        break;

                    // 字符串，去掉双引号赋值
                    case 2:
                        map.put(key, filterStr(value));
                        break;

                    // 数值，转为整数后赋值
                    case 1:
                        map.put(key, Integer.valueOf(value));
                        break;

                    // 异常，赋值异常字符串
                    default:
                        map.put(key,errRep);
                }
            }
        }

        return map;
    }

    private static Map getJsonMap(String json, String errRep){
        return getJsonMap(json,true,errRep);
    }

}
