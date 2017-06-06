package br.edu.ifspsaocarlos.sdm.asynctaskws;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private EditText etUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btAcessarWS = (Button)findViewById(R.id.bt_acessar_ws);
        btAcessarWS.setOnClickListener(this);
        mProgress = (ProgressBar)findViewById(R.id.pb_carregando);
        etUrl = (EditText)findViewById(R.id.et_url);
    }

    @Override
    public void onClick(View view) {
        if (view == btAcessarWS){
            buscarData(etUrl.getText().toString());
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
                    } else
                        return null;
                    jsonObject = new JSONObject(sb.toString());

                } catch (IOException e) {
                    Log.e("SDM", "Erro na recuperação do objeto");
                } catch (JSONException e) {
                    Log.e("SDM", "Erro na processamento do objeto JSON");
                }
                return jsonObject;
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                String json;
                super.onPostExecute(jsonObject);
                if (jsonObject != null) {
                    json = jsonObject.toString();
                    ((TextView) findViewById(R.id.tv_data)).setText(json);
                }
                else {
                    Toast.makeText(getBaseContext(), "Erro ao buscar o json", Toast.LENGTH_SHORT).show();
                    ((TextView) findViewById(R.id.tv_data)).setText("");
                }
                mProgress.setVisibility(View.GONE);
            }
        };
        tarefa.execute(url);
    }

    public static void chamaToast(View v, String mensagem) {
        Toast.makeText(v.getContext(), mensagem, Toast.LENGTH_SHORT).show();
    }
}
