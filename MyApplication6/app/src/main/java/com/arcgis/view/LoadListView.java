package com.arcgis.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.arcgis.R;

/**
 * Created by mars on 2015/1/31.
 */

public class LoadListView extends ListView implements AbsListView.OnScrollListener {

    View footer;// 底部布局；
    View header;//顶部
    int totalItemCount;// 总数量；
    int lastVisibleItem;// 最后一个可见的item；
    boolean isLoading;// 正在加载；
    ILoadListener iLoadListener;

    public LoadListView(Context context) {
        super(context);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoadListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (totalItemCount == lastVisibleItem && scrollState == SCROLL_STATE_IDLE) {
            if (!isLoading) {
                isLoading = true;
                footer.findViewById(R.id.load_layout).setVisibility(View.VISIBLE);
                // 加载更多
                iLoadListener.onLoad();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        footer = inflater.inflate(R.layout.footer_layout, null);
        footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
        header=inflater.inflate(R.layout.header_layout, null);
        this.addHeaderView(header);
        this.addFooterView(footer);
        this.setOnScrollListener(this);
    }

    /**
     * 加载完毕
     */
    public void loadComplete(){
        isLoading = false;
        footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
    }

    public void setInterface(ILoadListener iLoadListener){
        this.iLoadListener = iLoadListener;
    }
    //加载更多数据的回调接口
    public interface ILoadListener{
        public void onLoad();
    }

}
