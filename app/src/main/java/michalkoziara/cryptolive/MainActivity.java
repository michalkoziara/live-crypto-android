package michalkoziara.cryptolive;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String URL = "https://min-api.cryptocompare.com/data/all/coinlist";

    private String radioValue;
    private Integer limitValue = 9;
    private HashMap<String, String> currencies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View view = this.getWindow().getDecorView();
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.moneys);
        Bitmap blurredBitmap = BlurBuilder.blur(this, originalBitmap);
        view.setBackground(new BitmapDrawable(getResources(), blurredBitmap));

        String API_KEY = getString(R.string.crypto_api_key);

        AutoCompleteTextView firstCurrencySearch = findViewById(R.id.completeFirstCurrency);
        AutoCompleteTextView secondCurrencySearch = findViewById(R.id.completeSecondCurrency);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        firstCurrencySearch.setVisibility(View.INVISIBLE);
        secondCurrencySearch.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        TextView progressLimit = (TextView) findViewById(R.id.textView2);
        progressLimit.setText(String.format(Locale.getDefault(), "%d", limitValue + 1));
        progressLimit.setVisibility(View.INVISIBLE);

        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setVisibility(View.INVISIBLE);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setVisibility(View.INVISIBLE);

        if (savedInstanceState != null && savedInstanceState.getBoolean("isCurrencySet")) {
            DBManager dbManager = new DBManager(this);
            dbManager.open();

            Cursor cursor = dbManager.fetch();
            currencies = new HashMap<String, String>();

            if (cursor != null) {
                cursor.moveToFirst();

                String fullName = cursor.getString(cursor.getColumnIndex("full_name"));
                String symbol = cursor.getString(cursor.getColumnIndex("symbol"));

                currencies.put(fullName, symbol);

                while (!cursor.isAfterLast()) {
                    fullName = cursor.getString(cursor.getColumnIndex("full_name"));
                    symbol = cursor.getString(cursor.getColumnIndex("symbol"));

                    currencies.put(fullName, symbol);
                    cursor.moveToNext();
                }

                cursor.close();
            }

            dbManager.close();

            String[] fullNames = currencies.keySet().toArray(new String[0]);
            showCurrencySearches(fullNames);

        } else {
            AsyncSync sync = new AsyncSync(new AsyncSync.AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    if (output != null) {
                        ResponseParser responseParser = new ResponseParser(output);

                        currencies = responseParser.parseCoinsNames();
                        currencies.put("Dollar (USD)", "USD");

                        DBManager dbManager = new DBManager(MainActivity.this);
                        dbManager.open();

                        for (Map.Entry<String, String> entry : currencies.entrySet()) {
                            String fullName = entry.getKey();
                            String symbol = entry.getValue();

                            dbManager.insert(fullName, symbol);
                        }
                        dbManager.close();

                        String[] fullNames = currencies.keySet().toArray(new String[0]);
                        showCurrencySearches(fullNames);
                    }
                }
            });

            sync.execute(URL + "?api_key=" + API_KEY);
        }

        firstCurrencySearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
                AutoCompleteTextView secondView = (AutoCompleteTextView) findViewById(R.id.completeSecondCurrency);
                secondView.setFocusableInTouchMode(true);
                secondView.requestFocus();
            }
        });

        secondCurrencySearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
                LinearLayout focuser = (LinearLayout) findViewById(R.id.focusLayout);
                focuser.setFocusableInTouchMode(true);
                focuser.requestFocus();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch (id) {
                    case -1:
                        radioValue = null;
                        break;
                    case R.id.radioButton:
                        radioValue = "histominute";
                        break;
                    case R.id.radioButton2:
                        radioValue = "histohour";
                        break;
                    case R.id.radioButton3:
                        radioValue = "histoday";
                        break;
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView) findViewById(R.id.textView2)).setText(String.format(Locale.getDefault(), "%d", progress + 2));
                limitValue = progress + 1;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Button mainButton = findViewById(R.id.button);
        mainButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext().getApplicationContext(), GraphActivity.class);

                        AutoCompleteTextView firstCurrencySearch = findViewById(R.id.completeFirstCurrency);
                        AutoCompleteTextView secondCurrencySearch = findViewById(R.id.completeSecondCurrency);

                        if (currencies != null) {
                            Set<String> fullNames = currencies.keySet();

                            String selectedSym1 = firstCurrencySearch.getText().toString();
                            String selectedSym2 = secondCurrencySearch.getText().toString();

                            if (fullNames.contains(selectedSym1) &&
                                    fullNames.contains(selectedSym2) &&
                                    radioValue != null &&
                                    (!selectedSym1.equals(selectedSym2))) {

                                intent.putExtra("sym1", currencies.get(selectedSym1));
                                intent.putExtra("sym1FullName", selectedSym1);

                                intent.putExtra("sym2", currencies.get(selectedSym2));
                                intent.putExtra("sym2FullName", selectedSym2);

                                intent.putExtra("limitValue", limitValue);
                                intent.putExtra("dateTime", radioValue);

                                startActivity(intent);
                            } else {
                                String message = "";
                                if (selectedSym1.equals(selectedSym2)) {
                                    message = "Podaj różne waluty!";
                                }
                                if (radioValue == null) {
                                    message = "Zaznacz przedział czasu!";
                                }
                                if (!fullNames.contains(selectedSym1) ||
                                        !fullNames.contains(selectedSym2)) {
                                    message = "Podaj poprawne dane!";
                                }
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (findViewById(R.id.progressBar).getVisibility() != View.VISIBLE) {
            outState.putBoolean("isCurrencySet", true);
        } else {
            outState.putBoolean("isCurrencySet", false);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view != null && view instanceof EditText) {
                Rect r = new Rect();
                view.getGlobalVisibleRect(r);
                int rawX = (int) ev.getRawX();
                int rawY = (int) ev.getRawY();
                if (!r.contains(rawX, rawY)) {
                    view.clearFocus();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void showCurrencySearches(String[] fullNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line, fullNames);

        AutoCompleteTextView firstCurrencySearch = findViewById(R.id.completeFirstCurrency);
        AutoCompleteTextView secondCurrencySearch = findViewById(R.id.completeSecondCurrency);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        firstCurrencySearch.setAdapter(adapter);
        secondCurrencySearch.setAdapter(adapter);

        findViewById(R.id.textView2).setVisibility(View.VISIBLE);
        findViewById(R.id.radioGroup).setVisibility(View.VISIBLE);
        findViewById(R.id.seekBar).setVisibility(View.VISIBLE);
        firstCurrencySearch.setVisibility(View.VISIBLE);
        secondCurrencySearch.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }
}
