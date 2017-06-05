package com.example.myretrofittest.network.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.myretrofittest.R;
import com.example.myretrofittest.network.bean.News;

import java.util.List;

/**
 * Created by gcy on 2017/5/24 0024.
 */
public class HeadNewsAdapter extends BaseQuickAdapter<News, BaseViewHolder> {
    public HeadNewsAdapter(List<News> data) {
        super(R.layout.rvitem, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, News item) {
        helper.setText(R.id.title, item.getTitle());
        helper.setText(R.id.content, item.getContent());
        Glide.with(mContext).load(item.getPicUrl()).into((ImageView) helper.getView(R.id.pic));
    }
}
