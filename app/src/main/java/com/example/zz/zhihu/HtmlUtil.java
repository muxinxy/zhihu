package com.example.zz.zhihu;

import java.util.List;

public class HtmlUtil {
    // css样式，隐藏header
    private static final String HIDE_HEADER_STYLE = "<style>div.headline{display:none;}</style>";

    // css style tag, 需要格式化
    private static final String NEEDED_FORMAT_CSS_TAG = "<link rel=\"stylesheet\" type=\"text/css\" href=\"%s\"/>";

    public HtmlUtil() {
    }

    /**
     * 根据css链接生成Link标签
     * @param url String
     * @return String
     */
    public static String createCssTag(String url) {
        return String.format(NEEDED_FORMAT_CSS_TAG, url);
    }

    /**
     * 根据多个css链接生成Link标签
     * @param urls List<String>
     * @return String
     */
    public static String createCssTag(List<String> urls) {
        final StringBuilder sb = new StringBuilder();
        for (String url : urls) {
            sb.append(createCssTag(url));
        }
        return sb.toString();
    }

    /**
     * 根据样式标签,html字符串,js标签
     * 生成完整的HTML文档
     */
    public static String createHtmlData(String html, List<String> cssList) {
        final String css = HtmlUtil.createCssTag(cssList);
        return css.concat(HIDE_HEADER_STYLE).concat(html);
    }
}
