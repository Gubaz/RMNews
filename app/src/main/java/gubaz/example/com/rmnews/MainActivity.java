package gubaz.example.com.rmnews;



import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // URL Address
    String url = "http://www.madridistebi.com/index.php?do=cat&category=siaxleebi";
    ProgressDialog mProgressDialog;
    List<String> InfoLinks = new ArrayList<String>();
    List<String> InfoImg = new ArrayList<String>();
    List<String> InfoTitle = new ArrayList<String>();
    List<String> FullNews = new ArrayList<String>();
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Start
        // Locate the Buttons in activity_main.xml
        Button titlebutton = (Button) findViewById(R.id.titlebutton);
        Button logobutton = (Button) findViewById(R.id.logobutton);
        listView = (ListView) findViewById(R.id.listView);

        // Capture button click
        titlebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Execute Title AsyncTask
                new Info().execute();
            }
        });


        // Capture button click
        logobutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                // Execute Logo AsyncTask
                new Logo().execute();
            }
        });






        // ADAPTER FOR TEST !!!!!
        //FIRST OF ALL MAKE CUSTOM ADAPTER WITH PICASA LIBRARY IMAGES TO SET IMAGE INTO IMAGEVIEW FROM URL !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, InfoImg);
        listView.setAdapter(itemsAdapter);





    }


        // Title AsyncTask
        private class Info extends AsyncTask<Void, Void, Void> {
            String title;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setTitle("Getting info from www.madridistebi.com");
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // Connect to the web site
                    Document document = Jsoup.connect(url).timeout(1*1000).get();
                    Elements statieLinks = document.select("div[class= imageholder] a[href]");
                    Elements statieImage = document.select("div[class= imageholder] img[src]");
                   // Elements statieTitle = document.select("div[class= imageholder] img[alt]");  // Problem at reading > "" < after seeng " element stops reading and moves to next element

                    //Log.v("Gubaz","Statie Links:"+statieLinks);
                    //Log.v("Gubaz","StatieImageSrc:"+statieImage);
                    //Log.v("Gubaz","StatieTitle:"+statieTitle);
                    int linkCounter =0 , srcCounter=0;
                    for(Element el : statieLinks){
                        InfoLinks.add(el.attr("href"));
                        linkCounter++;
                    }
                    for(Element el : statieImage){
                        InfoImg.add(el.attr("src"));
                        srcCounter++;
                    }
                    Log.v("Gubaz","LinkCounter"+linkCounter);
                    Log.v("Gubaz","LinkCounter"+srcCounter);
                   /* for(Element el : statieTitle){
                        InfoTitle.add(el.attr("alt"));
                    } */
                    Log.v("Gubaz","URLs:"+InfoLinks);
                    Log.v("Gubaz","SRCs:"+InfoImg);
                  //Log.v("Gubaz","Titles:"+InfoTitle);
                    // Get the html document title
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                try{
                    /*
                    * Important Part!!!
                    * inside of this "for" , I'm getting every single news_s separeate link
                    * Than Log every single news from ArrayList in onPostExecute
                    * */
                    for(int i =0;i<=12;i++) {
                        String link = String.valueOf(InfoLinks.get(i));
                        Document document = Jsoup.connect(link).get();
                        Elements el = document.select("div[class=fullnews_content]");
                        //Log.v("Gubaz", "TitleInsideLink:" + link);
                        //Log.v("Gubaz", "TiTleFromLink" + el);
                        FullNews.add(el.text());
                   //     Log.v("Gubaz", "Content" + el.text());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                // Set title into TextView
                TextView txttitle = (TextView) findViewById(R.id.titletxt);
                txttitle.setText(title);
                mProgressDialog.dismiss();
                ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, InfoImg);
                listView.setAdapter(itemsAdapter);

                Log.v("Gubaz", "InfoFromMadridistebi" + FullNews);
            }
        }


        // Logo AsyncTask
        private class Logo extends AsyncTask<Void, Void, Void> {
            Bitmap bitmap;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setTitle("Android Basic JSoup Tutorial");
                mProgressDialog.setMessage("Loading...");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    Document document = Jsoup.connect(url).get();
                    Elements img = document.select("div[class=logo_top] img[src]");
                    String imgSrc = img.attr("src");
                    // imgSrc may not be full path
                    if(!imgSrc.startsWith(url)) {
                        // create full path
                        imgSrc = url+imgSrc;
                    }
                    InputStream input = new URL(imgSrc).openStream();
                    Log.v("GUBAZ","IMAGE SRC:"+imgSrc);
                    bitmap = BitmapFactory.decodeStream(input);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                // Set downloaded image into ImageView
                ImageView logoimg = (ImageView) findViewById(R.id.logo);
                logoimg.setImageBitmap(bitmap);
                mProgressDialog.dismiss();
            }
        }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void checkInternetType() {
        final ConnectivityManager connMgr = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isConnectedOrConnecting ()) {
            Toast.makeText(this, "Wifi", Toast.LENGTH_LONG).show();
        } else if (mobile.isConnectedOrConnecting ()) {
            Toast.makeText(this, "Mobile 3G ", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No Network ", Toast.LENGTH_LONG).show();
        }
    }
    private boolean checkInternetStatus(){
        final ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(wifi.isConnectedOrConnecting() || mobile.isConnectedOrConnecting()){
            return true;
        }
        else{
            return false;
        }
    }

}
