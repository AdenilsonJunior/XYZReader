package com.adenilson.xyzreader.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.adenilson.xyzreader.data.ArticleLoader;
import com.adenilson.xyzreader.data.UpdaterService;
import com.adenilson.xyzreader.decoration.GridSpaceItemDecoration;
import com.adenilson.xyzreader.util.ConnectionUtil;
import com.example.xyzreader.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity implements OnRefreshListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.list_content)
    CoordinatorLayout mCoordinatorLayout;
    private ArticleAdapter adapter;
    private boolean mIsRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        int columnCount = getResources().getInteger(R.integer.list_column_count);
        GridLayoutManager staggeredGridLayoutManager =
                new GridLayoutManager(this, columnCount);
        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpaceItemDecoration(columnCount, (int)getResources().getDimension(R.dimen.space_item_decoration), true));
        adapter = new ArticleAdapter(null);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        getSupportLoaderManager().initLoader(0, null, this);

        if (savedInstanceState == null) {
            refresh();
        }
    }

    private void refresh() {
        startService(new Intent(this, UpdaterService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSwipeRefreshLayout.setOnRefreshListener(this);
        registerReceiver(mRefreshingReceiver,
                new IntentFilter(UpdaterService.BROADCAST_ACTION_STATE_CHANGE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mRefreshingReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRecyclerView.clearOnScrollListeners();
    }

    private BroadcastReceiver mRefreshingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UpdaterService.BROADCAST_ACTION_STATE_CHANGE.equals(intent.getAction())) {
                mIsRefreshing = intent.getBooleanExtra(UpdaterService.EXTRA_REFRESHING, false);
                updateRefreshingUI();
            }
        }

    };

    private void updateRefreshingUI() {
        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    @Override
    public void onRefresh() {
        if(ConnectionUtil.isOnline(this)) {
            startService(new Intent(this, UpdaterService.class));
            Snackbar make = Snackbar.make(mCoordinatorLayout, R.string.refreshed_string, Snackbar.LENGTH_LONG);
            View view = make.getView();
            ((TextView) view.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            make.show();
        }else{
            mSwipeRefreshLayout.setRefreshing(false);
            Snackbar make = Snackbar.make(mCoordinatorLayout, R.string.offline_string, Snackbar.LENGTH_LONG);
            View view = make.getView();
            ((TextView) view.findViewById(android.support.design.R.id.snackbar_text)).setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            make.show();
        }
    }
}
