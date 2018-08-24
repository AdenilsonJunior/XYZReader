package com.adenilson.xyzreader.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat.IntentBuilder;
import android.support.v4.content.Loader;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Palette.PaletteAsyncListener;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adenilson.xyzreader.data.ArticleLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.xyzreader.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ArticleDetailFragment";
    public static final String ARG_ITEM_ID = "item_id";

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.toolbar_article_detail)
    Toolbar mToolbar;
    @BindView(R.id.image_view_photo)
    ImageView mImageViewPhoto;
    @BindView(R.id.fab_share)
    FloatingActionButton mFabShare;
    @BindView(R.id.text_view_info)
    TextView mTextViewInfo;
    @BindView(R.id.text_view_title)
    TextView mTextViewTitle;
    @BindView(R.id.text_view_body)
    TextView mTextViewBody;
    @BindView(R.id.background_bar)
    LinearLayout mBackgroundBar;

    private Cursor mCursor;
    private long mItemId;
    private View mRootView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARG_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);

            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_article_detail, container, false);
        getLoaderManager().initLoader(0, null, this);

        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onStart() {
        super.onStart();
        // animating the share botton
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            mFabShare.setAlpha(0f);
            mFabShare.setScaleX(0f);
            mFabShare.setScaleY(0f);
            mFabShare.setTranslationZ(1f);
            mFabShare.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .translationZ(25f)
                    .setInterpolator(new FastOutSlowInInterpolator())
                    .setStartDelay(300)
                    .start();
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Log.e(TAG, "Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        if (mCursor != null) {
            mRootView.setVisibility(View.VISIBLE);
            final String title = mCursor.getString(ArticleLoader.Query.TITLE);

            mTextViewInfo.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            mCursor.getLong(ArticleLoader.Query.PUBLISHED_DATE),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + " by <font color='#ffffff'>"
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)
                            + "</font>"));
            mTextViewBody.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY)));
            if (mToolbar != null) {
                ((ArticleDetailActivity) getActivity()).setSupportActionBar(mToolbar);
                mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
                ((ArticleDetailActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
                mToolbar.setNavigationOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().finish();
                    }
                });
            }

            mTextViewTitle.setText(title);
            Glide.with(getContext()).asBitmap()
                    .load(mCursor.getString(ArticleLoader.Query.PHOTO_URL))
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(final Bitmap bitmap, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            Palette.from(bitmap).generate(new PaletteAsyncListener() {
                                @Override
                                public void onGenerated(Palette palette) {
                                    int defaultColor = 0xFF333333;
                                    int color = palette.getDarkVibrantColor(defaultColor);
                                    mBackgroundBar.setBackgroundColor(color);
                                    if (mCollapsingToolbarLayout != null) {
                                        int scrimColor = palette.getDarkMutedColor(defaultColor);
                                        mCollapsingToolbarLayout.setStatusBarScrimColor(scrimColor);
                                        mCollapsingToolbarLayout.setContentScrimColor(scrimColor);
                                    }
                                }
                            });
                            mImageViewPhoto.setImageBitmap(bitmap);
                            return true;
                        }
                    }).into(mImageViewPhoto);

            mFabShare.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(Intent.createChooser(IntentBuilder.from(getActivity())
                            .setType("text/plain")
                            .setText(mTextViewBody.getText().toString())
                            .getIntent(), getString(R.string.action_share)));

                }
            });
        } else {
            Snackbar.make(mCoordinatorLayout, R.string.message_error, Snackbar.LENGTH_LONG).show();
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
    }

}
