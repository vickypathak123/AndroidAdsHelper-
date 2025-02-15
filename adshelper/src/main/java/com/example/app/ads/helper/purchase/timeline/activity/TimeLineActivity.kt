package com.example.app.ads.helper.purchase.timeline.activity

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.view.View
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import com.example.app.ads.helper.R
import com.example.app.ads.helper.base.BaseActivity
import com.example.app.ads.helper.base.BaseBindingActivity
import com.example.app.ads.helper.base.utils.beVisibleIf
import com.example.app.ads.helper.base.utils.disable
import com.example.app.ads.helper.base.utils.enable
import com.example.app.ads.helper.base.utils.getColorRes
import com.example.app.ads.helper.base.utils.gone
import com.example.app.ads.helper.base.utils.isTiramisuPlus
import com.example.app.ads.helper.base.utils.visible
import com.example.app.ads.helper.base.utils.withUnderLine
import com.example.app.ads.helper.databinding.ActivityTimeLineBinding
import com.example.app.ads.helper.launcher.Launcher
import com.example.app.ads.helper.notification.NotificationDataModel
import com.example.app.ads.helper.notification.scheduleNotification
import com.example.app.ads.helper.purchase.IS_ENABLE_TEST_PURCHASE
import com.example.app.ads.helper.purchase.IS_FROM_SPLASH
import com.example.app.ads.helper.purchase.SHOW_CLOSE_AD_FOR_TIME_LINE_SCREEN
import com.example.app.ads.helper.purchase.SUBSCRIPTION_DATA_LANGUAGE_CODE
import com.example.app.ads.helper.purchase.SUBSCRIPTION_PRIVACY_POLICY
import com.example.app.ads.helper.purchase.SUBSCRIPTION_TERMS_OF_USE
import com.example.app.ads.helper.purchase.VasuSubscriptionConfig.NotificationData
import com.example.app.ads.helper.purchase.fireSubscriptionEvent
import com.example.app.ads.helper.purchase.product.AdsManager
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.getBillingPeriodCount
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.getBillingPeriodName
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.getFullBillingPeriod
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.getSKU
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.isMonthlySKU
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.isWeeklySKU
import com.example.app.ads.helper.purchase.product.ProductPurchaseHelper.isYearlySKU
import com.example.app.ads.helper.purchase.timeline.utils.TimeLineScreenDataModel
import com.example.app.ads.helper.purchase.utils.SubscriptionEventType
import com.example.app.ads.helper.purchase.utils.getEventParamBundle
import com.example.app.ads.helper.remoteconfig.mVasuSubscriptionRemoteConfigModel
import com.example.app.ads.helper.utils.getLocalizedString
import com.example.app.ads.helper.utils.isOnline
import com.example.app.ads.helper.utils.isRTLDirectionFromLocale
import com.example.app.ads.helper.utils.logE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale

internal class TimeLineActivity : BaseBindingActivity<ActivityTimeLineBinding>() {

    @get:StringRes
    private val listOfInstantAccessHint: ArrayList<Int>
        get() = screenDataModel?.listOfInstantAccessHint?.takeIf { it.isNotEmpty() } ?: arrayListOf(
            R.string.instant_access_hint_1,
            R.string.instant_access_hint_2,
            R.string.instant_access_hint_3,
            R.string.instant_access_hint_4,
            R.string.instant_access_hint_5,
            R.string.instant_access_hint_6,
        )

    /**
     * Index == 2 ::-> Try 3 days for $0
     * Index == 1 ::-> START MY FREE TRIAL
     * Index == else ::-> CONTINUE
     */
    private val mPurchaseButtonTextIndex: Int get() = mVasuSubscriptionRemoteConfigModel.purchaseButtonTextIndex

    private val maxValue = 40f
    private var currentValue = 0f
    private val handler = Handler(Looper.getMainLooper())

    private val mainColor: ColorStateList get() = screenDataModel?.mainColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_time_line_main_color))
    private val headerColor: ColorStateList get() = screenDataModel?.headerColor ?: mainColor
    private val closeIconColor: ColorStateList get() = screenDataModel?.closeIconColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_time_line_close_icon_color))
    private val trackInactiveColor: ColorStateList get() = screenDataModel?.trackInactiveColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_time_line_track_inactive_color))
    private val hintTextColor: ColorStateList get() = screenDataModel?.hintTextColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_time_line_hint_text_color))
    private val instantAccessHintTextColor: ColorStateList get() = screenDataModel?.instantAccessHintTextColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_time_line_instant_access_hint_text_color))
    private val secureWithPlayStoreTextColor: ColorStateList get() = screenDataModel?.secureWithPlayStoreTextColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_secure_with_play_store_text_color))
    private val secureWithPlayStoreBackgroundColor: ColorStateList get() = screenDataModel?.secureWithPlayStoreBackgroundColor ?: ColorStateList.valueOf(mActivity.getColorRes(R.color.default_secure_with_play_store_background_color))
    private val buttonContinueTextColor: ColorStateList get() = screenDataModel?.buttonContinueTextColor ?: ColorStateList.valueOf(Color.WHITE)

    @get:RawRes
    private val instantAccessLottieFile: Int get() = screenDataModel?.instantAccessLottieFileRawRes ?: R.raw.lottie_subscription_unlock_today_bg
    private val isWithInstantAccessAnimation: Boolean get() = screenDataModel?.isWithInstantAccessAnimation ?: false
    private val isWithSliderAnimation: Boolean get() = screenDataModel?.isWithSliderAnimation ?: false

    companion object {
        private var screenDataModel: TimeLineScreenDataModel? = null
        private var notificationData: NotificationData? = null
        private var onViewAllPlans: () -> Unit = {}
        private var onScreenFinish: (isUserPurchaseAnyPlan: Boolean) -> Unit = {}

        private var reviewDialogData: Pair<String, String> = Pair("", "")

        internal var onPurchaseFromMorePlanScreen: () -> Unit = {}

        internal fun launchScreen(
            fActivity: Activity,
            screenDataModel: TimeLineScreenDataModel,
            notificationData: NotificationData,
            reviewDialogData: Pair<String, String>,
            onViewAllPlans: () -> Unit = {},
            onScreenFinish: (isUserPurchaseAnyPlan: Boolean) -> Unit,
        ) {
            Companion.onViewAllPlans = onViewAllPlans
            Companion.onScreenFinish = onScreenFinish
            Companion.screenDataModel = screenDataModel
            Companion.notificationData = notificationData
            Companion.reviewDialogData = reviewDialogData

            val lIntent = Intent(fActivity, TimeLineActivity::class.java)

            @AnimatorRes @AnimRes val lEnterAnimId: Int = android.R.anim.fade_in
            @AnimatorRes @AnimRes val lExitAnimId: Int = android.R.anim.fade_out

            fActivity.runOnUiThread {
                if (isTiramisuPlus) {
                    val options = ActivityOptions.makeCustomAnimation(fActivity, lEnterAnimId, lExitAnimId)
                    fActivity.startActivity(lIntent, options.toBundle())
                } else {
                    fActivity.startActivity(lIntent)
                    @Suppress("DEPRECATION")
                    fActivity.overridePendingTransition(lEnterAnimId, lExitAnimId)
                }
            }
        }
    }

    override fun getScreenLanguageCode(): String {
        return SUBSCRIPTION_DATA_LANGUAGE_CODE.takeIf { it.isNotEmpty() } ?: "en"
    }

    override fun setBinding(): ActivityTimeLineBinding = ActivityTimeLineBinding.inflate(layoutInflater)

    override fun getActivityContext(): BaseActivity = this@TimeLineActivity

    override fun setParamBeforeLayoutInit() {
        super.setParamBeforeLayoutInit()
        setEdgeToEdgeLayout()
    }

    private var isUserPurchaseAnyPlan: Boolean = false

    override fun onResume() {
        super.onResume()
        mBinding.lySubscribeButton.root.enable
        if (isOnPause) {
            isOnPause = false
            setBillingListener(fWhere = "onResume")
        }
    }

    private fun setBillingListener(fWhere: String): Job {
        val job: Job = CoroutineScope(Dispatchers.IO).launch {
            logE(TAG, "$fWhere: Set Listener")
            ProductPurchaseHelper.setPurchaseListener(object : ProductPurchaseHelper.ProductPurchaseListener {
                override fun onBillingSetupFinished() {
                    ProductPurchaseHelper.initProductsKeys(fContext = mActivity) {
                        setProductData()
                    }
                }

                override fun onPurchasedSuccess() {
                    super.onPurchasedSuccess()
                    logE(TAG, "onPurchasedSuccess: ")
                    CoroutineScope(Dispatchers.Main).launch {
                        mActivity.runOnUiThread {
                            ProductPurchaseHelper.getFreeTrialProductInfo?.let { productInfo ->
                                when {
                                    productInfo.id.getSKU.isMonthlySKU -> fireSubscriptionEvent(fEventType = SubscriptionEventType.MONTHLY_FREE_TRAIL_SUBSCRIBE(paramBundle = getEventParamBundle(productInfo = productInfo)))
                                    productInfo.id.getSKU.isWeeklySKU -> fireSubscriptionEvent(fEventType = SubscriptionEventType.WEEKLY_FREE_TRAIL_SUBSCRIBE(paramBundle = getEventParamBundle(productInfo = productInfo)))
                                    productInfo.id.getSKU.isYearlySKU -> fireSubscriptionEvent(fEventType = SubscriptionEventType.YEARLY_FREE_TRAIL_SUBSCRIBE(paramBundle = getEventParamBundle(productInfo = productInfo)))
                                }

                                notificationData?.let { data ->
                                    val freeTrialPeriodCount = productInfo.actualFreeTrialPeriod.getBillingPeriodCount()
                                    val periodCount: Int = (freeTrialPeriodCount - (2.takeIf { freeTrialPeriodCount <= 3 } ?: 5))

                                    val periodString: String = getLocalizedString<String>(
                                        context = mActivity,
                                        fLocale = Locale("en"),
                                        resourceId = R.string.period_days.takeIf { periodCount > 1 } ?: R.string.period_day,
                                    )

                                    AdsManager(mActivity).notificationData = NotificationDataModel(
                                        intentClass = data.intentClass::class.java.name,
                                        notificationIcon = data.notificationIcon,
                                        notificationId = data.notificationId,
                                        notificationChannelId = data.notificationChannelId,
                                        notificationChannelName = data.notificationChannelName,
                                        actualFreeTrialPeriod = getLocalizedString<String>(
                                            context = mActivity,
                                            fLocale = Locale("en"),
                                            resourceId = R.string.str_1_str_2,
                                            formatArgs = arrayOf("$periodCount", periodString)
                                        ),
                                    )

                                    scheduleNotification(intervalMillis = ((2.takeIf { freeTrialPeriodCount <= 3 } ?: 5) - 1) * DateUtils.DAY_IN_MILLIS)
                                    logE(TAG, "Saved Notification Data::-> \n${AdsManager(mActivity).notificationData}")
                                }

                            }

                            isUserPurchaseAnyPlan = true
                            mBinding.ivClose.performClick()
                        }
                    }
                }
            })
        }

        return job
    }

    override fun initView() {
        super.initView()

//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

//        getEdgeToEdgeMargin()

        fireSubscriptionEvent(fEventType = SubscriptionEventType.TIME_LINE_SCREEN_OPEN)

        onPurchaseFromMorePlanScreen = {
            CoroutineScope(Dispatchers.Main).launch {
                mActivity.runOnUiThread {
                    isUserPurchaseAnyPlan = true
                    mBinding.ivClose.performClick()
                }
            }
        }

        val job: Job = setBillingListener(fWhere = "initView")
        runBlocking {
            job.join()
            CoroutineScope(Dispatchers.IO).launch {
                logE(TAG, "initView: InitBilling")
                ProductPurchaseHelper.initBillingClient(fContext = mActivity)

                setProductData()
            }
        }

        with(mBinding) {
            root.layoutDirection = View.LAYOUT_DIRECTION_RTL.takeIf { isRTLDirectionFromLocale } ?: View.LAYOUT_DIRECTION_LTR
            root.textDirection = View.LAYOUT_DIRECTION_RTL.takeIf { isRTLDirectionFromLocale } ?: View.LAYOUT_DIRECTION_LTR

            timeLineSlider.rotation = 270f.takeIf { isRTLDirectionFromLocale } ?: 90f
            lySubscribeButton.lottieBtnContinue.scaleX = (-1f).takeIf { isRTLDirectionFromLocale } ?: 1f

            txtHaveDoubts.apply {
                this.setTextColor(headerColor)
                this.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.have_doubts)
            }

            txtStartWithAFreeTrial.apply {
                this.setTextColor(mainColor)
                this.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.start_with_a_free_trial)
            }

            timeLineSlider.apply {
                this.trackActiveTintList = mainColor
                this.trackInactiveTintList = trackInactiveColor
            }

            ivCheckNow.apply {
                this.setBackgroundColor(mainColor.defaultColor)
            }

            txtNow.apply {
                this.setTextColor(mainColor)
                this.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.now)
            }

            ivUnlockToday.apply {
                this.setBackgroundColor(mainColor.defaultColor)
                this.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN)
            }

            ivUnlockTodayBg.apply {
                this.setBackgroundColor(mainColor.defaultColor)
            }

            txtTodayGetInstantAccess.apply {
                this.setTextColor(mainColor)
                this.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.today_get_instant_access)
            }

            ivTrialReminder.apply {
                this.setBackgroundColor(trackInactiveColor.defaultColor)
                this.setColorFilter(mainColor.defaultColor, android.graphics.PorterDuff.Mode.SRC_IN)
            }

            txtTrialReminder.apply {
                this.setTextColor(mainColor)
            }

            ivPremiumBegins.apply {
                this.setBackgroundColor(trackInactiveColor.defaultColor)
                this.setColorFilter(mainColor.defaultColor, android.graphics.PorterDuff.Mode.SRC_IN)
            }

            txtPremiumBegins.apply {
                this.setTextColor(mainColor)
            }

            txtFreeThenPerPeriod.apply {
                this.setTextColor(mainColor)
            }

            lySubscribeButton.btnContinue.apply {
                this.setCardBackgroundColor(mainColor)
            }

            txtPayNothingNow.apply {
                this.setTextColor(mainColor)
                this.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.pay_nothing_now)
            }

            ivClose.apply {
                this.setColorFilter(closeIconColor.defaultColor, android.graphics.PorterDuff.Mode.SRC_IN)
            }

            txtUnlockAllTheFeatures.apply {
                this.setTextColor(hintTextColor)
                this.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.unlock_all_the_features)
            }

            txtTrialReminderHint.apply {
                this.setTextColor(hintTextColor)
                this.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.trial_reminder_hint)
            }

            txtPremiumBeginsHint.apply {
                this.setTextColor(hintTextColor)
                this.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.premium_begins_hint)
            }

            txtViewAllPlans.apply {
                this.setTextColor(hintTextColor)
                this.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.view_all_plans).withUnderLine
            }

            txtTermsOfUse.apply {
                this.setTextColor(hintTextColor)
                this.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.terms_of_use)
            }

            txtPrivacyPolicy.apply {
                this.setTextColor(hintTextColor)
                this.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.privacy_policy)
            }

            txtInstantAccessHint.apply {
                this.setTextColor(instantAccessHintTextColor)

                val finalString: StringBuilder = StringBuilder()
                listOfInstantAccessHint.forEachIndexed { index, hint ->
                    finalString.append(
                        getLocalizedString<String>(
                            context = mActivity,
                            resourceId = R.string.instant_access_hint_dot,
                            formatArgs = arrayOf(
                                getLocalizedString<String>(
                                    context = mActivity,
                                    resourceId = hint,
                                )
                            )
                        )
                    )

                    if (index < listOfInstantAccessHint.size - 1) {
                        finalString.append("\n")
                    }
                }

                this.text = finalString.toString().trim()
            }

            lySecureWithPlayStore.ivSecureWithPlayStore.apply {
                this.setColorFilter(secureWithPlayStoreTextColor.defaultColor, android.graphics.PorterDuff.Mode.SRC_IN)
            }

            lySecureWithPlayStore.txtSecureWithPlayStore.apply {
                this.setTextColor(secureWithPlayStoreTextColor)
                this.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.cancel_anytime_secure_with_play_store)
                this.isSelected = true
            }

            lySecureWithPlayStore.ivSecureWithPlayStoreBg.apply {
                this.setBackgroundColor(secureWithPlayStoreBackgroundColor.defaultColor)
            }

            lySubscribeButton.txtBtnContinue.apply {
                this.setTextColor(buttonContinueTextColor)
            }

            if (isWithInstantAccessAnimation) {
                lottieUnlockTodayBg.apply {
                    this.setAnimation(instantAccessLottieFile)
                    this.visible
                }
            } else {
                lottieUnlockTodayBg.apply {
                    this.cancelAnimation()
                    this.gone
                }
            }
            ivUnlockTodayBg.beVisibleIf(!isWithInstantAccessAnimation)

            if (isWithSliderAnimation) {
                lottieUnlockTodayBg.gone
                ivUnlockTodayBg.apply {
                    this.setBackgroundColor(trackInactiveColor.defaultColor)
                    this.visible
                }
                ivUnlockToday.apply {
                    this.setBackgroundColor(trackInactiveColor.defaultColor)
                    this.setColorFilter(mainColor.defaultColor, android.graphics.PorterDuff.Mode.SRC_IN)
                }
                updateSlider()
            }
        }
    }

    override fun initViewListener() {
        super.initViewListener()
        with(mBinding) {
            setClickListener(
                ivClose,
                txtPayNothingNow,
                txtViewAllPlans,
                txtTermsOfUse,
                txtPrivacyPolicy,
                lySubscribeButton.root
            )
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        with(mBinding) {
            when (v) {
                txtPayNothingNow,
                ivClose -> customOnBackPressed()

                txtTermsOfUse -> {
                    if (SUBSCRIPTION_TERMS_OF_USE.isNotEmpty()) {
                        Launcher.openAnyLink(
                            context = mActivity,
                            uri = SUBSCRIPTION_TERMS_OF_USE,
                            toolbarColor = mainColor.defaultColor,
                            isNightMode = false
                        )
                    } else {
                    }
                }

                txtPrivacyPolicy -> {
                    if (SUBSCRIPTION_PRIVACY_POLICY.isNotEmpty()) {
                        Launcher.openPrivacyPolicy(
                            context = mActivity,
                            fLink = SUBSCRIPTION_PRIVACY_POLICY,
                            toolbarColor = mainColor.defaultColor,
                            isNightMode = false
                        )
                    } else {
                    }
                }

                txtViewAllPlans -> onViewAllPlans.invoke()

                lySubscribeButton.root -> {
                    ProductPurchaseHelper.getFreeTrialProductInfo?.let { productInfo ->
                        lySubscribeButton.root.disable
                        if (IS_ENABLE_TEST_PURCHASE) {
                            ProductPurchaseHelper.fireTestingPurchase(context = mActivity)
                        } else {
                            ProductPurchaseHelper.purchase(activity = mActivity, productId = productInfo.id)
                        }
                    }
                }

                else -> {}
            }
        }
    }

    private fun updateSlider() {
        if (currentValue <= maxValue) {
            currentValue += 1f  // Increment the current value
            mBinding.timeLineSlider.value = currentValue
            handler.postDelayed({
                mActivity.runOnUiThread {
                    if (currentValue == 12f) {
                        with(mBinding) {
                            ivUnlockToday.apply {
                                this.setBackgroundColor(mainColor.defaultColor)
                                this.setColorFilter(Color.WHITE, android.graphics.PorterDuff.Mode.SRC_IN)
                            }
                            ivUnlockTodayBg.apply {
                                this.setBackgroundColor(mainColor.defaultColor)
                                this.beVisibleIf(!isWithInstantAccessAnimation)
                            }
                            lottieUnlockTodayBg.beVisibleIf(isWithInstantAccessAnimation)
                        }
                    }
                }

                updateSlider()
            }, 50)
        }
    }

    private fun setProductData() {
        CoroutineScope(Dispatchers.IO).launch {
            ProductPurchaseHelper.getFreeTrialProductInfo?.let { productInfo ->
                CoroutineScope(Dispatchers.Main).launch {
                    mActivity.runOnUiThread {
                        with(mBinding) {
                            val freeTrialPeriodCount = productInfo.actualFreeTrialPeriod.getBillingPeriodCount()

                            txtFreeThenPerPeriod.apply {
                                this.text = getLocalizedString<String>(
                                    context = mActivity,
                                    resourceId = R.string.free_then_per_period,
                                    formatArgs = arrayOf(
                                        productInfo.actualFreeTrialPeriod.getFullBillingPeriod(context = mActivity),
                                        productInfo.formattedPrice,
                                        productInfo.actualBillingPeriod.getBillingPeriodName(context = mActivity)
                                    )
                                ).lowercase()
                            }

                            lySubscribeButton.txtBtnContinue.apply {
//                                this.text = getLocalizedString<String>(context = mActivity, resourceId = R.string.start_my_free_trial)
                                this.text = getLocalizedString<String>(
                                    context = mActivity,
                                    resourceId = R.string.try_period_for_price.takeIf {
                                        mPurchaseButtonTextIndex == 2
                                    } ?: R.string.start_my_free_trial.takeIf {
                                        mPurchaseButtonTextIndex == 1
                                    } ?: R.string.continue_,
                                    formatArgs = arrayOf(
                                        productInfo.actualFreeTrialPeriod.getFullBillingPeriod(context = mActivity).lowercase(),
                                        "${productInfo.priceCurrencySymbol}0"
                                    )
                                )

                                this.isAllCaps = (mPurchaseButtonTextIndex != 2)
                            }

                            txtTrialReminder.apply {
                                this.text = getLocalizedString<String>(
                                    context = mActivity,
                                    resourceId = R.string.str_1_str_2,
                                    formatArgs = arrayOf(
                                        getLocalizedString<String>(
                                            context = mActivity,
                                            resourceId = R.string.day_title,
                                            formatArgs = arrayOf("2".takeIf { freeTrialPeriodCount <= 3 } ?: "5")
                                        ),
                                        getLocalizedString<String>(
                                            context = mActivity,
                                            resourceId = R.string.trial_reminder
                                        ),
                                    )
                                )
                            }

                            txtPremiumBegins.apply {
                                this.text = getLocalizedString<String>(
                                    context = mActivity,
                                    resourceId = R.string.str_1_str_2,
                                    formatArgs = arrayOf(
                                        getLocalizedString<String>(
                                            context = mActivity,
                                            resourceId = R.string.day_title,
                                            formatArgs = arrayOf("$freeTrialPeriodCount")
                                        ),
                                        getLocalizedString<String>(
                                            context = mActivity,
                                            resourceId = R.string.premium_begins
                                        ),
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onScreenFinishing() {
        super.onScreenFinishing()
        onScreenFinish.invoke(isUserPurchaseAnyPlan)
    }

    override fun needToShowBackAd(): Boolean {
        var isShowAd = false
        if (mBinding.ivClose.isPressed || isSystemBackButtonPressed || isFromReviewDialog) {
            if (IS_FROM_SPLASH && SHOW_CLOSE_AD_FOR_TIME_LINE_SCREEN) {
                isShowAd = true
            }
        }
        return isShowAd
    }

    override fun needToShowReviewDialog(): Boolean {
        return !isUserPurchaseAnyPlan && isOnline && (!AdsManager(context = mActivity).isReviewDialogOpened)
    }

    private var isFromReviewDialog: Boolean = false

    override fun showReviewDialog(onNextAction: () -> Unit) {
        super.showReviewDialog(onNextAction)
        mReviewDialog.show(
            fPackageName = reviewDialogData.first,
            fVersionName = reviewDialogData.second,
            fLanguageCode = SUBSCRIPTION_DATA_LANGUAGE_CODE,
            onDismiss = {
                isFromReviewDialog = true
                onNextAction.invoke()
            },
        )
    }

    override fun customOnBackPressed() {
        if (needToShowReviewDialog()) {
            super.customOnBackPressed()
        } else {
            if (mBinding.ivClose.isPressed || isSystemBackButtonPressed || isFromReviewDialog) {
                fireSubscriptionEvent(fEventType = SubscriptionEventType.TIME_LINE_SCREEN_CLOSE)
            }
            super.customOnBackPressed()
            isSystemBackButtonPressed = false
        }
    }


}