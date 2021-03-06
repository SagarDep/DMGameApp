package com.stx.xhb.dmgameapp.mvp.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.qq.e.ads.contentad.ContentAD;
import com.qq.e.ads.contentad.ContentAdData;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeADDataRef;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;
import com.stx.core.log.Logger;
import com.stx.core.mvp.BasePresenter;
import com.stx.core.utils.GsonUtil;
import com.stx.xhb.dmgameapp.config.API;
import com.stx.xhb.dmgameapp.config.Constants;
import com.stx.xhb.dmgameapp.entity.NewsContentBean;
import com.stx.xhb.dmgameapp.entity.NewsListBean;
import com.stx.xhb.dmgameapp.mvp.contract.GetNewsListContract;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Author：xiaohaibin
 * Time：2017/9/18
 * Emil：xhb_199409@163.com
 * Github：https://github.com/xiaohaibin/
 * Describe：
 */

public class GetNewsListPresenter extends BasePresenter<GetNewsListContract.getNewListView, GetNewsListContract.getNewsListModel> implements GetNewsListContract.getNewsListModel {

    private NativeExpressAD contentAD;
    private NativeExpressADView nativeExpressADView;

    @Override
    public void getNewsList(String appId, int page) {
        if (getView() == null) {
            return;
        }
        OkHttpUtils.postString()
                .content(GsonUtil.newGson().toJson(new NewsContentBean(appId, page)))
                .url(API.NEWS_CHANNEL_DATA)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(Request request, int id) {
                        getView().showLoading();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        getView().getNewListFailed(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (!TextUtils.isEmpty(response)) {
                            NewsListBean newsListBean = GsonUtil.newGson().fromJson(response, NewsListBean.class);
                            if (newsListBean.getCode() == Constants.SERVER_SUCCESS) {
                                getView().getNewListSuccess(newsListBean);
                            } else {
                                getView().hideLoading();
                                getView().getNewListFailed(TextUtils.isEmpty(newsListBean.getMsg()) ? "服务器请求失败，请重试" : newsListBean.getMsg());
                            }
                        }
                    }

                    @Override
                    public void onAfter(int id) {
                        getView().hideLoading();
                    }
                });
    }

    @Override
    public void loadAD(Context context) {
        if (contentAD == null) {
            contentAD = new NativeExpressAD(context, new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT), Constants.APPID, Constants.NativePosID, new NativeExpressAD.NativeExpressADListener() {
                @Override
                public void onNoAD(AdError adError) {

                }

                @Override
                public void onADLoaded(List<NativeExpressADView> list) {
                    if (nativeExpressADView != null) {
                        nativeExpressADView.destroy();
                    }
                    if (getView() != null && !list.isEmpty()) {
                        nativeExpressADView = list.get(0);
                    }
                    getView().getADData(nativeExpressADView);
                }

                @Override
                public void onRenderFail(NativeExpressADView nativeExpressADView) {

                }

                @Override
                public void onRenderSuccess(NativeExpressADView nativeExpressADView) {

                }

                @Override
                public void onADExposure(NativeExpressADView nativeExpressADView) {

                }

                @Override
                public void onADClicked(NativeExpressADView nativeExpressADView) {

                }

                @Override
                public void onADClosed(NativeExpressADView nativeExpressADView) {

                }

                @Override
                public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

                }

                @Override
                public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

                }

                @Override
                public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

                }
            });
        }
        contentAD.loadAD(1);
    }
}
