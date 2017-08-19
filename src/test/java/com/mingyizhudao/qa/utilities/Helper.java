package com.mingyizhudao.qa.utilities;

import com.mingyizhudao.qa.common.BaseTest;
import com.mingyizhudao.qa.common.TestLogger;
import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {

    public static String s_ParseJson(JSONObject node, String path) {

        TestLogger logger = new TestLogger(BaseTest.s_JobName());
        if (node == null) return null;
        if (!path.contains(":")) {
            if ( path.indexOf("(")+1 == path.indexOf(")") ) { // 不指定数组坐标
                if (node.getJSONArray(path.substring(0,path.length()-2)).size() >= 0) { //jsonArray不为空
                    logger.info(path.substring(0, path.indexOf("(")) + "的长度为: " + node.getJSONArray(path.substring(0, path.length() - 2)).size());
                    return String.valueOf(node.getJSONArray(path.substring(0,path.length()-2)).size()); //返回数组长度
                } else {
                    return null; //指定的数组key不存在
                }
            } else if ( path.indexOf("(")+1 < path.indexOf(")") ) { // 指定数组坐标
                if (node.getJSONArray(path.substring(0,path.indexOf("("))).size() > 0) {
                    logger.info(path.substring(0, path.indexOf("(")) + "的长度为: " + node.getJSONArray(path.substring(0, path.indexOf("("))));
                    return node.getJSONArray(path.substring(0, path.indexOf("("))).getString(Integer.parseInt(path.substring(path.indexOf("(") + 1, path.indexOf(")")))); //返回指定坐标的内容
                } else {
                    return null; // 指定的数组key不存在，或者长度为0
                }
            } else { // 不是数组
                if (node.containsKey(path)) {
                    return node.getString(path); // 返回值,包括""
                } else {
                    return null; // key不存在
                }
            }
        }

        String nextPath = path.substring(path.indexOf(":")+1);
        String head = path.substring(0,path.indexOf(":"));
        if ( head.indexOf("(")+1 == head.indexOf(")") ) {
            if (node.getJSONArray(head.substring(0,head.indexOf("("))).size() > 0)
                return s_ParseJson(node.getJSONArray(head.substring(0,head.length()-2)).getJSONObject(0),nextPath);
            else
                return null;
        } else if ( head.indexOf("(")+1 < head.indexOf(")") ) {
            if ( node.getJSONArray(head.substring(0,head.indexOf("("))).size() > 0 )
                return s_ParseJson(node.getJSONArray(head.substring(0,path.indexOf("("))).getJSONObject(Integer.parseInt(head.substring(head.indexOf("(")+1,head.indexOf(")")))),nextPath);
            else
                return null;
        } else {
            if (node.containsKey(head)) {
                return s_ParseJson(node.getJSONObject(head), nextPath);
            } else {
                return null;
            }
        }
    }


    /**
     * 对于非unicode的字符串，转换为unicode，用于打印显示
     *
     * @param strArr
     *            非unicode String
     *
     * @return unicode String
     */

    public static String unicodeString( String strArr ) {
        List<String> list	= new ArrayList<String>();
        String		zz	= "\\\\u[0-9,a-z,A-Z]{4}";
		/* 正则表达式用法参考API */
        Pattern pattern = Pattern.compile( zz );
        Matcher m = pattern.matcher( strArr );
        while ( m.find() )
        {
            list.add( m.group() );
        }
        for ( int i = 0, j = 2; i < list.size(); i++ )
        {
            String st = list.get( i ).substring( j, j + 4 );
			/* 将得到的数据按16进制解析为十进制整数，再強转为字符*/
            char ch = (char) Integer.parseInt( st, 16 );
			/* 用得到的字符替换编码表达式 */
            strArr = strArr.replace( list.get( i ), String.valueOf( ch ) );
        }
        return(strArr);
    }

    /**
     * 对于时间的比较
     *
     * @param date1
     *            参与比较的时间1
     * @param date2
     *            参与比较的时间2
     * @param s
     *            时间精确度
     * @return boolean
     *
     *
     */

    public static boolean noLater(String date1, String date2, String s) {
        SimpleDateFormat df = new SimpleDateFormat(s);
        long d1,d2;
        try {
            d1 = df.parse(date1).getTime();
            d2 = df.parse(date2).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return d1<=d2;
    }

    public static boolean noEarlier(String date1, String date2, String s) {
        SimpleDateFormat df = new SimpleDateFormat(s);
        long d1,d2;
        try {
            d1 = df.parse(date1).getTime();
            d2 = df.parse(date2).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return d1>=d2;
    }

    public static boolean almostEqual(String date1, String date2, String s) {
        SimpleDateFormat df = new SimpleDateFormat(s);
        long d1,d2;
        try {
            d1 = df.parse(date1).getTime();
            d2 = df.parse(date2).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return d1==d2 || d1-d2>=1000 || d2-d1<=1000;
    }

    public static void main(String[] args) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        long d1,d2;
        try {
            d1 = df.parse("2017-5-6T1:4:19").getTime();
            d2 = df.parse("2017-5-6T1:4:20").getTime();
            System.out.println(d1);
            System.out.println(d2);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
