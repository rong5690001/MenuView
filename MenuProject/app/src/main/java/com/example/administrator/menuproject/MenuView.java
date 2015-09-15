package com.example.administrator.menuproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Administrator on 2015/9/14.
 */
public class MenuView extends Fragment {

    @InjectView(R.id.navigation_btn_match)
    ImageButton navigationBtnMatch;
    @InjectView(R.id.navigation_btn_match_tv)
    TextView navigationBtnMatchTv;
    @InjectView(R.id.navigation_btn_good_match)
    ImageButton navigationBtnGoodMatch;
    @InjectView(R.id.navigation_btn_good_match_tv)
    TextView navigationBtnGoodMatchTv;
    @InjectView(R.id.navigation_btn_discount)
    ImageButton navigationBtnDiscount;
    @InjectView(R.id.navigation_btn_discount_tv)
    TextView navigationBtnDiscountTv;
    @InjectView(R.id.u01_bonusList)
    ImageButton u01BonusList;
    @InjectView(R.id.u01_bonusList_tv)
    TextView u01BonusListTv;
    @InjectView(R.id.u01_people)
    ImageButton u01People;
    @InjectView(R.id.u01_people_tv)
    TextView u01PeopleTv;
    @InjectView(R.id.s17_settting)
    ImageButton s17Settting;
    @InjectView(R.id.s17_settting_tv)
    TextView s17SetttingTv;
    @InjectView(R.id.background)
    FrameLayout background;
    @InjectView(R.id.menu_blur)
    ImageView menuBlur;
    @InjectView(R.id.menu_layout)
    LinearLayout menuLayout;
    private ViewGroup mGroup;
    private boolean dismissed = true;
    private View mView;

    private long duration = 300;
    private View blurView;

    public void show(FragmentManager manager, String tag, View blurView) {
        if (dismissed) {
            dismissed = false;
            FragmentTransaction fragmentTransaction = manager.beginTransaction();
            fragmentTransaction.add(this, tag);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            this.blurView = blurView;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.menu, null);

        mGroup = (ViewGroup) getActivity().getWindow().getDecorView();

        mGroup.addView(mView);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in);
        ButterKnife.inject(this, mView);
        menuLayout.startAnimation(animation);
        setListener();

        blurView.setDrawingCacheEnabled(true);
        blurView.buildDrawingCache();
        menuBlur.setImageBitmap(convertToBlur(blurView.getDrawingCache(), getActivity()));

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void dismiss() {
        if (dismissed) return;

        dismissed = true;
        getFragmentManager().popBackStack();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(this);
        ft.commit();

    }

    private void setListener() {

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        navigationBtnMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_out);
        animation.setDuration(duration);
        menuLayout.startAnimation(animation);
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mView.setVisibility(View.GONE);
                mGroup.removeView(mView);
            }
        }, duration);
        ButterKnife.reset(this);
    }

    public static Bitmap convertToBlur(Bitmap bmp, Context context) {
        final int radius = 20;
        if (Build.VERSION.SDK_INT > 16) {
            Log.d(MenuView.class.getSimpleName(), "VERSION.SDK_INT " + Build.VERSION.SDK_INT);
            Bitmap bitmap = bmp.copy(bmp.getConfig(), true);

            final RenderScript rs = RenderScript.create(context);
            final Allocation input = Allocation.createFromBitmap(rs, bmp, Allocation.MipmapControl.MIPMAP_NONE,
                    Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius /* e.g. 3.f */);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            return bitmap;
        }
        return bmp;
    }
}
