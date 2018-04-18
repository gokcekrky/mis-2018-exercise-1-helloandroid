package com.example.mis.helloandroid;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity {

    //class variables
    EditText inputField;
    Button searchBtn;
    Button renderBtn;
    TextView txtdisplay;
    WebView webdisplay;
    String errormsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //import view element references
        inputField = findViewById(R.id.searchInput);
        searchBtn = findViewById(R.id.searchBtn);
        renderBtn = findViewById(R.id.renderBtn);
        txtdisplay = findViewById(R.id.textDisplay);
        webdisplay = findViewById(R.id.webDisplay);

        //instantiate WebViewClient to keep Android from opening URL in system browser
        webdisplay.setWebViewClient(new WebViewClient());
        //disable render button to provide streamlined loading process
        renderBtn.setEnabled(false);

        /**
         * Define onclick methods
         */
        searchBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        URL url = StringToURL(inputField.getText().toString());

                        //don't run background task if url seems invalid
                        if (url != null) {
                            new getURLContent().execute(url);

                            //toggle visibility of different views
                            webdisplay.setVisibility(View.INVISIBLE);
                            txtdisplay.setVisibility(View.VISIBLE);

                            //enable render button if url was successfully loaded
                            renderBtn.setEnabled(true);
                        }
                    }
                }
        );


        renderBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //load content into webview by string url
                        webdisplay.loadUrl(inputField.getText().toString());
                        //toggle visibility of different views
                        webdisplay.setVisibility(View.VISIBLE);
                        txtdisplay.setVisibility(View.INVISIBLE);
                        renderBtn.setEnabled(false);
                    }
                }
        );

    }

    /**
     * Define background task with params, progress and result
     */
    private class getURLContent extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL...url) {
            return getPlaintext(url[0]);
        }

        protected void onPostExecute(String s) {
            if (errormsg != null) {
                Toast.makeText(getApplicationContext(), errormsg, Toast.LENGTH_SHORT).show();
                txtdisplay.setText("");
            } else{
                Toast.makeText(getApplicationContext(), "Loaded plaintext", Toast.LENGTH_SHORT).show();
                txtdisplay.setText(s);
            }
        }
    }

    /**
     * Parse string to url and handle exception
     */
    public URL StringToURL (String text) {
        URL url = null;
        try {
            url = new URL(text);
            return url;
        } catch (MalformedURLException e) {
            Toast.makeText(getApplicationContext(), "Not a valid url", Toast.LENGTH_SHORT).show();
             e.printStackTrace();
        }
        return url;
    }

    /**
     * Load content from url and handle exceptions
     */
    public String getPlaintext(URL url) {

        StringBuilder sb = new StringBuilder();
        errormsg = null;

        try {
            URLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();

        //catch arbitrary exceptions ... e
        } catch (UnknownHostException e) {
            e.printStackTrace();
            errormsg = e.toString();
            return null;
        } catch (ConnectException e) {
            e.printStackTrace();
            errormsg = e.toString();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            errormsg = e.toString();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            errormsg = e.toString();
            return null;
        }

        return sb.toString();
    }

}

