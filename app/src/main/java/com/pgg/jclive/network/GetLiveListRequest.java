package com.pgg.jclive.network;

import com.pgg.jclive.domain.LiveListResponseObj;
import com.pgg.jclive.domain.ResponseObject;

import java.io.IOException;

/**
 * Created by pengganggui on 2018/8/30.
 * 获取直播列表的请求封装
 */

public class GetLiveListRequest extends BaseRequest {
    private static final String HOST = "http://imoocbearlive.butterfly.mopaasapp.com/roomServlet?action=getList";

    public static class LiveListParam {
        public int pageIndex;

        public String toUrlParam() {
            return "&=pageIndex" + pageIndex;
        }
    }

    public String getUrl(LiveListParam param) {
        return HOST + param.toUrlParam();
    }



    @Override
    public void onResponseFail(int code) {
        sendFailMsg(code,"服务器异常");
    }

    @Override
    public void onResponseSuccess(String body) {
        LiveListResponseObj liveListresponseObject = gson.fromJson(body, LiveListResponseObj.class);
        if (liveListresponseObject == null) {
            sendFailMsg(-101, "数据格式错误");
            return;
        }

        if (liveListresponseObject.code.equals(ResponseObject.CODE_SUCCESS)) {
            sendSuccMsg(liveListresponseObject.data);
        } else if (liveListresponseObject.code.equals(ResponseObject.CODE_FAIL)) {
            sendFailMsg(Integer.valueOf(liveListresponseObject.errCode), liveListresponseObject.errMsg);
        }
    }

    @Override
    public void onFail(IOException e) {
        sendFailMsg(-100,e.toString());
    }
}
