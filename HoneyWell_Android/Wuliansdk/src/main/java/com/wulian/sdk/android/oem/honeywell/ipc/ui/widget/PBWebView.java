/**
 * Project Name:  iCam
 * File Name:     PBWebView.java
 * Package Name:  com.wulian.icam.widget
 * @Date:         2014年12月20日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.sdk.android.oem.honeywell.ipc.ui.widget;

import android.content.Context;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.wulian.sdk.android.oem.honeywell.ipc.utils.Utils;


/**
 * @ClassName: PBWebView
 * @Function: 带进度条的webview
 * @Date: 2014年12月20日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class PBWebView extends WebView {
	private ProgressBar progressBar;

	public PBWebView(Context context, AttributeSet attrs) {

		super(context, attrs);
		// progressBar = new ProgressBar(context, null,
		// android.R.attr.progressBarStyleHorizontal);
		// progressBar.setProgressDrawable(getResources().getDrawable(
		// com.wulian.icam.R.drawable.custom_progressbar_horizontal));
		// progressBar.setLayoutParams(new RelativeLayout.LayoutParams(
		// FrameLayout.LayoutParams.MATCH_PARENT, 10));
		// addView(progressBar);
		this.setHorizontalScrollBarEnabled(false);// 不显示水平滚动条
		WebSettings ws = this.getSettings(); 
		ws.setUseWideViewPort(true);
		ws.setLoadWithOverviewMode(true);
		ws.setJavaScriptEnabled(true);
		ws.setSupportZoom(false);
		ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		
		//webview长按禁止复制粘贴
		this.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		
		setWebViewClient(new MyWebViewClient());
		setWebChromeClient(new MyWebChromeClient());
	}

	public void setProgerssBar(ProgressBar pb) {
		this.progressBar = pb;
	}

	public class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			Utils.sysoInfo("loading:" + newProgress);
			if (progressBar != null) {
				if (newProgress == 100) {
					progressBar.setVisibility(GONE);
				} else {
					if (progressBar.getVisibility() == GONE) {
						progressBar.setVisibility(VISIBLE);
					}
					progressBar.setProgress(newProgress);
				}
			}
			super.onProgressChanged(view, newProgress);
		}

	}

	public class MyWebViewClient extends WebViewClient {
		@Override
		public void onReceivedSslError(WebView view,
				SslErrorHandler handler, SslError error) {
			handler.proceed();
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// return super.shouldOverrideUrlLoading(view, url);
			view.loadUrl(url);
			return true;
		}

	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {

		super.onScrollChanged(l, t, oldl, oldt);
		// 同步更改ProgressBar的位置?
		Utils.sysoInfo("scroll:" + l + "," + t + "," + oldl + "," + oldt);
	}
}
