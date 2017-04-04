package com.example.chen.ex9;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by Chen on 2016/11/24.
 */

public class MainActivity extends AppCompatActivity {
    private static final String NAMESPACE = "http://WebXml.com.cn/";
    private static final String OPERATION_NAME = "getWeather";
    private static final String SOAP_OPERATION = "http://WebXml.com.cn/getWeather";
    private static final String URL = "http://ws.webxml.com.cn/WebServices/WeatherWS.asmx";
    private String USERID = "d06e3be856bf4f7bbc8d1401d178c5e6";

    private static final int UPDATE_CONTENT = 0;
    private MyAdapter myadapter;
    private WeatherAdapter weatheradapter;
    private Button SearchButton;
    private LinearLayout DetailView;
    private ListView Indicator;
    private RecyclerView Future;
    private EditText CityNameInput;
    private TextView CityNameShow, UpdateTime, TemperatureShow,
            HumidityShow, AirShow, TemperatureRnageShow, WindShow;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_CONTENT:
                    SoapObject detail = (SoapObject)msg.obj;
                    if (detail.getProperty(0).toString().equals("查询结果为空")) {
                        Toast.makeText(MainActivity.this, "查询结果为空", Toast.LENGTH_SHORT).show();
                        DetailView.setVisibility(View.INVISIBLE);
                    } else if (detail.getProperty(0).toString().equals(
                            "发现错误：用户验证失败。http://www.webxml.com.cn/")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        View Dialogview = LayoutInflater.from(
                                MainActivity.this).inflate(R.layout.input_userid, null);
                        builder.setView(Dialogview);
                        builder.setTitle("UserId已经过期，请输入User Id");
                        final EditText userid = (EditText)Dialogview.findViewById(R.id.userid);
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    USERID = userid.getText().toString();
                                    return;
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                        builder.create();
                        builder.show();
                    } else {
                        ParseDataAndUpdateUI(detail);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void ParseDataAndUpdateUI(SoapObject detail) {
        weatheradapter.ClearWeather();
        myadapter.ResetData();
        DetailView.setVisibility(View.VISIBLE);
        String cityNameShow = detail.getProperty(1).toString();
        CityNameShow.setText(cityNameShow);
        String updateTime = getValue(detail.getProperty(3).toString(), ' ');
        UpdateTime.setText(updateTime+ " 更新");
        String [] details = getDetails(detail.getProperty(4).toString(), 8, '；', 3);
        String temperatureShow, humidity,windDirection, air;
        if (details != null) {
            temperatureShow = getValue(details[0], '：');
            air = getAirValue(detail.getProperty(5).toString());
            humidity = details[2];
            windDirection = getValue(details[1], '：');
            String[] indicators = getIndicators(detail.getProperty(6).toString());
            for (int i = 0; i < indicators.length; i++) {
                myadapter.SetData(i, getValue(indicators[i], '：'));
            }
        } else {
            temperatureShow = "暂无预报";
            humidity = "湿度：暂无预报";
            windDirection = "风向暂无数据";
            air = "空气质量：暂无预报";
        }
        TemperatureShow.setText(temperatureShow);
        HumidityShow.setText(humidity);
        WindShow.setText(windDirection);
        AirShow.setText(air);
        TemperatureRnageShow.setText(detail.getProperty(8).toString());
        GetFutureData(detail);
    }

    private void GetFutureData(SoapObject detail) {
        for (int begin = 7; begin < detail.getPropertyCount(); begin += 5) {
            String date = "", weather_description = " ", temperature = " ";
            String string = detail.getProperty(begin).toString();
            for (int j = 0; j < string.length(); j++) {
                if (string.charAt(j) == ' ') {
                    date = string.substring(0, j);
                    weather_description = string.substring(j+1, string.length());
                    break;
                }
            }
            temperature = detail.getProperty(begin+1).toString();
            weatheradapter.AddWeather(new Weather(date, weather_description, temperature));
        }
    }

    private String[] getIndicators(String s) {
        String[] result = new String[6];
        for (int begin = 0, end = 0, index = 0; end < s.length(); end++) {
            if (s.charAt(end) == '。') {
                result[index] = s.substring(begin, end);
                begin = end+1;
                index++;
            }
        }
        return result;
    }

    private String getAirValue(String s) {
        String result = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '。') {
                result = s.substring(i+1, s.length()-1);
                break;
            }
        }
        return result;
    }

    private String[] getDetails(String s, int beginIndex, char spiliter, int resultSize) {
        if (s.length() == 11)
            return null;
        int begin, end, index;
        if (s.charAt(s.length()-1) == spiliter)
            s = s.substring(0, s.length()-1);
        String[] result = new String[resultSize];
        String message = s.substring(beginIndex, s.length());
        for (begin = 0, end = 0, index = 0; end < message.length(); end++) {
            if (message.charAt(end) == spiliter) {
                result[index] = message.substring(begin, end);
                begin = end+1;
                index++;
            }
        }
        result[index] = message.substring(begin, message.length());
        return result;
    }

    private String getValue(String string, char spiliter) {
        int spiliterposition = 0;
        String result;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == spiliter) {
                spiliterposition = i;
            }
        }
        if (string.charAt(string.length()-1) != '。')
            result = string.substring(spiliterposition+1, string.length());
        else
            result = string.substring(spiliterposition+1, string.length()-1);
        return result;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        CheckPermission();
        FindViews();
        HideDetail();
        SetListernerAndAdapter();
    }

    private void HideDetail() {
        DetailView.setVisibility(View.INVISIBLE);
    }

    private void SetListernerAndAdapter() {
        myadapter = new MyAdapter(this);
        Indicator.setAdapter(myadapter);
        weatheradapter = new WeatherAdapter(this);
        Future.setAdapter(weatheradapter);

        SearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkIsAvaliable()) {
                    if (CityNameInput.getText().toString().equals("")) {
                        Toast.makeText(MainActivity.this, "城市名不能为空", Toast.LENGTH_SHORT).show();
                    } else {
                        String CityName = CityNameInput.getText().toString();
                        SendWebServiceRequest(CityName);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "当前没有可用网络！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean NetworkIsAvaliable() {
        ConnectivityManager manager = (ConnectivityManager)getSystemService((CONNECTIVITY_SERVICE));
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo != null) {
            return networkinfo.isAvailable();
        }
        return false;
    }

    private void SendWebServiceRequest(final String CityName) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                SoapObject request = new SoapObject(NAMESPACE, OPERATION_NAME);
                request.addProperty("theCityCode", CityName);
                request.addProperty("theUserID", USERID);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE transport = new HttpTransportSE(URL);
                try {
                    transport.call(SOAP_OPERATION, envelope);
                    SoapObject bodyIn = (SoapObject)envelope.bodyIn;
                    SoapObject detail = (SoapObject)bodyIn.getProperty("getWeatherResult");
                    Message message = new Message();
                    message.what = UPDATE_CONTENT;
                    message.obj = detail;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "查询失败！", Toast.LENGTH_SHORT).show();
                } catch (XmlPullParserException e) {
                    Toast.makeText(MainActivity.this, "数据解析失败！", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    private void FindViews() {
        SearchButton = (Button)findViewById(R.id.Search);
        DetailView = (LinearLayout)findViewById(R.id.DetailView);
        Indicator = (ListView) findViewById(R.id.Indicator);
        Future = (RecyclerView) findViewById(R.id.Future);
        CityNameInput = (EditText) findViewById(R.id.CityNameInput);
        CityNameShow = (TextView) findViewById(R.id.CityNameShow);
        UpdateTime = (TextView) findViewById(R.id.UpdateTime);
        TemperatureShow = (TextView) findViewById(R.id.TemperatureShow);
        HumidityShow = (TextView) findViewById(R.id.HumidityShow);
        AirShow = (TextView) findViewById(R.id.AirShow);
        TemperatureRnageShow = (TextView) findViewById(R.id.TemperatureRangeShow);
        WindShow = (TextView) findViewById(R.id.WindShow);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        Future.setLayoutManager(manager);
    }

    private void CheckPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 0);
        }
        return;
    }
}
