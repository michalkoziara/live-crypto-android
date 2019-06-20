package michalkoziara.cryptolive;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class ResponseParser {
    private final String response;

    public ResponseParser(String response) {
        this.response = response;
    }

    public List<List<Double>> parsePrices() {

        List<List<Double>> prices = new ArrayList<List<Double>>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getString("Response").equals("Success")) {
                JSONArray jsonArray = jsonObject.getJSONArray("Data");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject price = jsonArray.getJSONObject(i);

                    List<Double> bundleOfPrices = new ArrayList<Double>();
                    bundleOfPrices.add(price.getDouble("low"));
                    bundleOfPrices.add(price.getDouble("open"));
                    bundleOfPrices.add(price.getDouble("close"));
                    bundleOfPrices.add(price.getDouble("high"));

                    prices.add(bundleOfPrices);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return prices;
    }

    public List<String> parsePricesDates() {
        List<String> dates = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getString("Response").equals("Success")) {
                JSONArray jsonArray = jsonObject.getJSONArray("Data");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject price = jsonArray.getJSONObject(i);
                    String unixTimeStamp = price.getString("time");

                    long dateValue = Long.valueOf(unixTimeStamp)*1000;// its need to be in milisecond
                    Date date = new java.util.Date(dateValue);
                    String formattedDate = new SimpleDateFormat("dd/MM HH:mm").format(date);

                    dates.add(formattedDate);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dates;
    }

    public HashMap<String, String> parseCoinsNames() {
        HashMap<String, String> symbolsToNames = new HashMap<String, String>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getString("Response").equals("Success")) {
                JSONObject jsonDataObject = jsonObject.getJSONObject("Data");

                Iterator<String> keysIterator = jsonDataObject.keys();
                while (keysIterator.hasNext())
                {
                    String key = keysIterator.next();
                    JSONObject currency = jsonDataObject.getJSONObject(key);

                    symbolsToNames.put(
                            currency.getString("FullName"),
                            currency.getString("Symbol"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return symbolsToNames;
    }
}
