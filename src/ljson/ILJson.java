package ljson;

import ljson.annotation.DbField;
import ljson.annotation.Table;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 映射类识别接口
 * @author lidaye
 */
public interface ILJson {

    /**
     * 取表名
     * @return      表名
     */
    default String getTable(){
        // 当前对象类对象
        Class thisCls = this.getClass();
        // 注解Table类对象
        Class<Table> tableCls = Table.class;
        String res = "";

        // 取Table对象的value
        if(thisCls.isAnnotationPresent(tableCls)){
            Table table = (Table)thisCls.getAnnotation(tableCls);
            res = table.value();
        }

        return res;
    }

    /**
     * 取属性约束为PRIMARY的字段值
     * @return      字段值
     */
    default String getPrimary(){
        try {
            return getToDbFieldCst("PRIMARY").toString();
        }
        catch (Exception e){
            return null;
        }
    }

    /**
     * 通过DbField的cst值取属性值
     * @param dbFieldCst    DbField的cst值
     * @return              属性值
     */
    default Object getToDbFieldCst(String dbFieldCst){
        // 取当前类中所有的属性
        Field[] fields = getFields();
        // 取字段注解类
        Class<DbField> dbFieldCls = DbField.class;

        // 找到指定注解名的属性值
        for (Field field : fields) {
            if(dbFieldCst.equals(field.getAnnotation(dbFieldCls).cst().toString())){
                return get(field.getName());
            }
        }

        return null;
    }

    /**
     * 通过DbField的value值取属性值
     * @param dbFieldValue      DbField的value值
     * @return                  属性值
     */
    default Object getToDbFieldValue(String dbFieldValue){
        // 取当前类中所有的属性
        Field[] fields = getFields();
        // 取字段注解类
        Class<DbField> dbFieldCls = DbField.class;

        // 找到指定注解名的属性值
        for (Field field : fields) {
            if(dbFieldValue.equals(field.getAnnotation(dbFieldCls).value())){
                return get(field.getName());
            }
        }

        return null;
    }

    /**
     * 取所有属性的键值
     * @return      所有属性的键值集合
     */
    default Map getParam(){
        // 返回的Map
        Map map = new HashMap();
        // 取当前类中所有的属性
        Field[] declaredFields = getFields();
        // 取字段注解类
        Class<DbField> dbFieldCls = DbField.class;

        // 遍历说有属性
        for (Field f:declaredFields) {
            if (f.isAnnotationPresent(dbFieldCls)) {
                // 过滤PRIMARY
                DbField dbField = f.getAnnotation(dbFieldCls);
                if("PRIMARY".equals(dbField.cst().toString())){continue;}

                map.put(dbField.value(), get(f.getName()));
            }
        }

        return map;
    }

    /**
     * 通过属性名获取属性值
     * @param fieldName     属性名
     * @return              属性值
     */
    default Object get(String fieldName){
        // 通过属性名取get方法名
        String methodName = "get"
                + fieldName.substring(0, 1).toUpperCase()
                + fieldName.substring(1);

        // 属性值
        Object value;
        try {
            // 调用get方法取值
            value = this.getClass().getMethod(methodName).invoke(this);
        } catch (Exception e) {
            value = "error value!";
        }

        return value;
    }

    /**
     * 通过set方法修改对象属性
     * @param fieldName     属性名
     * @param value         属性值
     * @return              true 成功 | false 失败
     */
    default boolean set(String fieldName, Object value){
        // 当前的类的反射对象
        Class thisClass = this.getClass();
        // 取属性值
        try {
            Class<?> type = thisClass.getDeclaredField(fieldName).getType();

            // 拼接方法名
            String methodName = "set"+fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
            Method method = thisClass.getMethod(methodName, type);

            // 赋值null
            if(value == null){
                method.invoke(this,null);
            }
            // 正常复制
            else{
                method.invoke(this,format(type,value.toString()));
            }

            return true;
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return false;
    }

    /**
     * 通过map修改类的内容
     * @param map               map集合
     * @param isAnnotation      map中的key对应注解
     */
    default void set(Map<String,Object> map,boolean isAnnotation){
        // 通过匹配注解返回一个新的map
        // 新的map的key是属性名
        if(isAnnotation){map = annotationToField(map);}

        // 遍历设值值
        for (String k:map.keySet()){
            set(k,map.get(k));
        }
    }

    /**
     * 通过注解转换成字段名
     * @param map   原集合
     * @return      整理后的集合
     */
    default Map<String,Object> annotationToField(Map<String,Object> map){
        HashMap<String,Object> newMap = new HashMap<>();
        Class cls                     = this.getClass();
        Field[] fields                = cls.getDeclaredFields();
        Class<DbField> dbFieldClass   = DbField.class;
        int len                       = fields.length;


        for (int i = 0; i < len; i++) {
            Field field = fields[i];
            if(field.isAnnotationPresent(dbFieldClass)){
                String key = field.getAnnotation(dbFieldClass).value();
                Object value = map.get(key);
                if(value != null) {
                    newMap.put(field.getName(),value);
                }
            }
        }

        return newMap;
    }

    /**
     * 根据规定类型转换值的类型
     * @param type      类型对象
     * @param value     字符串值
     * @return          指定类型的对象
     */
    default Object format(Class<?> type, String value){
        try {
            String typeName = type.getName();
            // 字符串
            if ("java.lang.String".equals(typeName)) {
                return value;
            }
            // 整数
            else if ("int".equals(typeName) || "java.lang.Integer".equals(typeName)) {
                return Integer.parseInt(value);
            }
            // 长整数
            else if ("long".equals(typeName) || "java.lang.Long".equals(typeName)) {
                return Long.parseLong(value);
            }
            // 双浮点数
            else if ("double".equals(typeName) || "java.lang.Double".equals(typeName)) {
                return Double.parseDouble(value);
            }
            // 字节型
            else if ("short".equals(typeName) || "java.lang.Short".equals(typeName)) {
                return Short.parseShort(value);
            }
            // 字节型
            else if ("byte".equals(typeName) || "java.lang.Byte".equals(typeName)) {
                return Byte.parseByte(value);
            }
            // 单浮点数
            else if ("float".equals(typeName) || "java.lang.Float".equals(typeName)) {
                return Float.parseFloat(value);
            }
        }catch (Exception e){

        }

        return null;
    }

    /**
     * 取所有属性对象
     * @return      属性对象数组
     */
    default Field[] getFields(){
        return this.getClass().getDeclaredFields();
    }
}
