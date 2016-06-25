package com.tagor.ras;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;
import com.tagor.ras.utils.RxMgr;
import rx.Subscription;
import rx.functions.Action1;

public class AndroidLauncher extends AndroidApplication implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private Subscription
			shareSub,
			bannerSub,
			interstitialSub,
			showLeaderBoardSub,
			submitLeaderBoardSub,
			showAchivsSub,
			submitAchivsSub,
			incEventSub;

	private AdView bannerView;
	private InterstitialAd mInterstitialAd;

	private GoogleApiClient mGoogleApiClient;

	private static final int REQUEST_LEADERBOARD = 123;
	private static final int REQUEST_ACHIVS = 124;
	private final int RC_SIGN_IN = 9001;
	// Request code to use when launching the resolution activity
	private static final int REQUEST_RESOLVE_ERROR = 1001;
	// Unique tag for the error dialog fragment
	private static final String DIALOG_ERROR = "dialog_error";

	private boolean mResolvingConnectionFailure = false;
	private boolean mAutoStartSignInFlow  = true;
	private boolean mSignInClicked = false;

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

		mResolvingConnectionFailure = false;
		Games.GamesOptions gamesOptions = Games.GamesOptions.builder().setRequireGooglePlus(true).build();
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
				.addApi(Games.API, gamesOptions).addScope(Games.SCOPE_GAMES)
				.build();
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
						if (!v) {
							bannerView.loadAd(requestAd());
						}
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

		showLeaderBoardSub = RxMgr.showLeaderBoard()
				.asJavaObservable()
				.subscribe(new Action1<String>() {
			@Override public void call(final String s) {
				if (mGoogleApiClient.isConnected())
					bannerView.post(new Runnable() {
						@Override public void run() {
							try {
								Log.e("RGT", "showLeaderBoard let's work!");
								startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
										getString(R.string.leaderboard_main_score)), REQUEST_LEADERBOARD);
							} catch (NumberFormatException e) {
								e.printStackTrace();
								Log.e("RGT", "showLeaderBoard Err "+e.getMessage(), e);
							}
						}
					});
			}
		});

		submitLeaderBoardSub = RxMgr.submitLeaderBoard()
				.asJavaObservable()
				.subscribe(new Action1<String>() {
			@Override public void call(final String s) {
				bannerView.post(new Runnable() {
					@Override public void run() {
						try {
							Log.e("RGT", "submitLeaderBoard let's work!");
							if (mGoogleApiClient.isConnected() && !s.isEmpty()) {
								String[] arr = s.split(";");
								int overallScore = Integer.parseInt(arr[0]);
								int upScore = Integer.parseInt(arr[1]);
								int downScore = Integer.parseInt(arr[2]);

								if (overallScore > 0)
									Games.Leaderboards.submitScoreImmediate(mGoogleApiClient, getString(R.string.leaderboard_main_score), overallScore);
								if (upScore > 0)
									Games.Leaderboards.submitScoreImmediate(mGoogleApiClient, getString(R.string.leaderboard_up_blocks_scored), upScore);
								if (downScore > 0)
									Games.Leaderboards.submitScoreImmediate(mGoogleApiClient, getString(R.string.leaderboard_down_blocks_scored), downScore);
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
							Log.e("RGT", "submitLeaderBoard Err "+e.getMessage(), e);
						}
					}
				});
			}
		});

		showAchivsSub = RxMgr.showAchivements()
				.asJavaObservable()
				.subscribe(new Action1<String>() {
			@Override public void call(final String s) {
				if (mGoogleApiClient.isConnected())
					bannerView.post(new Runnable() {
						@Override public void run() {
							try {
								Log.e("RGT", "showAchivements let's work!");
								startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), REQUEST_ACHIVS);
							} catch (NumberFormatException e) {
								e.printStackTrace();
								Log.e("RGT", "showAchivements Err "+e.getMessage(), e);
							}
						}
					});
			}
		});

		submitAchivsSub = RxMgr.submitAchivements()
				.asJavaObservable()
				.subscribe(new Action1<String>() {
			@Override public void call(final String s) {
				bannerView.post(new Runnable() {
					@Override public void run() {
						try {
							if (mGoogleApiClient.isConnected()) {
								Log.d("RGT", "submitAchivements let's work!");
								String[] arr = s.split(";");
								int overallScore = Integer.parseInt(arr[0]);
								int upScore = Integer.parseInt(arr[1]);
								int downScore = Integer.parseInt(arr[2]);

								if (overallScore > 0) {
									Games.Achievements.incrementImmediate(mGoogleApiClient, getString(R.string.achievement_50_overall_score), overallScore);
									Games.Achievements.incrementImmediate(mGoogleApiClient, getString(R.string.achievement_100_overall_score), overallScore);
									Games.Achievements.incrementImmediate(mGoogleApiClient, getString(R.string.achievement_150_overall_score), overallScore);
								}

								if (upScore > 0) {
									Games.Achievements.incrementImmediate(mGoogleApiClient, getString(R.string.achievement_25_up_score), upScore);
									Games.Achievements.incrementImmediate(mGoogleApiClient, getString(R.string.achievement_50_up_score), upScore);
									Games.Achievements.incrementImmediate(mGoogleApiClient, getString(R.string.achievement_75_up_score), upScore);
								}

								if (downScore > 0) {
									Games.Achievements.incrementImmediate(mGoogleApiClient, getString(R.string.achievement_25_down_score), downScore);
									Games.Achievements.incrementImmediate(mGoogleApiClient, getString(R.string.achievement_50_down_score), downScore);
									Games.Achievements.incrementImmediate(mGoogleApiClient, getString(R.string.achievement_75_down_score), downScore);
								}
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
							Log.e("RGT", "showAchivements Err "+e.getMessage(), e);
						}
					}
				});
			}
		});

		incEventSub = RxMgr.incEvent()
				.asJavaObservable()
				.subscribe(new Action1<String>() {
			@Override public void call(final String s) {
				if (mGoogleApiClient.isConnected())
					bannerView.post(new Runnable() {
						@Override public void run() {
							try {
								Games.Events.increment(mGoogleApiClient, getString(R.string.event_plays_count), 1);
							} catch (Exception e) {
								e.printStackTrace();
								Log.e("RGT", "Games.Events.increment Err "+e.getMessage(), e);
							}
						}
					});
			}
		});

		mResolvingConnectionFailure = false;
		mGoogleApiClient.connect();
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
		if (showLeaderBoardSub != null)
			showLeaderBoardSub.unsubscribe();
		if (submitLeaderBoardSub != null)
			submitLeaderBoardSub.unsubscribe();
		if (showAchivsSub != null)
			showAchivsSub.unsubscribe();
		if (submitAchivsSub != null)
			submitAchivsSub.unsubscribe();
		if (incEventSub != null)
			incEventSub.unsubscribe();

		if (mGoogleApiClient.isConnected())
			mGoogleApiClient.disconnect();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bannerView != null)
			bannerView.destroy();
		Log.d("RGT", "Android to destroy");
		UIHelper.killApp(true);
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		Log.d("RGT", "GoogleClient connected");
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.d("RGT", "GoogleClient connection suspended "+ i +", reconnecting...");// Attempt to reconnect
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		Log.e("RGT", "GoogleClient onConnectionFailed "+ connectionResult.getErrorCode()+", "+ connectionResult.getErrorMessage()
		 + ", hasResolution = " + connectionResult.hasResolution());

		if (mResolvingConnectionFailure) {
			// Already attempting to resolve an error.
			Log.e("RGT", "GoogleClient onConnectionFailed - Already attempting to resolve an error.");
			return;
		} else if (connectionResult.hasResolution()) {
			Log.d("RGT", "GoogleClient onConnectionFailed - connectionResult.hasResolution()");
			try {
				mResolvingConnectionFailure = true;
				connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
			} catch (IntentSender.SendIntentException e) {
				// There was an error with the resolution intent. Try again.
				Log.e("RGT", "GoogleClient onConnectionFailed - There was an error with the resolution intent. Try again", e);
				mResolvingConnectionFailure = false;
				mGoogleApiClient.connect();
			}
		} else {
			// Show dialog using GoogleApiAvailability.getErrorDialog()
			showErrorDialog(connectionResult.getErrorCode());
			mResolvingConnectionFailure = true;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("RGT", String.format("onActivityResult requestCode %d, resultCode %d, data %s", requestCode, resultCode, data));
		if (requestCode == REQUEST_RESOLVE_ERROR) {
			if (!mGoogleApiClient.isConnecting())
				mGoogleApiClient.connect();
		} else if (requestCode == REQUEST_LEADERBOARD) {
			if (resultCode == 10001) { // RESULT_RECONNECT_REQUIRED
				Log.d("RGT", "GoogleClient RESULT_RECONNECT_REQUIRED");
				mResolvingConnectionFailure = false;
				mGoogleApiClient.reconnect();
			}
		}
	}

	/* Creates a dialog for an error message */
	private void showErrorDialog(int errorCode) {
		// Create a fragment for the error dialog
		ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
		// Pass the error that should be displayed
		Bundle args = new Bundle();
		args.putInt(DIALOG_ERROR, errorCode);
		dialogFragment.setArguments(args);
		dialogFragment.show(getFragmentManager(), "errordialog");
	}

	/* Called from ErrorDialogFragment when the dialog is dismissed. */
	public void onDialogDismissed() {
		mResolvingConnectionFailure = false;
	}

	/* A fragment to display an error dialog */
	public static class ErrorDialogFragment extends DialogFragment {
		public ErrorDialogFragment() { }

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Get the error code and retrieve the appropriate dialog
			int errorCode = this.getArguments().getInt(DIALOG_ERROR);
			return GoogleApiAvailability.getInstance().getErrorDialog(
					this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			((AndroidLauncher) getActivity()).onDialogDismissed();
		}
	}
}
