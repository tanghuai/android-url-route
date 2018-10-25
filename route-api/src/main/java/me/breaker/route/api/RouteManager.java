package me.breaker.route.api;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class RouteManager {

    private static RouteManager routeManager;

    private static HashMap<String, String> pathConfig;

    public static RouteManager getInstance() {
        if (routeManager == null) {
            routeManager = new RouteManager();
        }

        return routeManager;
    }

    private RouteManager() {
        pathConfig = initPathConfig();
    }

    public void startActivity(Context context, Uri uri) {
        for (String s : pathConfig.keySet()) {
            String path = uri.getPath();
            if (s.equals(path)) {
                try {
                    String className = pathConfig.get(s);
                    if (className != null) {
                        Class clz = Class.forName(className);
                        Intent intent = new Intent(context, clz);
                        intent.setData(uri);
                        context.startActivity(intent);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void startActivity(Context context, String path) {
        try {
            String className = pathConfig.get(path);
            if (className != null) {
                Class clz = Class.forName(className);
                Intent intent = new Intent(context, clz);
                context.startActivity(intent);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void startActivity(Context context, String path, Bundle bundle) {
        try {
            String className = pathConfig.get(path);
            if (className != null) {
                Class clz = Class.forName(className);
                Intent intent = new Intent(context, clz);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void inject(Object object) {
        try {
            String name = object.getClass().getName() + "$$Autowired";
            Class<?> clz = Class.forName(name);
            Method method = clz.getMethod("inject", Object.class);
            method.invoke(clz.newInstance(), object);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> initPathConfig() {
        try {
            Class clz = Class.forName("me.breaker.route.PathConfig$$Route");
            Method method = clz.getMethod("init", null);
            method.invoke(clz.newInstance(), null);
            Field field = clz.getField("mPathMap");
            field.setAccessible(true);
            HashMap<String, String> pathConfig = (HashMap<String, String>) field.get(null);
            return pathConfig;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

}
