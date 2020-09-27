package org.jinyuanjava.litemall.wx.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {

	 /**
     * 得到网页中图片的地址
     */
/*    public static Set<String> getImgStr(String htmlStr) {
        Set<String> pics = new HashSet<>();
        String img = "";
        Pattern p_image;
        Matcher m_image;
        //     String regEx_img = "<img.*src=(.*?)[^>]*?>"; //图片链接地址
        String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
        p_image = Pattern.compile
                (regEx_img, Pattern.CASE_INSENSITIVE);
        m_image = p_image.matcher(htmlStr);
        while (m_image.find()) {
            // 得到<img />数据
            img = m_image.group();
            // 匹配<img>中的src数据
            Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
            while (m.find()) {
                pics.add(m.group(1));
            }
        }
        return pics;
    }
    */
    /**
     * 替换指定标签的属性和值
     * @param str 需要处理的字符串
     * @param tag 标签名称
     * @param tagAttrib 要替换的标签属性值
     * @param startTag 新标签开始标记
     * @param endTag  新标签结束标记
     * @return
     */
    public static String replaceHtmlTag(String str, String tag, String tagAttrib, String startTag, String endTag) {
    	//标签 div img等
        String regxpForTag = "<\\s*" + tag + "\\s+([^>]*)\\s*" ;
        //属性 例如src href
        String regxpForTagAttrib = tagAttrib + "=\\s*\"([^\"]+)\"" ;
        Pattern patternForTag = Pattern.compile (regxpForTag,Pattern. CASE_INSENSITIVE );
        Pattern patternForAttrib = Pattern.compile (regxpForTagAttrib,Pattern. CASE_INSENSITIVE );
        //匹配标签
        Matcher matcherForTag = patternForTag.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result = matcherForTag.find();
        while (result) {
            StringBuffer sbreplace = new StringBuffer( "<"+tag+" ");
            Matcher matcherForAttrib = patternForAttrib.matcher(matcherForTag.group(1));
            if (matcherForAttrib.find()) {
                String attributeStr = matcherForAttrib.group(1);
                String replace = attributeStr.replace("..","");
                String[] split = replace.split("/");
                replace = split[split.length-1];
                matcherForAttrib.appendReplacement(sbreplace, startTag + replace + endTag);
            }
            matcherForAttrib.appendTail(sbreplace);
            matcherForTag.appendReplacement(sb, sbreplace.toString());
            result = matcherForTag.find();
        }
        matcherForTag.appendTail(sb);
        return sb.toString();
    }

    public static String addOrReplaceHtmlTag(String str, String tag, String tagAttrib, String styleStr) {
    	//标签 div img等
        String regxpForTag = "<\\s*" + tag + "\\s+([^>]*)\\s*" ;
        //属性 例如src href
        String regxpForTagAttrib = tagAttrib + "=\\s*\"([^\"]+)\"" ;
        Pattern patternForTag = Pattern.compile (regxpForTag,Pattern. CASE_INSENSITIVE );
        Pattern patternForAttrib = Pattern.compile (regxpForTagAttrib,Pattern. CASE_INSENSITIVE );
        //匹配标签
        Matcher matcherForTag = patternForTag.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result = matcherForTag.find();
        while (result) {
            StringBuffer sbreplace = new StringBuffer( "<"+tag+" ");
            Matcher matcherForAttrib = patternForAttrib.matcher(matcherForTag.group(1));
            if (matcherForAttrib.find()) {
                String attributeStr = matcherForAttrib.group(1);
                if(!attributeStr.equals(styleStr)){
                	 int indexOf = attributeStr.indexOf(";");
                     String replace="";
                     if(indexOf>0){
                     	replace+=attributeStr+styleStr+";";
                     }else{
                     	replace+=attributeStr+";"+styleStr+";";
                     }
                     matcherForAttrib.appendReplacement(sbreplace, tagAttrib+"=\"" + replace +"\"");
                }
            }else{
            	sbreplace.append(tagAttrib+"=\""+styleStr+";\" ");
            }
            matcherForAttrib.appendTail(sbreplace);
            matcherForTag.appendReplacement(sb, sbreplace.toString());
            result = matcherForTag.find();
        }
        matcherForTag.appendTail(sb);
        return sb.toString();
    }


    /**
     * 替换CSS引用的其他文件的值
     * @param str 需要处理的字符串
     * @param tag 标签名称
     * @param tagAttrib 要替换的标签属性值
     * @param startTag 新标签开始标记
     * @param endTag  新标签结束标记
     * @return
     */
    public static String replaceCss(String a) {
		String regxpForTag = "url\\(\"(.*)";
		String regxpForTag2 = "\"(.*.css)\"";
		StringBuffer sb = new StringBuffer();
		Pattern patternForTag = Pattern.compile (regxpForTag,Pattern. CASE_INSENSITIVE );
		Pattern patternForTag2 = Pattern.compile (regxpForTag2,Pattern. CASE_INSENSITIVE );
		 Matcher matcherForTag = patternForTag.matcher(a);
		 boolean result = matcherForTag.find();
		 while(result){
			 StringBuffer stringBuffer = new StringBuffer();
			 String group = matcherForTag.group();
			 Matcher matcher = patternForTag2.matcher(group);
			 if(matcher.find()){
				 String group2 = matcher.group();
				 String[] split = group2.replace("\"","").split("/");
				 String string = "\""+split[split.length-1];
				 matcher.appendReplacement(stringBuffer,string+"\"");
				// System.out.println(stringBuffer);
			 }
			 matcher.appendTail(stringBuffer);
	         matcherForTag.appendReplacement(sb, stringBuffer.toString());
	         result = matcherForTag.find();
		 }
		 matcherForTag.appendTail(sb);
		 return sb.toString();
    }


	/**
     * 得到网页中图片的地址
     */
    public static Set<String> getImgStr(String htmlStr) {
        Set<String> pics = new HashSet<String>();
        String img = "";
        Pattern p_image;
        Matcher m_image;
        //     String regEx_img = "<img.*src=(.*?)[^>]*?>"; //图片链接地址
        String regEx_img = "<img.*src\\s*=\\s*(.*?)[^>]*?>";
        p_image = Pattern.compile
                (regEx_img, Pattern.CASE_INSENSITIVE);
        //获得所有img标签
        m_image = p_image.matcher(htmlStr);
        while (m_image.find()) {
            // 得到<img />数据
            img = m_image.group();
            // 匹配<img>中的src数据
            Matcher m = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)").matcher(img);
            while (m.find()) {
                pics.add(m.group(1));
            }
        }
        return pics;
    }


    /**
     * 去除html中的所有标签
     * @param htmlStr
     * @return
     */
    public static String delHTMLTag(String htmlStr){
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式

        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE);
        Matcher m_script=p_script.matcher(htmlStr);
        htmlStr=m_script.replaceAll(""); //过滤script标签

        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE);
        Matcher m_style=p_style.matcher(htmlStr);
        htmlStr=m_style.replaceAll(""); //过滤style标签

        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE);
        Matcher m_html=p_html.matcher(htmlStr);
        htmlStr=m_html.replaceAll(""); //过滤html标签

        return htmlStr.trim(); //返回文本字符串
    }




    public static void main(String[] args) {
        StringBuffer content = new StringBuffer();
        content.append("<ul class=\"imgBox\"><li><img style=\"\" id=\"160424\" src=\"../uploads/allimg/160424/1-160424120T1-50.jpg\" class=\"src_class\"></li>");
        content.append("<li><img id=\"150628\" src=\"uploads/allimg/150628/1-15062Q12247.jpg\" class=\"src_class\"></li></ul>");
        System.out.println("原始字符串为:"+content.toString());
        String newStr = addOrReplaceHtmlTag(content.toString(), "img", "style", "width:100%");
        System.out.println("       替换后为:"+newStr);
    }
}
