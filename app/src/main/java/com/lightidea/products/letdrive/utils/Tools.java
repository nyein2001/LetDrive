package com.lightidea.products.letdrive.utils;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lightidea.products.letdrive.R;

public class Tools {

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

}
