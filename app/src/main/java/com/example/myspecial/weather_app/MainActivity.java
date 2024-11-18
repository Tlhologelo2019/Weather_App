package com.example.myspecial.weather_app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    TextView locationName;
    Button search;
    TextView show;
    String url;


    class getWeather extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls){
            StringBuilder result = new StringBuilder();
            try{
                URL url= new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line="";
                while((line = reader.readLine()) != null){
                    result.append(line).append("\n");
                }
                return result.toString();
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main = jsonObject.getJSONObject("main");

                double kelvinTemp = main.getDouble("temp");
                double celsiusTemp = kelvinTemp - 273.15;

                String weatherInfo = "Temperature: " + String.format("%.2f", celsiusTemp) + "°C\n";
                weatherInfo += "Feels Like: " + String.format("%.2f", main.getDouble("feels_like") - 273.15) + "°C\n";
                weatherInfo += "Temperature Max: " + String.format("%.2f", main.getDouble("temp_max") - 273.15) + "°C\n";
                weatherInfo += "Temperature Min: " + String.format("%.2f", main.getDouble("temp_min") - 273.15) + "°C\n";
                weatherInfo += "Pressure: " + main.getInt("pressure") + " hPa\n";
                weatherInfo += "Humidity: " + main.getInt("humidity") + "%";


                show.setText(weatherInfo);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationName = findViewById(R.id.locationName);
        search = findViewById(R.id.search);
        show = findViewById(R.id.weatherDetails);

        final String[] temp={""};

        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(MainActivity.this, "Button Clicked! ", Toast.LENGTH_SHORT).show();
                String location = locationName.getText().toString();
                try{
                    if(location!=null){
                        url = "https://api.openweathermap.org/data/2.5/weather?q=" + location + "&appid={ API KEY }";
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Enter City", Toast.LENGTH_SHORT).show();
                    }
                    getWeather task= new getWeather();
                    temp[0] = task.execute(url).get();
                }
                catch(ExecutionException e){
                    e.printStackTrace();
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
                if(temp[0] == null){
                    show.setText("Cannot able to find Weather");
                }

            }
        });

    }
}
