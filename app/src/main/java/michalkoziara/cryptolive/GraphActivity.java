package michalkoziara.cryptolive;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class GraphActivity extends AppCompatActivity {

    private static final String URL = "https://min-api.cryptocompare.com/data/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        View view = this.getWindow().getDecorView();
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.moneys);
        Bitmap blurredBitmap = BlurBuilder.blur(this, originalBitmap);
        view.setBackground(new BitmapDrawable(getResources(), blurredBitmap));

        String API_KEY = getString(R.string.crypto_api_key);

        WebView webView = findViewById(R.id.webView);
        webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webView.requestFocusFromTouch();
        webView.setVisibility(View.INVISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                ProgressBar progressBar = findViewById(R.id.progressBar);
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                view.setVisibility(View.VISIBLE);
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        AsyncSync sync = new AsyncSync(new AsyncSync.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                ResponseParser responseParser = new ResponseParser(output);

                List<String> dates = responseParser.parsePricesDates();
                List<List<Double>> prices = responseParser.parsePrices();

                if (!dates.isEmpty() && !prices.isEmpty()) {
                    WebView webView = findViewById(R.id.webView);
                    webView.addJavascriptInterface(new WebAppInterface(dates, prices, webView), "Android");
                    webView.loadUrl("file:///android_asset/graph.html");
                } else {
                    Toast.makeText(GraphActivity.this, "Brak danych!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Intent intent = getIntent();
        String sym1 = intent.getStringExtra("sym1");
        String sym2 = intent.getStringExtra("sym2");

        String sym1FullName = intent.getStringExtra("sym1FullName");
        String sym2FullName = intent.getStringExtra("sym2FullName");

        Integer limitValue = intent.getIntExtra("limitValue", 10);

        TextView desc = findViewById(R.id.textDescription);
        desc.setText("Wymiana " + sym1FullName + "\nna " + sym2FullName);

        String dateTime = intent.getStringExtra("dateTime");

        sync.execute(URL + dateTime + "?fsym=" + sym1 + "&tsym=" + sym2 + "&limit=" + limitValue.toString() + "&api_key=" + API_KEY);
    }

}
