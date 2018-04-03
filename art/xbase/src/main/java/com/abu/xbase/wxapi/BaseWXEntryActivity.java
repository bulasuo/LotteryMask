package com.abu.xbase.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.abu.xbase.app.BaseApp;
import com.abu.xbase.util.ToastUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * PS :: 请在app model里面放微信activity, 因为要 包名路径下!!!
 * 微信开发者平台 微信支付功能的 签名是 md5 去冒号 小写
 * @author   abu
 * 2018/2/8    22:13
 * ..
 */

public abstract class BaseWXEntryActivity extends Activity implements IWXAPIEventHandler{

	public static IWXAPI api;

	private void handlerIntent(){
		try {
			if (!api.handleIntent(getIntent(), this)) {
				onErr();
			}
		}catch (Exception e){
			onErr();
		}
	}

	private void onErr(){
		ToastUtil.showShort("请求失败!");
		finish();
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

	@Override
	public void onReq(BaseReq baseReq) {
	}

	@Override
	public void onResp(BaseResp baseResp) {
		if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			int code = baseResp.errCode;
			switch (code) {
				case 0:
					ToastUtil.showShort("支付成功");
					finish();
					break;
				case -1:
					ToastUtil.showShort("支付失败");
					finish();
					break;
				case -2:
					ToastUtil.showShort("支付取消");
					finish();
					break;
				default:
					ToastUtil.showShort("支付失败");
					finish();
					break;
			}
		}
	}
}