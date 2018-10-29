package me.breaker.route;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import me.breaker.route.annotation.Route;
import me.breaker.route.annotation.Autowired;
import me.breaker.route.api.RouteManager;


@Route("/page/bubble")
public class BubbleActivity extends AppCompatActivity {

    @Autowired
    String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);
        RouteManager.getInstance().inject(this);
        Toast.makeText(this, "id=" + id, Toast.LENGTH_SHORT).show();
    }

}
