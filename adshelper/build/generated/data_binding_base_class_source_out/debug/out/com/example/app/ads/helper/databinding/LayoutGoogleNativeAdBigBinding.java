// Generated by view binder compiler. Do not edit!
package com.example.app.ads.helper.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Barrier;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.app.ads.helper.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class LayoutGoogleNativeAdBigBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView adAdvertiser;

  @NonNull
  public final ShapeableImageView adAppIcon;

  @NonNull
  public final TextView adBody;

  @NonNull
  public final MaterialButton adCallToAction;

  @NonNull
  public final TextView adHeadline;

  @NonNull
  public final RatingBar adStars;

  @NonNull
  public final Barrier barrierAppIcon;

  @NonNull
  public final ConstraintLayout llStar;

  @NonNull
  public final FrameLayout nativeAdMediaContainer;

  @NonNull
  public final TextView txtAd;

  @NonNull
  public final View viewAppIconHeight;

  private LayoutGoogleNativeAdBigBinding(@NonNull ConstraintLayout rootView,
      @NonNull TextView adAdvertiser, @NonNull ShapeableImageView adAppIcon,
      @NonNull TextView adBody, @NonNull MaterialButton adCallToAction,
      @NonNull TextView adHeadline, @NonNull RatingBar adStars, @NonNull Barrier barrierAppIcon,
      @NonNull ConstraintLayout llStar, @NonNull FrameLayout nativeAdMediaContainer,
      @NonNull TextView txtAd, @NonNull View viewAppIconHeight) {
    this.rootView = rootView;
    this.adAdvertiser = adAdvertiser;
    this.adAppIcon = adAppIcon;
    this.adBody = adBody;
    this.adCallToAction = adCallToAction;
    this.adHeadline = adHeadline;
    this.adStars = adStars;
    this.barrierAppIcon = barrierAppIcon;
    this.llStar = llStar;
    this.nativeAdMediaContainer = nativeAdMediaContainer;
    this.txtAd = txtAd;
    this.viewAppIconHeight = viewAppIconHeight;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static LayoutGoogleNativeAdBigBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LayoutGoogleNativeAdBigBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.layout_google_native_ad_big, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LayoutGoogleNativeAdBigBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ad_advertiser;
      TextView adAdvertiser = ViewBindings.findChildViewById(rootView, id);
      if (adAdvertiser == null) {
        break missingId;
      }

      id = R.id.ad_app_icon;
      ShapeableImageView adAppIcon = ViewBindings.findChildViewById(rootView, id);
      if (adAppIcon == null) {
        break missingId;
      }

      id = R.id.ad_body;
      TextView adBody = ViewBindings.findChildViewById(rootView, id);
      if (adBody == null) {
        break missingId;
      }

      id = R.id.ad_call_to_action;
      MaterialButton adCallToAction = ViewBindings.findChildViewById(rootView, id);
      if (adCallToAction == null) {
        break missingId;
      }

      id = R.id.ad_headline;
      TextView adHeadline = ViewBindings.findChildViewById(rootView, id);
      if (adHeadline == null) {
        break missingId;
      }

      id = R.id.ad_stars;
      RatingBar adStars = ViewBindings.findChildViewById(rootView, id);
      if (adStars == null) {
        break missingId;
      }

      id = R.id.barrier_app_icon;
      Barrier barrierAppIcon = ViewBindings.findChildViewById(rootView, id);
      if (barrierAppIcon == null) {
        break missingId;
      }

      id = R.id.ll_star;
      ConstraintLayout llStar = ViewBindings.findChildViewById(rootView, id);
      if (llStar == null) {
        break missingId;
      }

      id = R.id.native_ad_media_container;
      FrameLayout nativeAdMediaContainer = ViewBindings.findChildViewById(rootView, id);
      if (nativeAdMediaContainer == null) {
        break missingId;
      }

      id = R.id.txt_ad;
      TextView txtAd = ViewBindings.findChildViewById(rootView, id);
      if (txtAd == null) {
        break missingId;
      }

      id = R.id.view_app_icon_height;
      View viewAppIconHeight = ViewBindings.findChildViewById(rootView, id);
      if (viewAppIconHeight == null) {
        break missingId;
      }

      return new LayoutGoogleNativeAdBigBinding((ConstraintLayout) rootView, adAdvertiser,
          adAppIcon, adBody, adCallToAction, adHeadline, adStars, barrierAppIcon, llStar,
          nativeAdMediaContainer, txtAd, viewAppIconHeight);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
