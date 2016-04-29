package com.tagor.ras;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.*;
import com.tagor.ras.utils.RxMgr;
import rx.Subscription;
import rx.functions.Action1;

public class AndroidLauncher extends AndroidApplication {

	private Subscription shareSub;
	private Subscription bannerSub;
	private Subscription interstitialSub;

	private AdView bannerView;
	private InterstitialAd mInterstitialAd;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useImmersiveMode = true;
		config.useAccelerometer = false;
		config.useCompass = false;

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		Log.d("RGT", "Android on Create initializeForView RasGame");

		View gview = initializeForView(new RasGame(), config);

		FrameLayout layout = new FrameLayout(this);
		layout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
		gview.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

		bannerView = new AdView(this);
		bannerView.setAdSize(AdSize.SMART_BANNER);
		bannerView.setAdUnitId(getString(R.string.bottom_banner));
		FrameLayout.LayoutParams bvlp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		bvlp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		bannerView.setLayoutParams(bvlp);

		layout.addView(gview);
		layout.addView(bannerView);

		setContentView(layout);

		bannerView.post(new Runnable() {
			@Override public void run() {
				bannerView.loadAd(requestAd());
			}
		});
		bannerView.setVisibility(View.GONE);

		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId(getString(R.string.full_interstitial));
		requestNewInterstitial();

		mInterstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				super.onAdClosed();
				requestNewInterstitial();
			}
		});
	}

	private void requestNewInterstitial() {
		mInterstitialAd.loadAd(requestAd());
	}

	private AdRequest requestAd() {
		return new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.addTestDevice("E7BE4AD3136E7914679B84D5580F3B86")
				.build();
	}

	@Override
	protected void onStart() {
		super.onStart();
		shareSub = RxMgr.onShareText()
				.asJavaObservable()
				.subscribe(new Action1<String>() {
			@Override public void call(final String s) {
				bannerView.post(new Runnable() {
					@Override public void run() {
						Intent sendIntent = new Intent();
						sendIntent.setAction(Intent.ACTION_SEND);
						sendIntent.putExtra(Intent.EXTRA_TEXT, s);
						sendIntent.setType("text/plain");
						startActivity(sendIntent);
					}
				});
			}
		});

		bannerSub = RxMgr.bannerViewVisible()
				.asJavaObservable()
				.subscribe(new Action1<String>() {
			@Override public void call(String b) {
				final boolean v = Boolean.parseBoolean(b);
				bannerView.post(new Runnable() {
					@Override public void run() {
						bannerView.setVisibility(v ? View.VISIBLE : View.GONE);
					}
				});
			}
		});

		interstitialSub = RxMgr.interstitialViewVisible()
				.asJavaObservable()
				.subscribe(new Action1<String>() {
			@Override public void call(String s) {
				bannerView.post(new Runnable() {
					@Override public void run() {
						if (mInterstitialAd.isLoaded())
							mInterstitialAd.show();
					}
				});
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (bannerView != null)
			bannerView.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (bannerView != null)
			bannerView.resume();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (shareSub != null)
			shareSub.unsubscribe();
		if (bannerSub != null)
			bannerSub.unsubscribe();
		if (interstitialSub != null)
			interstitialSub.unsubscribe();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bannerView != null)
			bannerView.destroy();
		Log.d("RGT", "Android to destroy");
		UIHelper.killApp(true);
	}
}
