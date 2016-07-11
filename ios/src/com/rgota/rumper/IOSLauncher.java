package com.rgota.rumper;

import com.badlogic.gdx.Gdx;
import com.tagor.ras.RasGame;
import com.tagor.ras.utils.RxMgr;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGSize;
import org.robovm.apple.foundation.*;
import org.robovm.apple.gamekit.*;
import org.robovm.apple.uikit.*;
import org.robovm.pods.google.mobileads.*;
//import org.robovm.pods.google.analytics.GAI;
//import org.robovm.pods.google.analytics.GAILogLevel;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.objc.block.VoidBlock2;
import rx.Subscription;
import rx.functions.Action1;

import java.util.Arrays;

public class IOSLauncher extends IOSApplication.Delegate implements GKGameCenterControllerDelegate {
    private GADBannerView bannerView;
    private GADInterstitial interstitial;

    private Subscription
            shareSub,
            bannerSub,
            interstitialSub,
            showLeaderBoardSub,
            submitLeaderBoardSub,
            showAchivementSub,
            submitAchivementSub;

    private final String LB_DEFAULT = "1";

    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.orientationLandscape = true;
        config.orientationPortrait = false;
        config.useAccelerometer = false;
        config.useCompass = false;
        config.allowIpod = false;
        return new IOSApplication(new RasGame(), config);
    }

    @Override
    public boolean didFinishLaunching(UIApplication application, UIApplicationLaunchOptions launchOptions) {
//        GAI gai = GAI.getSharedInstance();
//        gai.enableCrashReporting();
//        gai.getLogger().setLogLevel(GAILogLevel.Verbose);


        return super.didFinishLaunching(application, launchOptions);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
    }

    private IOSApplication getApp() {
        return (IOSApplication) Gdx.app;
    }

    @Override
    public void didBecomeActive(UIApplication application) {
        super.didBecomeActive(application);
        initAdBanner();

        shareSub = RxMgr.onShareText()
                .asJavaObservable()
                .subscribe(new Action1<String>() {
            @Override public void call(String s) {
                NSArray<NSString> items = new NSArray<>(new NSString(s));

                try {
                    UIActivityViewController uiActivityViewController = new UIActivityViewController(items, null);
                    if (UIDevice.getCurrentDevice().getUserInterfaceIdiom() == UIUserInterfaceIdiom.Phone) { // iphone
                        getApp().getUIViewController().presentViewController(uiActivityViewController, true, null);
                    } else { // IPAD
                        int nVer = getApp().getVersion();
                        UIView view = getApp().getUIViewController().getView();
                        if (nVer >= 8) {
                            uiActivityViewController.setModalPresentationStyle(UIModalPresentationStyle.Popover);
                            uiActivityViewController.getPopoverPresentationController().setSourceView(view);
                            getApp().getUIViewController().presentViewController(uiActivityViewController, true, null);
                        } else {
                            UIPopoverController popup = new UIPopoverController(uiActivityViewController);
                            popup.presentFromRectInView(view.getFrame(), view, UIPopoverArrowDirection.Any, true);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("RGT -> presentViewController(uiActivityViewController (share text) Err "+e.getMessage());
                }
            }
        });

        bannerSub = RxMgr.bannerViewVisible()
                .asJavaObservable()
                .subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                boolean v = Boolean.parseBoolean(s);
                if (v) showAdBanner();
                else hideAdBanner();
            }
        });

        interstitialSub = RxMgr.interstitialViewVisible()
                .asJavaObservable()
                .subscribe(new Action1<String>() {
               @Override
           public void call(String s) {
               showInterstitial();
           }
        });

        showLeaderBoardSub = RxMgr.showLeaderBoard()
                .asJavaObservable()
                .subscribe(new Action1<String>() {
            @Override public void call(final String s) {
                try {
                    showLeaderboard();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    System.out.println("RGT -> showLeaderBoard Err "+e.getMessage());
                }

            }
        });

        submitLeaderBoardSub = RxMgr.submitLeaderBoard()
                .asJavaObservable()
                .subscribe(new Action1<String>() {
            @Override public void call(final String s) {
                try {
                    reportLeaderBoard(s);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    System.out.println("RGT -> showLeaderBoard Err "+e.getMessage());
                }
            }
        });

        showAchivementSub = RxMgr.showAchivements()
                .asJavaObservable()
                .subscribe(new Action1<String>() {
            @Override public void call(final String s) {
                try {
                    showAchivement();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    System.out.println("RGT -> showLeaderBoard Err "+e.getMessage());
                }

            }
        });

        submitAchivementSub = RxMgr.submitAchivements()
                .asJavaObservable()
                .subscribe(new Action1<String>() {
            @Override public void call(final String s) {
                try {
                    reportAchivements(s);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    System.out.println("RGT -> showLeaderBoard Err "+e.getMessage());
                }
            }
        });

        authenticateLocalPlayer();
    }

    private void showInterstitial() {
        try {
            if (interstitial.isReady()) {
                interstitial.present(getApp().getUIViewController());
            } else {
                System.out.println("Interstitial not ready!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("RGT -> showInterstitial Err "+e.getMessage());
        }
    }

    @Override
    public void willResignActive(UIApplication application) {
        super.willResignActive(application);
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
        if (showAchivementSub != null)
            showAchivementSub.unsubscribe();
        if (submitAchivementSub != null)
            submitAchivementSub.unsubscribe();
    }

    private void initAdBanner() {
        if (bannerView != null) {
            bannerView.removeFromSuperview();
            bannerView = null;
        }
        bannerView = new GADBannerView(GADAdSize.SmartBannerLandscape());
        bannerView.setAutoloadEnabled(true);
        String BANNER_AD_UNIT = "ca-app-pub-9325222623451987/4524002648";
        bannerView.setAdUnitID(BANNER_AD_UNIT);
        bannerView.setRootViewController(getApp().getUIViewController());
        bannerView.loadRequest(createRequest());

        interstitial = createAndLoadInterstitial();
    }

    private GADInterstitial createAndLoadInterstitial() {
        String FULL_INTERSTITIAL = "ca-app-pub-9325222623451987/2042503445";
        GADInterstitial interstitial = new GADInterstitial(FULL_INTERSTITIAL);
        interstitial.setDelegate(new GADInterstitialDelegateAdapter() {
            @Override public void didDismissScreen(GADInterstitial ad) {
                IOSLauncher.this.interstitial = createAndLoadInterstitial();
            }
        });
        interstitial.loadRequest(createRequest());
        return interstitial;
    }

    private GADRequest createRequest() {
        GADRequest request = new GADRequest();
        // To test on your devices, add their UDIDs here:
        request.setTestDevices(Arrays.asList(GADRequest.getSimulatorID()));
        return request;
    }

    private void showAdBanner() {
        UIView view = getApp().getUIViewController().getView();
        view.addSubview(bannerView);
        CGSize sSize = UIScreen.getMainScreen().getBounds().getSize();
        bannerView.setCenter(new CGPoint(sSize.getWidth() / 2, sSize.getHeight() - bannerView.getFrame().getSize().getHeight()));
        view.layoutIfNeeded();
    }

    private void hideAdBanner() {
        try {
            bannerView.removeFromSuperview();
            getApp().getUIViewController().getView().layoutIfNeeded();
            bannerView.loadRequest(createRequest());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void authenticateLocalPlayer() {
        final GKLocalPlayer localPlayer = GKLocalPlayer.getLocalPlayer();
        System.out.println("RGT - > GKLocalPlayer.getLocalPlayer()");

        localPlayer.setAuthenticateHandler(new VoidBlock2<UIViewController, NSError>() {
            @Override
            public void invoke(UIViewController uiViewController, NSError nsError) {
                if (uiViewController != null) {
                    System.out.println("RGT - > uiViewController != null");
                    getApp().getUIViewController()
                            .presentViewController(uiViewController, true, null);
                } else if (localPlayer.isAuthenticated()) {
                    System.out.println("RGT - > localPlayer isAuthenticated");
                } else {
                    System.err.println("RGT Err - > localPlayer.setAuthenticateHandler -> "
                            + nsError.getLocalizedDescription());
                }
            }
        });
    }

    private void reportLeaderBoard(final String value) {
        try {
            if (GKLocalPlayer.getLocalPlayer().isAuthenticated()) {

                String[] arr = value.split(";");
                long overallScoreValue = Long.parseLong(arr[0]);
                long upScoreValue = Long.parseLong(arr[1]);
                long downScoreValue = Long.parseLong(arr[2]);

                final String LB_UP = "upBlocksScored",
                    LB_DOWN = "downBlocksScored";

                final GKScore score = new GKScore(LB_DEFAULT);
                score.setValue(overallScoreValue);

                final GKScore upScore = new GKScore(LB_UP);
                upScore.setValue(upScoreValue);

                final GKScore downScore = new GKScore(LB_DOWN);
                downScore.setValue(downScoreValue);

                final NSArray<GKScore> scores = new NSArray<GKScore>(score, upScore, downScore);
                GKScore.reportScores(scores, new VoidBlock1<NSError>() {
                    @Override
                    public void invoke(NSError nsError) {
                        if (nsError != null) {
                            System.err.println("RGT Err -> GKScore.reportScores " + nsError.getLocalizedDescription());
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("RGT Err -> reportLeaderBoard "+ e.getMessage());
        }
    }

    private void reportAchivements(final String value) {
        try {
            if (GKLocalPlayer.getLocalPlayer().isAuthenticated()) {

                String[] arr = value.split(";");
                double overallAchivValue = Double.parseDouble(arr[0]);
                double upAchivValue = Double.parseDouble(arr[1]);
                double downAchivValue = Double.parseDouble(arr[2]);
                final String AV_50_OVERALL = "50OverallScore",
                    AV_75_OVERALL = "75OverallScore",
                    AV_100_OVERALL = "100OverallScore",
                    AV_25_UP = "25UpScore",
                    AV_50_UP = "50UpScore",
                    AV_75_UP = "75UpScore",
                    AV_25_DOWN = "25DownScore",
                    AV_50_DOWN = "50DownScore",
                    AV_75_DOWN = "75DownScore";

                final GKAchievement a50OverallScore = new GKAchievement(AV_50_OVERALL);
                a50OverallScore.setPercentComplete(overallAchivValue/50 * 100);

                final GKAchievement a75OverallScore = new GKAchievement(AV_75_OVERALL);
                a75OverallScore.setPercentComplete(overallAchivValue/75 * 100);

                final GKAchievement a100OverallScore = new GKAchievement(AV_100_OVERALL);
                a100OverallScore.setPercentComplete(overallAchivValue/100 * 100);

                // --
                final GKAchievement a25UpScore = new GKAchievement(AV_25_UP);
                a25UpScore.setPercentComplete(upAchivValue/25 * 100);

                final GKAchievement a50UpScore = new GKAchievement(AV_50_UP);
                a50UpScore.setPercentComplete(upAchivValue/50 * 100);

                final GKAchievement a75UpScore = new GKAchievement(AV_75_UP);
                a75UpScore.setPercentComplete(upAchivValue/75 * 100);

                // --
                final GKAchievement a25DownScore = new GKAchievement(AV_25_DOWN);
                a25DownScore.setPercentComplete(downAchivValue/25 * 100);

                final GKAchievement a50DownScore = new GKAchievement(AV_50_DOWN);
                a50DownScore.setPercentComplete(downAchivValue/50 * 100);

                final GKAchievement a75DownScore = new GKAchievement(AV_75_DOWN);
                a75DownScore.setPercentComplete(downAchivValue/75 * 100);

                final NSArray<GKAchievement> achivs = new NSArray<GKAchievement>(
                        a50OverallScore,
                        a75OverallScore,
                        a100OverallScore,
                        a25UpScore,
                        a50UpScore,
                        a75UpScore,
                        a25DownScore,
                        a50DownScore,
                        a75DownScore);
                GKAchievement.reportAchievements(achivs, new VoidBlock1<NSError>() {
                    @Override
                    public void invoke(NSError nsError) {
                        if (nsError != null) {
                            System.err.println("RGT -> GKAchievement.reportAchievements " + nsError.getLocalizedDescription());
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("RGT -> reportAchivements "+ e.getMessage());
        }
    }

    private void showLeaderboard() {
        if (!GKLocalPlayer.getLocalPlayer().isAuthenticated()) {
            authenticateLocalPlayer();
            return;
        }

        GKGameCenterViewController gcViewController = new GKGameCenterViewController();
        gcViewController.setGameCenterDelegate(this);
        gcViewController.setViewState(GKGameCenterViewControllerState.Leaderboards);
        gcViewController.setLeaderboardIdentifier(LB_DEFAULT);

        getApp().getUIViewController().presentViewController(gcViewController, true, null);
    }

    private void showAchivement() {
        if (!GKLocalPlayer.getLocalPlayer().isAuthenticated()) {
            authenticateLocalPlayer();
            return;
        }

        GKGameCenterViewController gcViewController = new GKGameCenterViewController();
        gcViewController.setGameCenterDelegate(this);
        gcViewController.setViewState(GKGameCenterViewControllerState.Achievements);

        getApp().getUIViewController().presentViewController(gcViewController, true, null);
    }

    @Override
    public void didFinish(GKGameCenterViewController gkGameCenterViewController) {
        gkGameCenterViewController.dismissViewController(true, null);
    }
}