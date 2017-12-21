package com.modla.andy.processingcardboard;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Contains two sub-views to provide a simple stereo HUD.
 */
public class CardboardOverlayView extends LinearLayout {
	private static final String TAG = CardboardOverlayView.class
			.getSimpleName();
	private final CardboardOverlayEyeView mLeftView;
	private final CardboardOverlayEyeView mRightView;
	private AlphaAnimation mTextFadeAnimation;

	Animation zoom_out_anim;

	Animator x;

	public CardboardOverlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(HORIZONTAL);

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, 1.0f);
		params.setMargins(0, 0, 0, 0);

		mLeftView = new CardboardOverlayEyeView(context, attrs);
		mLeftView.setLayoutParams(params);
		addView(mLeftView);

		mRightView = new CardboardOverlayEyeView(context, attrs);
		mRightView.setLayoutParams(params);
		addView(mRightView);

		// Set some reasonable defaults.
		setDepthOffset(0.016f);
        setColor(Color.rgb(255, 255, 255));
		setVisibility(View.VISIBLE);

		mTextFadeAnimation = new AlphaAnimation(1.0f, 0.0f);
		mTextFadeAnimation.setDuration(10000);



	}

	public void show3DToast(String message) {
		setText(message);
		setTextAlpha(1f);
		mTextFadeAnimation.setAnimationListener(new EndAnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				setTextAlpha(0f);
			}
		});
		startAnimation(mTextFadeAnimation);
	}

	public void show3DImage(int mScore, Context context) {
		setImg(mScore, context);

	}

	public void setAnimation(Context c) {

		zoom_out_anim = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
		zoom_out_anim.setDuration(Constants.TIME_VALUE*1000);

		/*zoom_out_anim.setRepeatCount(Animation.INFINITE);
		zoom_out_anim.setRepeatMode(Animation.INFINITE);*/

		mLeftView.textView.startAnimation(zoom_out_anim);
		mRightView.textView.startAnimation(zoom_out_anim);

        zoom_out_anim.setAnimationListener(new EndAnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {

                mLeftView.textView.setVisibility(View.GONE);
                mLeftView.textView.startAnimation(zoom_out_anim);

                mRightView.textView.setVisibility(View.GONE);
                mRightView.textView.startAnimation(zoom_out_anim);
            }

            @Override
            public void onAnimationStart(Animation animation) {
                super.onAnimationStart(animation);
                mLeftView.textView.setVisibility(View.VISIBLE);
                mRightView.textView.setVisibility(View.VISIBLE);

            }
        });
    }


	private abstract class EndAnimationListener implements
			Animation.AnimationListener {
		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}
	}

	private void setDepthOffset(float offset) {
		mLeftView.setOffset(offset);
		mRightView.setOffset(-offset);
	}

	// ---------------------------------------------------------------------------------------------

	/**
	 * this method is used to show background and text
	 * @param mScore
	 * @param context
	 */
	private void setImg(int mScore, Context context) {

		Log.e("mScore==>>", "" + mScore);

		switch (mScore) {
		case 0:
			mLeftView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mLeftView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			mRightView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mRightView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			break;
		/*case 1:
			mLeftView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mLeftView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			mRightView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mRightView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			break;
		case 2:
			mLeftView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mLeftView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			mRightView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mRightView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			break;
		case 3:
			mLeftView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mLeftView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			mRightView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mRightView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			break;
		case 4:
			mLeftView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mLeftView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			mRightView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mRightView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			break;
		case 5:
			mLeftView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mLeftView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			mRightView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mRightView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			break;
		case 6:
			mLeftView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mLeftView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			mRightView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mRightView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			break;
		case 7:
			mLeftView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mLeftView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			mRightView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mRightView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			break;*/
		default:
			mLeftView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mLeftView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			mRightView.imageView.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mRightView.imageView.setBackgroundResource(Constants.BACKGROUND_VALUE );
			break;
			/*Intent intent = new Intent(context, CardBoardActivity.class);
			context.startActivity(intent);*/
		}

	}

	// ------------------------------------------------------------------------------------------

	private void setText(String text) {
		mLeftView.setText(text);
		mRightView.setText(text);
	}

	private void setTextAlpha(float alpha) {
		mLeftView.setTextViewAlpha(alpha);
		mRightView.setTextViewAlpha(alpha);
	}

	private void setColor(int color) {
		mLeftView.setColor(color);
		mRightView.setColor(color);
	}

	/**
	 * A simple view group containing some horizontally centered text underneath
	 * a horizontally centered image.
	 * 
	 * This is a helper class for CardboardOverlayView.
	 */
	private class CardboardOverlayEyeView extends ViewGroup {
		private final ImageView imageView;
		private final TextView textView;
		private float offset;

		public CardboardOverlayEyeView(Context context, AttributeSet attrs) {
			super(context, attrs);
			imageView = new ImageView(context, attrs);
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imageView.setAdjustViewBounds(true); // Preserve aspect ratio.
			addView(imageView);

			textView = new TextView(context, attrs);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 130.0f);
			textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
			textView.setGravity(Gravity.CENTER);


            textView.setTextColor(Color.parseColor("#ffffff"));
            setText("LOVE");
			addView(textView);

			zoom_out_anim = AnimationUtils.loadAnimation(context, R.anim.zoom_out);
			zoom_out_anim.setDuration(Constants.TIME_VALUE*1000);

			textView.startAnimation(zoom_out_anim);



			zoom_out_anim.setAnimationListener(new EndAnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {

                    textView.setVisibility(View.GONE);
                    textView.startAnimation(zoom_out_anim);

                }

                @Override
                public void onAnimationStart(Animation animation) {
                    super.onAnimationStart(animation);
                    textView.setVisibility(View.VISIBLE);
                }

            });
		}


		public void setColor(int color) {
			textView.setTextColor(color);
		}

		public void setText(String text) {
			textView.setText(text);
		}

		public void setTextViewAlpha(float alpha) {
			textView.setAlpha(alpha);
		}

		public void setOffset(float offset) {
			this.offset = offset;
		}

		/**
		 * to adjust the position of text
		 * @param changed
     		 * @param left
		 * @param top
		 * @param right
		 * @param bottom
		 */
		@Override
		protected void onLayout(boolean changed, int left, int top, int right,
				int bottom) {
			// Width and height of this ViewGroup.
			final int width = right - left;
			final int height = bottom - top;

			// The size of the image, given as a fraction of the dimension as a
			// ViewGroup. We multiply
			// both width and heading with this number to compute the image's
			// bounding box. Inside the
			// box, the image is the horizontally and vertically centered.
			final float imageSize = 1.0f;

            // The fraction of this ViewGroup's height by which we shift the
            // image off the ViewGroup's
			// center. Positive values shift downwards, negative values shift
			// upwards.
			// final float verticalImageOffset = -0.07f;
			final float verticalImageOffset = -0.00f;

			// Vertical position of the text, specified in fractions of this
			// ViewGroup's height.


            // ***** changes *****//

			//final float verticalTextPos = 0.52f;
            final float verticalTextPos = 0.35f;

			// Layout ImageView
			float imageMargin = (1.0f - imageSize) / 2.0f;

			//float leftMargin = (int) (width * (imageMargin + offset));
            // **** changes *****//
            float leftMargin = (int) (width * (imageMargin + offset));
			float topMargin = (int) (height * (imageMargin + verticalImageOffset));
			imageView.layout((int) leftMargin, (int) topMargin,
					(int) (leftMargin + width * imageSize),
					(int) (topMargin + height * imageSize));

			// Layout TextView
			leftMargin = offset * width;
			topMargin = height * verticalTextPos;
            textView.layout((int) leftMargin, (int) topMargin,
                    (int) (leftMargin + width), (int) (topMargin + height
                            * (1f - verticalTextPos)));
		}
	}
}