package com.tagor.ras;

import com.badlogic.gdx.Gdx;
import com.tagor.ras.utils.RxMgr;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.*;
import org.robovm.apple.iad.ADAdType;
import org.robovm.apple.iad.ADBannerView;
import org.robovm.apple.uikit.UIActivityViewController;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import org.robovm.apple.uikit.UIView;
import rx.Subscription;
import rx.functions.Action1;

public class IOSLauncher extends IOSApplication.Delegate {

    private final ADBannerView mADBannerView = new ADBannerView(ADAdType.Banner);
    private Subscription shareSub, bannerSub;

    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        config.orientationLandscape = true;
        config.orientationPortrait = false;
        config.useAccelerometer = false;
        config.useCompass = false;
        config.allowIpod = true;
        return new IOSApplication(new RasGame(), config);
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
        initAd();

        shareSub = RxMgr.onShareText()
                .asJavaObservable()
                .subscribe(new Action1<String>() {
            @Override public void call(String s) {
                NSArray<NSString> items = new NSArray<>(new NSString(s));

                UIActivityViewController uiActivityViewController = new UIActivityViewController(items, null);
                getApp().getUIViewController().presentViewController(uiActivityViewController, true, null);
            }
        });

        bannerSub = RxMgr.bannerViewVisible()
                .asJavaObservable()
                .subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                boolean v = Boolean.parseBoolean(s);
                if (v) showAd();
                else hideAd();
            }
        });
    }

    @Override
    public void willResignActive(UIApplication application) {
        super.willResignActive(application);
        if (shareSub != null)
            shareSub.unsubscribe();
        if (bannerSub != null)
            bannerSub.unsubscribe();
    }

    private void initAd() {
        CGRect frame = mADBannerView.getFrame();
        frame.getOrigin().setY(
                getApp().getUIViewController().getView().getFrame().getSize().getHeight()
                        - mADBannerView.getFrame().getSize().getHeight());
        mADBannerView.setFrame(frame);
    }

    private void showAd() {
        if (mADBannerView.isBannerLoaded()) {
            UIView view = getApp().getUIViewController().getView();
            view.addSubview(mADBannerView);
            view.layoutIfNeeded();
        }
    }

    private void hideAd() {
        mADBannerView.removeFromSuperview();
        getApp().getUIViewController().getView().layoutIfNeeded();
    }

}