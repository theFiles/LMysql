package ljson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * json操作
 * @author lidaye
 */
public abstract class Json {
    /**
     * 开始忽略符
     * 分割字符串时，避免受到下一维的字符串内容影响
     * 设定可能是下一维内容的开始符
     */
    final private static char[] ignoreS = {'{','['};

    /**
     * 结束忽略符
     * 分割字符串时，避免受到下一维的字符串内容影响
     * 设定可能是下一维内容的结束符
     */
    final private static char[] ignoreE = {'}',']'};

    /**
     * 截取json内容
     * @param json      json字符串
     * @return          内容字符串
     */
    protected static String subStr(String json){
        return json.substring(1,json.length()-1);
    }

    /**
     * json字符串类型检测
     * @param json      json字符串
     * @param ckType    指定比较类型
     * @return          是否与比较类型匹配
     */
    protected static boolean strType(String json,int ckType){
        return strType(json) == ckType;
    }
    /**
     * json字符串类型识别 0.异常 1.数值 2.字符串 3.module.ljson 4.list
     * @param json  json字符串
     * @return      0.异常 1.数值 2.字符串 3.module.ljson 4.list
     */
    protected static byte strType(String json){
        String tempStr = json.trim();
        int  lastIndex = tempStr.length()-1;

        // list格式
        if(
                tempStr.charAt(0) == '['
                        && tempStr.charAt(lastIndex) == ']'
                ){
            return 4;
        }
        // json格式
        else if(
                (tempStr.indexOf("{\"") == 0)
                        && (tempStr.lastIndexOf("}") == lastIndex)
                        && (tempStr.contains(":"))
                ){
            return 3;
        }

        // 字符串
        else if(
                (json.charAt(0) == '"')
                        && (json.charAt(lastIndex) == '"')
                ){
            return 2;
        }

        // 数值
        else if(isInteger(json)){return 1;}

        return 0;
    }

    /**
     * 拆分字符串
     * @param str       要拆分的字符串
     * @param flag      拆分匹配的字符串
     * @param depth     拆分深度
     * @return          字符串形式的list
     */
    protected static List<String> split(String str, char flag, int depth){
        // json字符串长度
        int len          = str.length();
        // 上一个符合条件的位置
        int prevIndex    = 0;
        // 忽略阈值 0是不忽略
        int ignore       = 0;
        // 忽略忽略
        boolean igIgnore = true;
        // 深度计数器
        int depthNum     = 0;
        // list容器
        List<String> resArr    = new ArrayList<>();

        for (int i=0; i<len; i++){
            char nowChar = str.charAt(i);

            // 切换忽略忽略
            if(nowChar == '"'){igIgnore = igIgnore?false:true;}

            if(igIgnore){
                // 开始忽略匹配
                if(charInArray(ignoreS,nowChar)){
                    ++ignore;
                }

                // 结束忽略匹配
                else if(
                    ignore != 0
                    && charInArray(ignoreE,nowChar)
                ){--ignore;}
            }

            // 是否忽略匹配
            // 是否找到符合的字符
            if(ignore == 0 && nowChar == flag){
                resArr.add(str.substring(prevIndex,i));
                prevIndex = i+1;
                ++depthNum;
                if(depthNum == depth){break;}
            }
        }

        resArr.add(str.substring(prevIndex,len));
        return resArr;
    }

    /**
     * 过滤字符串
     * @param str       原字符串
     * @return          过滤后的字符串
     */
    protected static String filterStr(String str){
        return deUnicode(str.substring(1, str.length() - 1).replace("\\\"","\""));
    }

    /**
     * 字符串是不是数字
     * @param str   字符串
     * @return      是/否
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 字符串是否为中文
     * @param str       字符串
     * @return          是/否
     */
    public static boolean isChinese(CharSequence str){
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]+");
        return pattern.matcher(str).matches();
    }

    /**
     * 字符是否包含在字符数组中
     * @param arr       字符数组
     * @param content   字符
     * @return          是/否
     */
    public static boolean charInArray(char[] arr,char content){
        int len = arr.length;
        for(int i=0; i<len; i++){
            boolean res = arr[i] == content;
            if(res) return res;
        }

        return false;
    }

    /**
     * 转json字符串
     * @param obj   数组或值
     * @return      json字符串
     */
    protected static String getJsonStr(Object obj){
        StringBuffer buff = new StringBuffer();

        // 普通自定义对象转为map
        if(obj instanceof ILJson){obj = ((ILJson) obj).getParam(false);}

        // Map
        if(obj instanceof Map){
            Map map = (Map)obj;
            // 开始
            buff.append("{");

            for (Object key:map.keySet()){
                // 键名
                buff.append("\""+key+"\":");
                // 值
                buff.append(getJsonStr(map.get(key)));
            }

            // 去除多余分隔符
            if(buff.length() > 1){buff.deleteCharAt(buff.length()-1);}

            // 结束
            buff.append("}");
        }

        // List
        else if(obj instanceof List){
            List list = (List)obj;
            int len = list.size();
            // 开始
            buff.append("[");
            for (int i=0; i<len; i++){
                buff.append(getJsonStr(list.get(i)));
            }
            // 去除多余分隔符
            buff.deleteCharAt(buff.length()-1);
            // 结束
            buff.append("]");
        }

        // String
        else if(obj instanceof String){
            String value = (String)obj;

            // 中文转unicode
            buff.append("\""+enUniCode(value)+"\"");
        }

        // int或其他
        else{buff.append(obj);}

        // 分隔符
        buff.append(',');

        return buff.toString();
    }

    /**
     * 字符串转unicode
     * @param str   字符串
     * @return      unicode字符串
     */
    public static String enUniCode(String str){
        StringBuffer buff = new StringBuffer();
        int len = str.length();
        for (int i=0; i<len; i++){
            char nowChar = str.charAt(i);
            if(nowChar == '"'){buff.append("\\");}

            buff.append(
                isChinese(((Character)nowChar).toString())
                    ? uniCode(nowChar)
                    : nowChar
            );
        }
        return buff.toString();
    }

    /**
     * 单字符转unicode
     * @param c     字符
     * @return      unicode字符串
     */
    public static String uniCode(char c){
        StringBuffer buff = new StringBuffer("\\u");
        //取出高8位
        int j = (c >>>8);
        String tmp = Integer.toHexString(j);

        if (tmp.length() == 1){buff.append("0");}

        buff.append(tmp);
        //取出低8位
        j = (c & 0xFF);
        tmp = Integer.toHexString(j);

        if (tmp.length() == 1){buff.append("0");}

        buff.append(tmp);

        return buff.toString();
    }

    /**
     * unicode 解码
     * @param unicode       unicode字符串
     * @return              转码后的字符串
     */
    public static String deUnicode(String unicode){
        StringBuilder codeBuff = new StringBuilder(unicode);
        int codeIndex = codeBuff.indexOf("\\u");

        while (codeIndex != -1){
            // 截取 u 后面的值
            String uniNum = codeBuff.substring(codeIndex+2,codeIndex+6);
            // 把值转成字符串 并替换
            codeBuff.replace(codeIndex,codeIndex+6,String.valueOf((char)Integer.parseInt(uniNum,16)));
            // 更新位置为当前下标+1
            codeIndex = codeBuff.indexOf("\\u",codeIndex+1);
        }

        return codeBuff.toString();
    }
}
