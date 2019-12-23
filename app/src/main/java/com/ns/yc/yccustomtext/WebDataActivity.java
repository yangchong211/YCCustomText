package com.ns.yc.yccustomtext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class WebDataActivity extends AppCompatActivity {


    //自己制造的一些假数据。外加筛选图片样式
    /****  3333333333 ***************************************************/
//    private String dataStr="<html><body><style>img{ width:100% !important;}</style>"+"<img src=\"https://ss0.bdstatic.com/-0U0bnSm1A5BphGlnYG/tam-ogel/dd9d1d686cdc814db9653b254e00402e_259_194.jpg\" alt=\"\" /> \r<p style=\"text-align:right;\">\r\t品类定位的思考\r</p>\r<h3>\r\t<strong><span style=\"color:#00D5FF;\">品类定</span></strong>\n" +
//            "<h3>\r\t<a href='JavaScript:android.returnAndroid(\"要返回给APP的数据\")'>点击我跳回APP</a>"+"</body></html>";


    /***  11111111111  **************************************************************************/
    //这个数据的外层不加两层<html><body>标签，不过在下面一个地方加上一个div和图片样式
//    private String dataStr = "<img src=\"https://ss0.bdstatic.com/-0U0bnSm1A5BphGlnYG/tam-ogel/dd9d1d686cdc814db9653b254e00402e_259_194.jpg\" alt=\"\" /> \r<p style=\"text-align:right;\">\r\t品类定位的思考\r</p>\r<h3>\r\t<strong><span style=\"color:#00D5FF;\">品类定";

    /****  11111111111  *************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_diarys);
        final String dataStr = getIntent().getStringExtra("diarys");
        initWebView(dataStr);
    }

    public void initWebView(String data) {
        WebView mWebView = findViewById(R.id.showdiarys);
        WebSettings settings = mWebView.getSettings();

        //settings.setUseWideViewPort(true);//调整到适合webview的大小，不过尽量不要用，有些手机有问题
        settings.setLoadWithOverviewMode(true);//设置WebView是否使用预览模式加载界面。
        mWebView.setVerticalScrollBarEnabled(false);//不能垂直滑动
        mWebView.setHorizontalScrollBarEnabled(false);//不能水平滑动
        settings.setTextSize(WebSettings.TextSize.NORMAL);//通过设置WebSettings，改变HTML中文字的大小
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
        //设置WebView属性，能够执行Javascript脚本
        mWebView.getSettings().setJavaScriptEnabled(true);//设置js可用
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.addJavascriptInterface(new AndroidJavaScript(getApplication()), "android");//设置js接口
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//支持内容重新布局


/******  22222222  ***********************************************************************/
        data = "</Div><head><style>img{ width:100% !important;}</style></head>" + data;//给图片设置一个样式，宽满屏
/******  2222222222  ***********************************************************************/

        mWebView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
    }

    /**
     * AndroidJavaScript
     * 本地与h5页面交互的js类，这里写成内部类了
     * returnAndroid方法上@JavascriptInterface一定不能漏了
     */
    private class AndroidJavaScript {
        Context mContxt;

        public AndroidJavaScript(Context mContxt) {
            this.mContxt = mContxt;
        }

        @JavascriptInterface
        public void returnAndroid(String name) {//从网页跳回到APP，这个方法已经在上面的HTML中写上了
            if (name.isEmpty() || name.equals("")) {
                return;
            }
            Toast.makeText(getApplication(), name, Toast.LENGTH_SHORT).show();
            //这里写你的操作///////////////////////
            //MainActivity就是一个空页面，不影响
            Intent intent = new Intent(WebDataActivity.this, MainActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);
        }
    }
}
