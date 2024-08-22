package com.example.ads.helper.new_.demo.activitys

import android.Manifest
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.akshay.harsoda.permission.helper.AksPermission
import com.akshay.harsoda.permission.helper.utiles.OnAlertButtonClickListener
import com.akshay.harsoda.permission.helper.utiles.getPermissionName
import com.akshay.harsoda.permission.helper.utiles.showAlert
import com.example.ads.helper.new_.demo.IS_OPEN_ADS_ENABLE
import com.example.ads.helper.new_.demo.R
import com.example.ads.helper.new_.demo.base.BaseActivity
import com.example.ads.helper.new_.demo.base.BaseBindingActivity
import com.example.ads.helper.new_.demo.base.shared_prefs.getBoolean
import com.example.ads.helper.new_.demo.base.shared_prefs.save
import com.example.ads.helper.new_.demo.base.utils.getDrawableRes
import com.example.ads.helper.new_.demo.base.utils.getStringRes
import com.example.ads.helper.new_.demo.base.utils.setSelection
import com.example.ads.helper.new_.demo.base.utils.shareApp
import com.example.ads.helper.new_.demo.databinding.ActivityMainBinding
import com.example.ads.helper.new_.demo.triggerRebirth
import com.example.ads.helper.new_.demo.widget.UpdateSubscriptionDialog
import com.example.app.ads.helper.activity.showFullScreenNativeAdActivity
import com.example.app.ads.helper.interstitialad.InterstitialAdHelper.showInterstitialAd
import com.example.app.ads.helper.purchase.AdsManager
import com.example.app.ads.helper.reward.RewardedInterstitialAdHelper
import com.example.app.ads.helper.reward.RewardedInterstitialAdHelper.showRewardedInterstitialAd
import com.example.app.ads.helper.reward.RewardedVideoAdHelper
import com.example.app.ads.helper.reward.RewardedVideoAdHelper.showRewardedVideoAd
import com.example.app.ads.helper.startShowingOpenAdInternally
import com.example.app.ads.helper.stopShowingOpenAdInternally

class MainActivity : BaseBindingActivity<ActivityMainBinding>() {

    override fun getActivityContext(): BaseActivity {
        return this@MainActivity
    }

    override fun setBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initView() {
        super.initView()

        AdsManager.isShowAds.observe(mActivity) {
            Log.e(TAG, "initView: AdsManager needToShowAds::-> $it")
        }

        with(mBinding) {
            root.setSelection()

            openAdsSwitch.isChecked = mActivity.getBoolean(IS_OPEN_ADS_ENABLE, true)
            adsSwitch.isChecked = true

            with(layoutHeader) {
                ivHeaderBack.setImageDrawable(mActivity.getDrawableRes(R.drawable.ic_new_header_back))
                ivHeaderRightIcon.setImageDrawable(mActivity.getDrawableRes(R.drawable.ic_share_blue))
            }
        }

        /*SUBSCRIPTION_DATA_LANGUAGE_CODE = Locale.ENGLISH.language
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> OTP -> ${"OTP".getFullBillingPeriod(mActivity)}")
        SUBSCRIPTION_DATA_LANGUAGE_CODE = "hi"
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> OTP -> ${"OTP".getFullBillingPeriod(mActivity)}")
        SUBSCRIPTION_DATA_LANGUAGE_CODE = "gu"
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> OTP -> ${"OTP".getFullBillingPeriod(mActivity)}")

        SUBSCRIPTION_DATA_LANGUAGE_CODE = Locale.ENGLISH.language
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P1D -> ${"P1D".getFullBillingPeriod(mActivity)}")
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P2D -> ${"P2D".getFullBillingPeriod(mActivity)}")
        SUBSCRIPTION_DATA_LANGUAGE_CODE = "hi"
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P1D -> ${"P1D".getFullBillingPeriod(mActivity)}")
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P2D -> ${"P2D".getFullBillingPeriod(mActivity)}")
        SUBSCRIPTION_DATA_LANGUAGE_CODE = "gu"
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P1D -> ${"P1D".getFullBillingPeriod(mActivity)}")
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P2D -> ${"P2D".getFullBillingPeriod(mActivity)}")

        SUBSCRIPTION_DATA_LANGUAGE_CODE = Locale.ENGLISH.language
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P1W -> ${"P1W".getFullBillingPeriod(mActivity)}")
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P2W -> ${"P2W".getFullBillingPeriod(mActivity)}")
        SUBSCRIPTION_DATA_LANGUAGE_CODE = "hi"
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P1W -> ${"P1W".getFullBillingPeriod(mActivity)}")
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P2W -> ${"P2W".getFullBillingPeriod(mActivity)}")
        SUBSCRIPTION_DATA_LANGUAGE_CODE = "gu"
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P1W -> ${"P1W".getFullBillingPeriod(mActivity)}")
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P2W -> ${"P2W".getFullBillingPeriod(mActivity)}")

        SUBSCRIPTION_DATA_LANGUAGE_CODE = Locale.ENGLISH.language
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P1M -> ${"P1M".getFullBillingPeriod(mActivity)}")
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P2M -> ${"P2M".getFullBillingPeriod(mActivity)}")
        SUBSCRIPTION_DATA_LANGUAGE_CODE = "hi"
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P1M -> ${"P1M".getFullBillingPeriod(mActivity)}")
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P2M -> ${"P2M".getFullBillingPeriod(mActivity)}")
        SUBSCRIPTION_DATA_LANGUAGE_CODE = "gu"
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P1M -> ${"P1M".getFullBillingPeriod(mActivity)}")
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P2M -> ${"P2M".getFullBillingPeriod(mActivity)}")

        SUBSCRIPTION_DATA_LANGUAGE_CODE = Locale.ENGLISH.language
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P1Y -> ${"P1Y".getFullBillingPeriod(mActivity)}")
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P2Y -> ${"P2Y".getFullBillingPeriod(mActivity)}")
        SUBSCRIPTION_DATA_LANGUAGE_CODE = "hi"
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P1Y -> ${"P1Y".getFullBillingPeriod(mActivity)}")
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P2Y -> ${"P2Y".getFullBillingPeriod(mActivity)}")
        SUBSCRIPTION_DATA_LANGUAGE_CODE = "gu"
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P1Y -> ${"P1Y".getFullBillingPeriod(mActivity)}")
        Log.e(TAG, "initView: ${SUBSCRIPTION_DATA_LANGUAGE_CODE} -> P2Y -> ${"P2Y".getFullBillingPeriod(mActivity)}")*/
    }

    override fun setDefaultAdUI() {
        super.setDefaultAdUI()
        with(mBinding) {
//            showInterstitialAds.apply {
//                this.alpha = 0.5f
//                this.isEnabled = false
//            }
            showRewardVideoAds.apply {
                this.alpha = 0.5f
                this.isEnabled = false
            }
            showRewardInterstitialAds.apply {
                this.alpha = 0.5f
                this.isEnabled = false
            }
        }
    }

    override fun initAds() {
        super.initAds()

        with(mBinding) {

            //<editor-fold desc="Interstitial Ad Work">
//            InterstitialAdHelper.loadAd(
//                fContext = mActivity,
////                onAdLoaded = {
////                    if (it) {
////                        Log.e(TAG, "Admob_ initAds: AdLoaded")
////                        showInterstitialAds.alpha = 1.0f
////                        showInterstitialAds.isEnabled = true
////                    } else {
////                        showInterstitialAds.alpha = 0.5f
////                        showInterstitialAds.isEnabled = false
////                    }
////                }
//            )
            //</editor-fold>

            //<editor-fold desc="Reward Video Ad Work">
            RewardedVideoAdHelper.loadAd(
                fContext = mActivity,
                onStartToLoadAd = {
                    showRewardVideoAds.alpha = 0.5f
                    showRewardVideoAds.isEnabled = false
                },
                onAdLoaded = {
                    showRewardVideoAds.alpha = 1f
                    showRewardVideoAds.isEnabled = true
                },
            )
            //</editor-fold>

            //<editor-fold desc="Reward Interstitial Video Ad Work">
            RewardedInterstitialAdHelper.loadAd(
                fContext = mActivity,
                onStartToLoadAd = {
                    Log.e(TAG, "initAds: RewardedInterstitialAd: onStartToLoadAd")
                    showRewardInterstitialAds.alpha = 0.5f
                    showRewardInterstitialAds.isEnabled = false
                },
                onAdLoaded = {
                    Log.e(TAG, "initAds: RewardedInterstitialAd: onAdLoaded")
                    showRewardInterstitialAds.alpha = 1f
                    showRewardInterstitialAds.isEnabled = true
                },
            )
            //</editor-fold>
        }
    }

    override fun initViewListener() {
        super.initViewListener()

        with(mBinding) {
            adsSwitch.setOnCheckedChangeListener { _, _ ->
//                if (NativeAdvancedModelHelper.getNativeAd != null) {
//                    NativeAdvancedModelHelper.destroy()
//                }
            }

            openAdsSwitch.setOnCheckedChangeListener { _, _ ->
                mActivity.save(IS_OPEN_ADS_ENABLE, !(mActivity.getBoolean(IS_OPEN_ADS_ENABLE, true)))
                Handler(Looper.getMainLooper()).postDelayed({
                    Log.e(TAG, "initViewListener: IS_OPEN_ADS_ENABLE::${mActivity.getBoolean(IS_OPEN_ADS_ENABLE, true)}")
                    finishAfterTransition()
                    triggerRebirth(mActivity)
                }, 500)
            }

            setClickListener(
                layoutHeader.ivHeaderBack,
                layoutHeader.ivHeaderRightIcon,
                showInterstitialAds,
                showFullScreenNativeAd,
                showRewardVideoAds,
                showRewardInterstitialAds,
                showNativeAds,
                showCustomNativeAds,
                showRunTimePermission,
                showDialogs,
                showBannerAds,
                showSubscriptionScreen,
            )
        }
    }

    override fun onClick(v: View) {
        when (v) {
            mBinding.layoutHeader.ivHeaderBack -> customOnBackPressed()

            mBinding.layoutHeader.ivHeaderRightIcon -> mActivity.shareApp

            mBinding.showInterstitialAds -> {
                mActivity.showInterstitialAd { isAdShowing, isShowFullScreenAd ->
                    Log.e(TAG, "onClick: isAdShowing::$isAdShowing, isShowFullScreenAd::$isShowFullScreenAd")
                }
            }

            mBinding.showFullScreenNativeAd -> {
                showFullScreenNativeAdActivity(mActivity) {}
            }

            mBinding.showRewardVideoAds -> {
                mActivity.showRewardedVideoAd(
                    onUserEarnedReward = { isUserEarnedReward ->
                        Log.e(TAG, "onClick: RewardedVideoAd: isUserEarnedReward::$isUserEarnedReward")
                        mBinding.showRewardVideoAds.alpha = 0.5f
                        mBinding.showRewardVideoAds.isEnabled = false
                    }
                )
            }

            mBinding.showRewardInterstitialAds -> {
                mActivity.showRewardedInterstitialAd(
                    onUserEarnedReward = { isUserEarnedReward ->
                        Log.e(TAG, "onClick: RewardedInterstitialAd: isUserEarnedReward::$isUserEarnedReward")
                        mBinding.showRewardInterstitialAds.alpha = 0.5f
                        mBinding.showRewardInterstitialAds.isEnabled = false
                    }
                )
            }

            mBinding.showNativeAds -> {
                launchActivity(getActivityIntent<NativeAdsBigActivity>(isAddFlag = false) { putBoolean("is_add_video_options", mBinding.adsSwitch.isChecked) })
            }

            mBinding.showCustomNativeAds -> {
//                mActivity.showInterstitialAd { _, _ ->
                launchActivity(getActivityIntent<CustomNativeAdsActivity> { putBoolean("is_add_video_options", mBinding.adsSwitch.isChecked) })
//                }
            }

            mBinding.showRunTimePermission -> {
                stopShowingOpenAdInternally()

                AksPermission.with(mActivity)
                    .permissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                    )
                    .isShowDefaultSettingDialog(false)
                    .request(
                        onGrantedResult = {},
                        onDeniedResult = {},
                        onPermanentlyDeniedResult = {
                            doNotAskAgain(it.toMutableList().getPermissionName().toString())
                        }
                    )
            }

            mBinding.showDialogs -> {
                stopShowingOpenAdInternally()

                AlertDialog.Builder(mActivity)
                    .setTitle("Hello")
                    .setMessage("Hello")
                    .create()
                    .show()
            }

            mBinding.showBannerAds -> {
                launchActivity(getActivityIntent<BannerPortraitActivity>())
            }

            mBinding.showSubscriptionScreen -> {
                launchActivity(getActivityIntent<ManageSubscriptionUiActivity>())
                /*VasuSubscriptionConfig.with(fActivity = mActivity)
                    .launchScreen {
                        Log.e(TAG, "onClick: Akshay_ Screen Finished")
                    }*/

                /*mUpdateSubscriptionDialog.show {
                        fLanguageCode,
                        useInstantAccessLottieFile,
                        isWithSliderAnimation,
                        fTimeLineMainColor,
                        fTimeLineHeaderColor,
                        fTimeLineCloseIconColor,
                        fTimeLineTrackInactiveColor,
                        fTimeLineHintTextColor,
                        fTimeLineInstantAccessHintTextColor,
                        fSecureWithPlayStoreTextColor,
                        fSecureWithPlayStoreBackgroundColor,
                        fButtonContinueTextColor,
                    ->
                    VasuSubscriptionConfig.with(fActivity = mActivity)
                        .setAppLanguageCode(fCode = fLanguageCode.takeIf { it.isNotEmpty() } ?: "en")
                        .setPrivacyPolicy(fLink = "https://www.freeprivacypolicy.com/blog/privacy-policy-url/")
                        .setTermsOfUse(fLink = "https://policies.google.com/privacy?hl=en-US")
                        .setTimeLineScreenData { fTimeLineScreenData ->
                            with(fTimeLineScreenData) {
                                this.setWithInstantAccessAnimation(isAnimated = useInstantAccessLottieFile)
//                                this.setInstantAccessLottieFile(fLottieFile = (1).takeIf { useInstantAccessLottieFile } ?: (-1))
                                this.setWithSliderAnimation(isAnimated = isWithSliderAnimation)
                                this.mainColor(fColors = fTimeLineMainColor)
                                this.headerColor(fColors = fTimeLineHeaderColor)
                                this.closeIconColor(fColors = fTimeLineCloseIconColor)
                                this.trackInactiveColor(fColors = fTimeLineTrackInactiveColor)
                                this.hintTextColor(fColors = fTimeLineHintTextColor)
                                this.instantAccessHintTextColor(fColors = fTimeLineInstantAccessHintTextColor)
                                this.secureWithPlayStoreTextColor(fColors = fSecureWithPlayStoreTextColor)
                                this.secureWithPlayStoreBackgroundColor(fColors = fSecureWithPlayStoreBackgroundColor)
                                this.buttonContinueTextColor(fColors = fButtonContinueTextColor)

                                this.setInstantAccessHint(
                                    R.string.dialog_title
                                )
                            }
                        }
                        .setViewAllPlansScreenData { fViewAllPlansScreenData ->
                            with(fViewAllPlansScreenData) {
//                                this.setPlanSelector(SelectorDrawableItem(
//                                    selectedDrawableRes = R.drawable.ic_launcher_background
//                                ))
                            }

                        }

                        .launchScreen { isUserPurchaseAnyPlan ->
                            Log.e(TAG, "onClick: Akshay_ Screen Finished isUserPurchaseAnyPlan::-> $isUserPurchaseAnyPlan")
                        }
                }*/
            }
        }
    }

    private val mUpdateSubscriptionDialog: UpdateSubscriptionDialog by lazy {
        UpdateSubscriptionDialog(mActivity)
    }

    private fun doNotAskAgain(fMessage: String) {
        stopShowingOpenAdInternally()

        mActivity.showAlert(
            fTitle = mActivity.getStringRes(R.string.dialog_title),
            fMessage = mActivity.getStringRes(R.string.dialog_messages, fMessage),
            fPositiveText = mActivity.getStringRes(android.R.string.ok),
            fNegativeText = mActivity.getStringRes(android.R.string.cancel),
            fTitleColor = Color.BLACK,
            fMessageColor = Color.BLACK,
            fButtonClickListener = object : OnAlertButtonClickListener {
                override fun onPositiveButtonClick() {
                    mActivity.packageName?.let {
                        mActivity.startActivity(AksPermission.appDetailSettingsIntent(it))
                    }
                    startShowingOpenAdInternally()
                }

                override fun onNegativeButtonClick() {
                    super.onNegativeButtonClick()
                    startShowingOpenAdInternally()
                }
            }
        )
    }
}