package org.tensorflow.lite.examples.classification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity_mknow extends AppCompatActivity {
    private ImageButton ibtn_1,btn_next,btn_sw;
    String TAG = "MyTAG";
    ArrayList<HashMap<String,String>> arrayList;
    ArrayList<HashMap<String,String>> arrayListFilter;
    RecyclerViewAdapter mAdapter;
    RecyclerView recyclerView;
    String sw="",stype="",swstype="";
    String lid="0",swlid="0",klid="0",clid="0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().show();
        setContentView(R.layout.activity_main);
        //
          Intent it=this.getIntent();
          sw=it.getStringExtra("sword");
          stype=it.getStringExtra("stype");
          Log.d("sw:",sw+","+stype);
        //
        ibtn_1 = (ImageButton) findViewById(R.id.imageButton2);
        btn_sw = (ImageButton) findViewById(R.id.imageButton3);
        btn_next = (ImageButton) findViewById(R.id.imageButtonnext);
        recyclerView = findViewById(R.id.recyclerView);
        //
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        //
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                catchData();
            }
        });
        //
        btn_sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(swstype.equals("")) {
                    swstype = stype;
                    swlid = lid;
                    if (swstype.contains("know")) {
                        stype = "clinic";
                        lid = "0";
                        catchData();
                    } else {
                        stype = "know";
                        lid = "0";
                        catchData();
                    }
                }
                else
                {
                    if (stype.contains("know")) {
                        klid=lid;
                        stype = "clinic";
                        lid = clid;
                        catchData();
                    } else {
                        clid=lid;
                        stype = "know";
                        lid = klid;
                        catchData();
                    }
                }
            }
        });
        //
        ibtn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(MainActivity_mknow.this,"回到首頁",Toast.LENGTH_LONG).show();
               Intent intent = new Intent(MainActivity_mknow.this,ClassifierActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //
        catchData();
        //catchData2();
        //



    }
//

//抓取api資料
public void catchData(){
    String catchData1 = "https://192.192.140.250/gethot30news.php?"; //蛀牙";//+sw; //10.0.2.2已為本地端不需要更改 只須改後面路徑
    String catchData2 = "https://192.192.140.250/gethot30clinic.php?"; //蛀牙";//+sw; //10.0.2.2已為本地端不需要更改 只須改後面路徑
    String catchData = ""; //蛀牙";//+sw; //10.0.2.2已為本地端不需要更改 只須改後面路徑
    String wurl="";
    if(stype.contains("know"))
        catchData =catchData1;
    else
        catchData =catchData2;

    arrayList = new ArrayList<>();
    wurl =catchData+"id="+lid+"&hword="+sw;
    Log.d(TAG, "url1: "+wurl);
    //wurl =catchData+"蛀牙";
    Log.d(TAG, "url2: "+wurl);
    String urls=wurl;
    ProgressDialog dialog = ProgressDialog.show(this,"讀取中"
            ,"請稍候",true);
    new Thread(()->{
        try {
            URL url = new URL(urls);

            // https
            //URLConnection connection = url.openConnection();
            //
           SSLContext sslctxt = SSLContext.getInstance("TLS"); // 為請求通TLS協議,生成SSLContext物件
            // 初始化SSLContext
           //sslctxt.init(null, new TrustManager[] { new MyX509TrustManager() },
           //         new java.security.SecureRandom());
            sslctxt.init(null, new TrustManager[] { new MyX509TrustManager() }, new java.security.SecureRandom());
            // 獲取SSLSocketFactory物件
            SSLSocketFactory ssf = sslctxt.getSocketFactory();

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            //HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //connection.setReadTimeout(5000);
            //設置連接超時時間5000即5000毫秒=5秒鐘
            connection.setSSLSocketFactory(ssf);
            connection.setRequestMethod("GET");
            connection.connect();
            InputStream is = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String line = in.readLine();
            StringBuffer json = new StringBuffer();
            while (line != null) {
                Log.d(TAG, "urlline: "+line);
                json.append(line);
                line = in.readLine();
            }

            JSONArray jsonArray= new JSONArray(String.valueOf(json));
            for (int i =0;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id = jsonObject.getString("id");
                lid=id;
                String tit = jsonObject.getString("tit");
                String datafrom = jsonObject.getString("datafrom");
                String dates = jsonObject.getString("dates");
                String weblink = jsonObject.getString("weblink");

                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("id",id);
                hashMap.put("tit",tit);
                hashMap.put("datafrom", datafrom);
                hashMap.put("dates", dates);
                hashMap.put("weblink", weblink);

                arrayList.add(hashMap);
            }

            Log.d(TAG, "catchData: "+arrayList);

            runOnUiThread(()->{
                dialog.dismiss();

                mAdapter = new RecyclerViewAdapter(arrayList,MainActivity_mknow.this,stype);
                recyclerView.setAdapter(mAdapter);
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }

    }).start();
}
//
    /**初始化Toolbar內SearchView的設置*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        /**SearchView設置，以及輸入內容後的行動*/
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /**調用RecyclerView內的Filter方法*/
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    //
    public class MyX509TrustManager implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {

        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {

        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    }
}