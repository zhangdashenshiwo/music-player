package com.example.zhang.myapplication.viewpage;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.zhang.myapplication.R;

import java.util.ArrayList;
import java.util.List;


public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<Object> mData;
    private Context mContext;

    private int MaxElevationFactor = 9;

    @Override
    public int getMaxElevationFactor() {
        return MaxElevationFactor;
    }

    @Override
    public void setMaxElevationFactor(int MaxElevationFactor) {
        this.MaxElevationFactor = MaxElevationFactor;
    }

    public CardPagerAdapter(Context context) {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();

        this.mContext = context;
    }

    public void addImgUrlList(List<Object> imgUrlList) {
        mData.addAll(imgUrlList);

        for (int i = 0; i < imgUrlList.size(); i++) {
            mViews.add(null);
        }
    }

    /**
     * 获取图片序列下标
     * @param position
     * @return 下标
     */
    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    /**
     *  用于得到数据
     * @return  数据大小
     */
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_viewpage_adapter, container, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardItemClickListener != null) {
                    cardItemClickListener.onClick(position);
                }
            }
        });
        container.addView(view);
        bind(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        cardView.setMaxCardElevation(MaxElevationFactor);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(Object imgUrl, View view) {
        ImageView iv = (ImageView) view.findViewById(R.id.item_iv);
        ImageLoadFactory.getInstence().loadImage(iv,imgUrl,iv.getContext());

    }

    private OnCardItemClickListener cardItemClickListener;

    public interface OnCardItemClickListener {
        void onClick(int position);
    }

    public void setOnCardItemClickListener(OnCardItemClickListener cardItemClickListener) {
        this.cardItemClickListener = cardItemClickListener;
    }

}
