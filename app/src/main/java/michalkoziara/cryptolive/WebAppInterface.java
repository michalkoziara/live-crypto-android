package michalkoziara.cryptolive;

import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import java.lang.ref.WeakReference;
import java.util.List;

public class WebAppInterface {

    private List<String> dates;
    private List<List<Double>> prices;
    private final WeakReference<WebView> weakWebView;

    WebAppInterface(List<String> dates, List<List<Double>> prices, WebView webView) {
        this.dates = dates;
        this.prices = prices;
        this.weakWebView = new WeakReference<>(webView);
    }

    @JavascriptInterface
    public void loaded(Boolean isLoaded) {
    }

    @JavascriptInterface
    public String getJSONData() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        if (!dates.isEmpty()) {
            List<Double> price = prices.get(0);

            builder.append("{");
            builder.append("\"data\":\"").append(dates.get(0)).append("\",");
            builder.append("\"open\":").append(price.get(0)).append(",");
            builder.append("\"low\":").append(price.get(1)).append(",");
            builder.append("\"high\":").append(price.get(2)).append(",");
            builder.append("\"close\":").append(price.get(3));
            builder.append("}");

            for (int i = 1, n = dates.size(); i < n; i++) {
                price = prices.get(i);

                builder.append(",").append("{");
                builder.append("\"data\":\"").append(dates.get(i)).append("\",");
                builder.append("\"open\":").append(price.get(0)).append(",");
                builder.append("\"low\":").append(price.get(1)).append(",");
                builder.append("\"high\":").append(price.get(2)).append(",");
                builder.append("\"close\":").append(price.get(3));
                builder.append("}");
            }
        }
        builder.append("]");
        Log.d("wynik", builder.toString());

        return builder.toString();
    }
}
