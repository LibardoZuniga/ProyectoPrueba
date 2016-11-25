package libardozuniga.pruebatecnica;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Locale;
import libardozuniga.pruebatecnica.devazt.networking.HttpClient;
import libardozuniga.pruebatecnica.devazt.networking.OnHttpRequestComplete;
import libardozuniga.pruebatecnica.devazt.networking.Response;

public class MainActivity extends AppCompatActivity {


    Button   boton;
    EditText ciudad;
    TextView CiudadNombre;
    TextView Temperatura;
    TextView Descripcion;
    ImageView ImgClima;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boton       = (Button)   findViewById(R.id.Consultar);
        ciudad      = (EditText) findViewById(R.id.textCiudad);
        CiudadNombre= (TextView) findViewById(R.id.CiudadNombre);
        Temperatura = (TextView) findViewById(R.id.Temperatura);
        Descripcion = (TextView) findViewById(R.id.Descripcion);
        ImgClima    = (ImageView) findViewById(R.id.ImgClima);


        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dato_ciudad = ciudad.getText().toString();
                if (isOnlineNet() != false) {
                    HttpClient client = new HttpClient(new OnHttpRequestComplete() {
                        @TargetApi(Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(Response status) {
                            if (status.isSuccess()) {
                                String resultLogin = status.getResult();


                                JSONObject jsond = null;
                                try {
                                    jsond = new JSONObject(resultLogin);
                                    JSONObject weather = jsond.getJSONArray("weather").getJSONObject(0);
                                    JSONObject main = jsond.getJSONObject("main");
                                    JSONObject sys = jsond.getJSONObject("sys");
                                    String CodJ = jsond.getString("cod").toUpperCase(Locale.US);
                                    int resID = getResources().getIdentifier("image" + weather.getString("icon").toUpperCase(Locale.US).toLowerCase(), "drawable", getPackageName());


                                    CiudadNombre.setText(jsond.getString("name").toUpperCase(Locale.US) + "," + sys.getString("country"));
                                    Descripcion.setText(weather.getString("description").toUpperCase(Locale.US));
                                    Temperatura.setText(main.getString("temp").substring(0, 2) + "°");
                                    ImgClima.setImageResource(resID);


                                    // Toast.makeText(MainActivity.this,  String.format("%.2f", main.getString("temp"))+ " ℃", Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "No se encontro la ciudad!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    client.excecute("http://api.openweathermap.org/data/2.5/weather?q=" + dato_ciudad + "&apikey=75a98e394db1edc40501b08b3edace9f");

                } else {
                    Toast.makeText(MainActivity.this, "Por favor habilitar su conexion a internet!", Toast.LENGTH_LONG).show();

                }
            }
        });
    }
    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}
