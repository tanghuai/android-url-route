package me.breaker.route;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.breaker.route.api.RouteManager;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openBubble(View view) {
        Uri uri = Uri.parse("syrup://happysyrup.com/page/bubble?id=10");
        RouteManager.getInstance().startActivity(this, uri);
        RouteManager.getInstance().startActivity(this,"/page/buble");
        Bundle bundle = new Bundle();
        RouteManager.getInstance().startActivity(this,"/page/buble",bundle);
    }
}
