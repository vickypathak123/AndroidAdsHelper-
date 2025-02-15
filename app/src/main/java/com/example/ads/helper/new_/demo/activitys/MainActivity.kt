package com.example.ads.helper.new_.demo.activitys

import android.Manifest
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import com.akshay.harsoda.permission.helper.AksPermission
import com.akshay.harsoda.permission.helper.utiles.OnAlertButtonClickListener
import com.akshay.harsoda.permission.helper.utiles.getPermissionName
import com.akshay.harsoda.permission.helper.utiles.showAlert
import com.example.ads.helper.new_.demo.BuildConfig
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
import com.example.ads.helper.new_.demo.utils.SELECTED_APP_LANGUAGE_CODE
import com.example.ads.helper.new_.demo.utils.selectedAppLanguageCode
import com.example.app.ads.helper.activity.showFullScreenNativeAdActivity
import com.example.app.ads.helper.feedback.FeedBackConfig
import com.example.app.ads.helper.interstitialad.InterstitialAdHelper.showInterstitialAd
import com.example.app.ads.helper.purchase.product.AdsManager
import com.example.app.ads.helper.reward.RewardedInterstitialAdHelper
import com.example.app.ads.helper.reward.RewardedInterstitialAdHelper.showRewardedInterstitialAd
import com.example.app.ads.helper.reward.RewardedVideoAdHelper
import com.example.app.ads.helper.reward.RewardedVideoAdHelper.showRewardedVideoAd
import com.example.app.ads.helper.utils.startShowingOpenAdInternally
import com.example.app.ads.helper.utils.stopShowingOpenAdInternally

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

            val languageCodes = resources.getStringArray(R.array.subscription_screen_language).map { it.substringAfter("(").substringBefore(")") }
            val selectedIndex = languageCodes.indexOf(selectedAppLanguageCode)
            // Set the selected item in the spinner
            if (selectedIndex != -1) {
                spLanguage.setSelection(selectedIndex)
            }

//            txtLanguageCode.text = "Selected Language Code:- ($selectedAppLanguageCode)"
            txtLanguageCode.text = "Selected Language:- "

            openAdsSwitch.isChecked = mActivity.getBoolean(IS_OPEN_ADS_ENABLE, true)
            adsSwitch.isChecked = true

            with(layoutHeader) {
                ivHeaderBack.setImageDrawable(mActivity.getDrawableRes(R.drawable.ic_new_header_back))
                ivHeaderRightIcon.setImageDrawable(mActivity.getDrawableRes(R.drawable.ic_share_blue))
            }
        }
    }

    override fun setDefaultAdUI() {
        super.setDefaultAdUI()
        with(mBinding) {
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
            openAdsSwitch.setOnCheckedChangeListener { _, _ ->
                mActivity.save(IS_OPEN_ADS_ENABLE, !(mActivity.getBoolean(IS_OPEN_ADS_ENABLE, true)))
                Handler(Looper.getMainLooper()).postDelayed({
                    Log.e(TAG, "initViewListener: IS_OPEN_ADS_ENABLE::${mActivity.getBoolean(IS_OPEN_ADS_ENABLE, true)}")
                    finishAfterTransition()
                    triggerRebirth(mActivity)
                }, 500)
            }

            spLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedItem = parent?.getItemAtPosition(position)?.toString()?.substringAfter("(")?.substringBefore(")")?.takeIf { it.isNotEmpty() } ?: "en"
                    SELECTED_APP_LANGUAGE_CODE = selectedItem
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

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
                showRateApp,
                showFeedback,
            )
        }
    }

    override fun onClick(v: View) {
        when (v) {
            mBinding.layoutHeader.ivHeaderBack -> customOnBackPressed()

            mBinding.layoutHeader.ivHeaderRightIcon -> mActivity.shareApp

            mBinding.showRateApp -> {
                mRateAppDialog.show(
                    fLanguageCode = selectedAppLanguageCode,
                    onClickAskMeLater = {
                        Log.e(TAG, "onClick: onClickAskMeLater")
                    },
                    onClickNegativeReview = {
                        Log.e(TAG, "onClick: onClickNegativeReview")
                    },
                    onClickPositiveReview = {
                        Log.e(TAG, "onClick: onClickPositiveReview")
                    },
                )
            }

            mBinding.showFeedback -> {
                FeedBackConfig.with(fActivity = mActivity, fAppPackageName = mActivity.packageName, fAppVersionName = BuildConfig.VERSION_NAME)
                    .setAppLanguageCode(selectedAppLanguageCode)
                    .setFeedBackScreenData { fFeedBackScreenData ->
                        with(fFeedBackScreenData) {
//                            progressBarColor(resourceId = R.color.exit_button_text)
//                            useLightStatusBar(isLight = false)
//                            toolbarBackgroundColor(resourceId = R.color.color_accent)
//                            screenBackgroundColor(resourceId = R.color.color_primary)
//                            changeBackIcon(R.layout.item_toolbar_testing)
//                            screenTitleTextGravity(gravity = Gravity.CENTER)
//                            toolbarTextSize(resourceId = com.intuit.ssp.R.dimen._10ssp)
//                            toolbarTextFontFamily(resourceId = com.example.app.ads.helper.R.font.ads_metropolis_regular)
                        }
                    }
                    .launchScreen {
                        Log.e(TAG, "onClick: back from Feedback")
                    }
            }

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
                launchActivity(getActivityIntent<CustomNativeAdsActivity> { putBoolean("is_add_video_options", mBinding.adsSwitch.isChecked) })
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
            }
        }
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