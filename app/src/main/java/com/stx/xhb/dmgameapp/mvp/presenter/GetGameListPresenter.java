package com.stx.xhb.dmgameapp.mvp.presenter;

import android.text.TextUtils;

import com.stx.core.mvp.BasePresenter;
import com.stx.core.utils.GsonUtil;
import com.stx.xhb.dmgameapp.config.API;
import com.stx.xhb.dmgameapp.config.Constants;
import com.stx.xhb.dmgameapp.entity.GameListBean;
import com.stx.xhb.dmgameapp.entity.NewsContentBean;
import com.stx.xhb.dmgameapp.mvp.contract.GetGameListContract;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Author：xiaohaibin
 * Time：2017/9/20
 * Emil：xhb_199409@163.com
 * Github：https://github.com/xiaohaibin/
 * Describe：
 */

public class GetGameListPresenter extends BasePresenter<GetGameListContract.getGameListDataView,GetGameListContract.getGameListModel> implements GetGameListContract.getGameListModel{
    @Override
    public void getGameListData(String appId, int page) {
        if (getView()==null){
            return;
        }
        OkHttpUtils.postString()
                  .content(GsonUtil.newGson().toJson(new NewsContentBean(appId,page)))
                  .url(API.GET_GAME_CHANNEL_DATA)
                  .build()
                  .execute(new StringCallback() {
                      @Override
                      public void onBefore(Request request, int id) {
                          getView().showLoading();
                      }

                      @Override
                      public void onError(Call call, Exception e, int id) {
                          getView().getGameListDataFailed(e.getMessage());
                      }

                      @Override
                      public void onResponse(String response, int id) {
                           if (!TextUtils.isEmpty(response)){
                               GameListBean gameListBean = GsonUtil.newGson().fromJson(response, GameListBean.class);
                               if (gameListBean.getCode()== Constants.SERVER_SUCCESS){
                                   getView().getGameListDataSuccess(gameListBean.getHtml());
                               }else {
                                   getView().hideLoading();
                                   getView().getGameListDataFailed(gameListBean.getMsg());
                               }
                           }
                      }

                      @Override
                      public void onAfter(int id) {
                          getView().hideLoading();
                      }
                  });
    }
}
