package com.pgg.jclive.network;

import com.pgg.jclive.domain.ResponseObject;
import com.pgg.jclive.domain.RoomInfoResponseObj;

import java.io.IOException;


/**
 * Created by pgg.
 * 创建直播间的请求
 */

public class CreateRoomRequest extends BaseRequest {

    private static final String Action = "http://imoocbearlive.butterfly.mopaasapp.com/roomServlet?action=create";

    private static final String RequestParamKey_UserId = "userId";
    private static final String RequestParamKey_UserAvatar = "userAvatar";
    private static final String RequestParamKey_UserName = "userName";
    private static final String RequestParamKey_LiveTitle = "liveTitle";
    private static final String RequestParamKey_LiveCover = "liveCover";

    public static class CreateRoomParam {
        public String userId;
        public String userAvatar;
        public String userName;
        public String liveTitle;
        public String liveCover;
    }

    public String getUrl(CreateRoomParam param) {
        return Action
                + "&" + RequestParamKey_UserId + "=" + param.userId
                + "&" + RequestParamKey_UserAvatar + "=" + param.userAvatar
                + "&" + RequestParamKey_UserName + "=" + param.userName
                + "&" + RequestParamKey_LiveTitle + "=" + param.liveTitle
                + "&" + RequestParamKey_LiveCover + "=" + param.liveCover
                ;
    }

    @Override
    public void onFail(IOException e) {
        sendFailMsg(-100, e.getMessage());
    }


    @Override
    public void onResponseFail(int code) {
        sendFailMsg(code, "服务出现异常");
    }

    @Override
    public void onResponseSuccess(String body) {
        RoomInfoResponseObj responseObject = gson.fromJson(body, RoomInfoResponseObj.class);
        if (responseObject == null) {
            sendFailMsg(-101, "数据格式错误");
            return;
        }

        if (responseObject.code.equals(ResponseObject.CODE_SUCCESS)) {
            sendSuccMsg(responseObject.data);
        } else if (responseObject.code.equals(ResponseObject.CODE_FAIL)) {
            sendFailMsg(Integer.valueOf(responseObject.errCode), responseObject.errMsg);
        }
    }

}
