package br.edu.ifspsaocarlos.sdm.asynctaskws;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity implements View.OnClickListener {
    private Button btAcessarWS;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btAcessarWS = (Button)findViewById(R.id.bt_acessar_ws);
        btAcessarWS.setOnClickListener(this);
        mProgress = (ProgressBar)findViewById(R.id.pb_carregando);
    }

    @Override
    public void onClick(View view) {
        if (view == btAcessarWS){
            buscarTexto("http://192.168.101.64/texto.php");
            buscarData("http://192.168.101.64/data.php");
        }
    }

    private void buscarData(String url) {
        AsyncTask<String, Void, JSONObject> tarefa = new AsyncTask<String, Void, JSONObject>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgress.setVisibility(View.VISIBLE);
            }

            @Override
            protected JSONObject doInBackground(String... strings) {
                JSONObject jsonObject = null;
                StringBuilder sb = new StringBuilder();
                try {
                    HttpURLConnection conn =
                            (HttpURLConnection)(new URL(strings[0])).openConnection();
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        InputStream is = conn.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        String temp;
                        while ((temp = br.readLine()) != null){
                            sb.append(temp);
                        }
                    }
                    jsonObject = new JSONObject(sb.toString());
                    Log.e("Erro JSON", jsonObject.toString());
                } catch (IOException e) {
                    Log.e("SDM", "Erro na recuperação do objeto");
                } catch (JSONException e) {
                    Log.e("SDM", "Erro na processamento do objeto JSON");
                }
                return jsonObject;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                String data = null, hora = null, ds = null;
                super.onPostExecute(jsonObject);
                try {
                    data = jsonObject.getInt("mday") + "/" + jsonObject.getInt("mon") + "/" + jsonObject.getInt("year");
                    hora = jsonObject.getInt("hours") + ":" + jsonObject.getInt("minutes") + ":" + jsonObject.getInt("seconds");
                    ds = jsonObject.getString("weekday");
                } catch (JSONException e) {
                    Log.e("SDM", "Erro na processamento do objeto JSON");
                }
                ((TextView) findViewById(R.id.tv_data)).setText(data + "\n" + hora + "\n" + ds);
                mProgress.setVisibility(View.GONE);
            }
        };
        tarefa.execute(url);
    }

    private void buscarTexto(String url) {
        AsyncTask<String, Void, String> tarefa = new AsyncTask<String, Void, String>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... strings) {
                StringBuilder sb = new StringBuilder();
                try {
                    HttpURLConnection conn =
                            (HttpURLConnection)(new URL(strings[0])).openConnection();
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                        InputStream is = conn.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        String temp;
                        while ((temp = br.readLine()) != null){
                            sb.append(temp);
                        }
                    }
                } catch (IOException ioe) {
                    Log.e("SDM", "Erro na recuperação de texto");
                }
                return sb.toString();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                TextView tv = (TextView)findViewById(R.id.tv_texto);
                tv.setText(s);
            }
        };
        tarefa.execute(url);
    }
}
