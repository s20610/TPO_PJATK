/**
 *
 *  @author Kotnowski Borys S20610
 *
 */

package zad1;


import com.neovisionaries.i18n.CountryCode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Currency;
import java.util.Locale;

public class Service {
    public String kraj;
    public String countryCode;
    public String currencyCode;
    public String city = "";
    public Service(String kraj) {
        this.kraj = kraj;
        countryCode = CountryCode.findByName(kraj).get(0).name();
        Currency c = Currency.getInstance(new Locale("",countryCode));
        currencyCode = c.getCurrencyCode();
    }

     String getWeather(String miasto) {
        city = miasto;
         String apiKey = "APIKEY";
         String url1 = "http://api.openweathermap.org/data/2.5/weather?q=";
         String url2 = "&appid=";
         String fullURL = url1+miasto+","+countryCode+url2+apiKey;
         return getJSON(fullURL);
     }
    Double getRateFor(String kod_waluty){
        String url1 = "https://api.exchangerate.host/latest?base=";
        String url2 = "&symbols=";
        String fullURL =url1+currencyCode+url2+kod_waluty;
        String json = getJSON(fullURL);
        JSONObject jo = new JSONObject(json);
        JSONObject rates = jo.getJSONObject("rates");
        return rates.getDouble(kod_waluty);
    }
    Double getNBPRate(){
        //http://api.nbp.pl/api/exchangerates/rates/a/USD/?format=json
        if(currencyCode.equals("PLN")){
            return 1.0;
        }else{
            String url = "http://api.nbp.pl/api/exchangerates/rates/a/"+currencyCode+"/?format=json";
            String json = getJSON(url);
            JSONObject jo = new JSONObject(json);
            JSONArray rates = jo.getJSONArray("rates");
            JSONObject mid = rates.getJSONObject(0);
            return mid.optDouble("mid");
        }
    }

    String getJSON(String fullUrl){
        URL url = null;
        try{
            url = new URL(fullUrl);
        }catch (MalformedURLException e){
            System.out.println("upss");
        }
        String json = "";
        assert url != null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))){
            String line;
            while ((line = in.readLine()) != null) json += line;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }
}


//Wien liegt im Westen von Linz
//