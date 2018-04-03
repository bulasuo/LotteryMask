package com.abu.xbase.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.abu.xbase.Task.TaskR;
import com.abu.xbase.Task.TaskTR;
import com.abu.xbase.app.BaseApp;
import com.abu.xbase.util.ToastUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.lang.ref.WeakReference;

/**
 * PS :: 请在app model里面放微信activity, 因为要 包名路径下!!!
 * 微信开发者平台 微信支付功能的 签名是 md5 去冒号 小写
 * @author   abu
 * 2018/2/8    22:13
 * ..
 */
public abstract class BaseWXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	public static IWXAPI api;
	private static WeakReference<TaskTR<BaseResp, Boolean>> mSuccessTask;
	private static WeakReference<TaskR<Boolean>> mFailTask;

	private void handlerIntent(){
		try {
			if (!api.handleIntent(getIntent(), this)) {
				onErr();
			}
		}catch (Exception e){
			onErr();
		}
	}

	public static IWXAPI getApi(String appId){
		return  (api = WXAPIFactory.createWXAPI(BaseApp.getInstance(), appId));
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handlerIntent();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handlerIntent();
	}

	public static void sendReq(PayReq req,
                              TaskTR<BaseResp, Boolean> success,
							   TaskR<Boolean> fail){
		mSuccessTask = new WeakReference<TaskTR<BaseResp, Boolean>>(success);
		mFailTask = new WeakReference<TaskR<Boolean>>(fail);
		getApi(req.appId);
//		api.registerApp(req.appId);
		boolean b = api.sendReq(req);
		if(!b)
			onFail("调起微信客户端失败!");

	}

	@Override
	public void onReq(BaseReq baseReq) {
	}

	@Override
	public void onResp(BaseResp baseResp) {
		if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			int code = baseResp.errCode;
			switch (code) {
				case 0:
					onSuccess(baseResp);
					finish();
					break;
				case -1:
					onFail("支付失败!");
					finish();
					break;
				case -2:
					onFail("支付取消!");
					finish();
					break;
				default:
					onFail("支付失败!");
					finish();
					break;
			}
		}
	}

	private static void onSuccess(BaseResp baseResp){
		try{
			TaskTR<BaseResp, Boolean> success;
			if(mSuccessTask == null
					|| (success = mSuccessTask.get()) == null
					|| !success.apply(baseResp)){
				ToastUtil.showShort("支付成功!");
			}
		}catch (Exception e){
			ToastUtil.showException(e);
			ToastUtil.showShort("支付成功!");
		}
	}

	private static void onFail(String tip){
		try{
			TaskR<Boolean> fail;
			if(mFailTask == null
					|| (fail = mFailTask.get()) == null
					|| !fail.apply()){
				ToastUtil.showShort(tip);
			}
		}catch (Exception e){
			ToastUtil.showException(e);
			ToastUtil.showShort(tip);
		}
	}

	private void onErr(){
		onFail("请求失败!");
		finish();
	}
}