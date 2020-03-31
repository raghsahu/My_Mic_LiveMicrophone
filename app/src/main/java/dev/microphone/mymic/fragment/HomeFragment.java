package dev.microphone.mymic.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.ToxicBakery.viewpager.transforms.ZoomOutSlideTransformer;


import dev.microphone.mymic.R;
import dev.microphone.mymic.adapter.SliderAdapter;

import static dev.microphone.mymic.activity.MainActivity.card_live;
import static dev.microphone.mymic.activity.MainActivity.card_record;
import static dev.microphone.mymic.activity.MainActivity.ll_bottom;
import static dev.microphone.mymic.activity.MainActivity.toolbar;
import static dev.microphone.mymic.activity.MainActivity.tv_live;
import static dev.microphone.mymic.activity.MainActivity.tv_record;

public class HomeFragment extends Fragment {

    private ViewPager viewPager;
    private LinearLayout linearLayout;
    private SliderAdapter sliderAdapter;
    private int dotsCount;
    private ImageView[] dotes;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        //getActivity().setTitle("Home");
       // tv_live.setVisibility(View.GONE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.blackColor));
        ll_bottom.setBackgroundColor(getResources().getColor(R.color.blackColor));

        card_live.setCardBackgroundColor(getResources().getColor(R.color.whiteColor));
        card_record.setCardBackgroundColor(getResources().getColor(R.color.whiteColor));
        tv_live.setTextColor(getResources().getColor(R.color.blackColor));
        tv_record.setTextColor(getResources().getColor(R.color.blackColor));

        viewPager = root.findViewById(R.id.slider_pager);
        linearLayout = root.findViewById(R.id.linear_layout);


        //set slider
        sliderAdapter = new SliderAdapter(getActivity());
        viewPager.setAdapter(sliderAdapter);
        viewPager.setPageTransformer(true, new ZoomOutSlideTransformer());
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(pageChangeListener);
        dotesIndicater();


        return root;

    }





    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            //dotesIndicater(position);

            //for (int i = 0; i < dotes.length; i++) {
            /*for (ImageView dote : dotsCount) {
                dotes[position].setImageResource(R.drawable.circle_inactive);
            }*/

            for (int i = 0; i < dotsCount; i++) {
                dotes[i].setImageDrawable(getResources().getDrawable(R.drawable.circle_inactive));
            }

            dotes[position].setImageResource(R.drawable.circle_active);

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @SuppressLint("ClickableViewAccessibility")
    public void dotesIndicater() {
        dotsCount = sliderAdapter.getCount();
        dotes = new ImageView[dotsCount];
        linearLayout.removeAllViews();
        for (int i = 0; i < dotsCount; i++) {
            dotes[i] = new ImageView(getActivity());
            dotes[i].setImageResource(R.drawable.circle_inactive);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    20,
                    20
            );

            params.setMargins(4, 0, 4, 0);

            final int presentPosition = i;
            dotes[presentPosition].setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    viewPager.setCurrentItem(presentPosition);
                    return true;
                }

            });


            linearLayout.addView(dotes[i], params);

            //linearLayout.addView(dotes[i]);
            //linearLayout.bringToFront();
        }
        dotes[0].setImageResource(R.drawable.circle_active);
        /*if (dotes.length > 0) {
            dotes[i].setImageResource(R.drawable.circle_active);
        }*/
    }




}
