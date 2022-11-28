package com.lightidea.products.letdrive.utils;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lightidea.products.letdrive.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Tools {

    public static final String[] PERMISSION_ALL = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static boolean needRequestPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static String[] getDeniedPermission(Activity act) {
        List<String> permissions = new ArrayList<>();
        if (needRequestPermission()) {
            for (String s : PERMISSION_ALL) {
                int status = act.checkSelfPermission(s);
                if (status != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(s);
                }
            }
        }

        return permissions.toArray(new String[permissions.size()]);
    }

    public static void displayImageCircle(Context ctx, ImageView img, Uri url) {
        try {
            Glide.with(ctx.getApplicationContext()).load(url)
                    .transition(withCrossFade())
                    .apply(RequestOptions.circleCropTransform())
                    .into(img);
        } catch (Exception e) {
            Glide.with(ctx.getApplicationContext()).load(R.drawable.avatar_placeholder_click)
                    .transition(withCrossFade())
                    .apply(RequestOptions.circleCropTransform())
                    .into(img);
        }
    }

    public static void displayImageCircle(Context ctx, ImageView img, String url) {
        try {
            Glide.with(ctx.getApplicationContext()).load(url)
                    .transition(withCrossFade())
                    .apply(RequestOptions.circleCropTransform())
                    .into(img);
        } catch (Exception e) {
            Glide.with(ctx.getApplicationContext()).load(R.drawable.avatar_placeholder_click)
                    .transition(withCrossFade())
                    .apply(RequestOptions.circleCropTransform())
                    .into(img);
        }
    }

    public static Drawable convertDrawableFromUrl(String url) {

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("User-agent", "Mozilla/4.0");

            connection.connect();
            InputStream input = connection.getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return new BitmapDrawable(Resources.getSystem(), bitmap);
        } catch (IOException e) {
            return null;
        }
    }

    public static void hideKeyboard(Activity context) {
        context.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
