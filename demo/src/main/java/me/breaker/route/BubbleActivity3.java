package me.breaker.route;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import me.breaker.route.annotation.Route;


@Route("/page/bubble3")
public class BubbleActivity3 extends AppCompatActivity {

    public int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);
    }

}
