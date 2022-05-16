package httpUtils;


import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.example.myfiirstapplication.Main5Activity.cookie;

public class helper {
    //异步请求get
    public  static  void getRequest(String url, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }
//    异步请求post加上实际的user-agent
    public static void postRequest(String url, FormBody.Builder builder,Callback callback,Context context){
        OkHttpClient client=new OkHttpClient();
        FormBody build=builder.build();
        Request request=new Request.Builder()
                .url(url).removeHeader("User-Agent").addHeader("User-Agent",getUserAgent(context))
                //以后每次请求头每次都要加上静态全局变量Cookie JSESSIONID=XXXXX，{key，value}形式 服务器端拿到根据value值判断是否为新的会话
                .addHeader("Cookie","JSESSIONID="+cookie)
                .post(build)
                .build();
        client.newCall(request).enqueue(callback);
    }
//获得实际user-agent的方法
    private static String getUserAgent(Context context) {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuilder sb = new StringBuilder();
        assert userAgent != null;
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

}
