package com.adenilson.xyzreader.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adenilson.xyzreader.data.ArticleLoader;
import com.adenilson.xyzreader.data.ItemsContract;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.xyzreader.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleAdapter extends CursorRecyclerViewAdapter<ArticleAdapter.ViewHolder> {

    public ArticleAdapter(Cursor cursor) {
        super(cursor);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final Cursor cursor) {
        final Context context = holder.itemView.getContext();
        holder.titleView.setText(cursor.getString(ArticleLoader.Query.TITLE));
        String thumb = cursor.getString(ArticleLoader.Query.THUMB_URL);
        holder.subtitleView.setText(context.getString(R.string.subtitle_by_author, DateUtils.getRelativeTimeSpanString(
                cursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL).toString(), cursor.getString(ArticleLoader.Query.AUTHOR)));
        Glide.with(context).asBitmap()
                .load(thumb)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        Palette palette = Palette.from(resource).generate();
                        int defaultColor = 0xFF333333;
                        int color = palette.getVibrantColor(defaultColor);
                        holder.mViewBackground.setBackgroundColor(color);
                        holder.mImageViewThumbnail.setImageBitmap(resource);
                        return true;
                    }
                }).into(holder.mImageViewThumbnail);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View views) {
                holder.itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                        ItemsContract.Items.buildItemUri(getItemId(holder.getAdapterPosition()))));
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_article, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_view_thumbnail)
        ImageView mImageViewThumbnail;
        @BindView(R.id.article_title)
        TextView titleView;
        @BindView(R.id.article_subtitle)
        TextView subtitleView;
        @BindView(R.id.view_background)
        View mViewBackground;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}