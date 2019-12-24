package com.ns.yc.yccustomtextlib.utils;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 将富文本转化成html格式工具类
 *     revise: 上传服务器数据：第一种json方式，第二种html方法
 * </pre>
 */
public final class HyperHtmlUtils {


    /**
     * 将字符串格式化成JSON的格式
     * @param strJson                           字符串
     * @return                                  json
     */
    public static String stringToJson(String strJson) {
        // 计数tab的个数
        int tabNum = 0;
        StringBuilder jsonFormat = new StringBuilder();
        int length = strJson.length();
        char last = 0;
        for (int i = 0; i < length; i++) {
            char c = strJson.charAt(i);
            if (c == '{') {
                tabNum++;
                jsonFormat.append(c).append("\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
            }
            else if (c == '}') {
                tabNum--;
                jsonFormat.append("\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
                jsonFormat.append(c);
            }
            else if (c == ',') {
                jsonFormat.append(c).append("\n");
                jsonFormat.append(getSpaceOrTab(tabNum));
            }
            else if (c == ':') {
                jsonFormat.append(c).append(" ");
            } else if (c == '[') {
                tabNum++;
                char next = strJson.charAt(i + 1);
                if (next == ']') {
                    jsonFormat.append(c);
                }
                else {
                    jsonFormat.append(c).append("\n");
                    jsonFormat.append(getSpaceOrTab(tabNum));
                }
            } else if (c == ']') {
                tabNum--;
                if (last == '[') {
                    jsonFormat.append(c);
                }
                else {
                    jsonFormat.append("\n").append(getSpaceOrTab(tabNum)).append(c);
                }
            }
            else {
                jsonFormat.append(c);
            }
            last = c;
        }
        return jsonFormat.toString();
    }

    /**
     * 换行操作
     * @param tabNum                            tabNum
     * @return
     */
    private static String getSpaceOrTab(int tabNum) {
        StringBuilder sbTab = new StringBuilder();
        for (int i = 0; i < tabNum; i++) {
            sbTab.append('\t');
        }
        return sbTab.toString();
    }

    /**
     * 将内容转化为html格式
     * @param content                           内容
     * @return
     */
    public static String stringToHtml(String content){
        return null;
    }

}
