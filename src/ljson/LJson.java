package ljson;


import java.util.List;
import java.util.Map;

/**
 * 转换后的操作类
 * 暂时只提供查询
 */
public class LJson<T> {
    /**
     * json转换来的map对象
     */
    private T obj;

    /**
     * 最后的错误提示
     */
    private String lastError;

    /**
     * 转换失败的构造
     */
    public LJson(String error){
        this.lastError = error;
        this.obj       = null;
    }

    /**
     * 成功转换的构造
     */
    public LJson(T obj) {
        this.obj = obj;
    }

    /**
     * 查看错误
     * @return      错误提示
     */
    public String getLastError() {
        return lastError;
    }

    /**
     * 取整个对象
     */
    public T getObj(){
        return obj;
    }

    /**
     * 获取json对象中的值(多维)
     * @param keys      键名或下标(一个下标进一维)
     * @return          值
     */
    public Object get(Object... keys){
        int lastIndex = keys.length-1; // 保留最后一位用来取值
        Object nowObj = obj; // 当前对象

        for(int i=0; i<lastIndex; i++){
            // 没有下一维
            if(!(obj instanceof Map || obj instanceof List))
                return null;

            // 解析到下一维
            nowObj = getValue(nowObj,keys,i);
        }

        // 取最后一个下标的值
        return getValue(nowObj,keys,lastIndex);
    }

    /**
     * 获取集合中的值
     * @param obj       当前取值的集合
     * @param index     值的下标
     * @return          下标对应的值
     */
    private  Object getValue(Object obj,Object[] keys, int index){
        try{
            return obj instanceof Map
                ? ((Map)obj).get(keys[index])
                : ((List)obj).get((int)keys[index]);
        }
        catch (RuntimeException e){
            return null;
        }
    }

    public String toString(){
        return obj.toString();
    }

}
