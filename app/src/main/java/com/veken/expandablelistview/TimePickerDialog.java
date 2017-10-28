package com.veken.expandablelistview;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


import com.veken.expandablelistview.wheelview.OnWheelChangedListener;
import com.veken.expandablelistview.wheelview.WheelView;
import com.veken.expandablelistview.wheelview.adapter.ArrayWheelAdapter;

import java.util.Calendar;


/**
 *
 * @author Veken
 */

@SuppressLint("ValidFragment")
public class TimePickerDialog extends DialogFragment implements OnWheelChangedListener, View.OnClickListener {

    private View mContainer;
    private View btnOk;
    private View btnCancel;
    private WheelView wvProvince;
    private WheelView wvCity;
    private WheelView wvDistrict;

    private String[] years;
    private String currentYear;
    private String[] months;
    private String currentMonth;
    private String[] dates;
    private String currentDate;
    private OnDialogClickListener mListener;
    private int mStartYear;
    private int mEndYear;
    private int mCurrentYear;
    private int currentItem;
    private int mCurrentMonth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //去掉dialog的标题，需要在setContentView()之前
        this.getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = this.getDialog().getWindow();
        //去掉dialog默认的padding
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //设置dialog的位置在底部
        lp.gravity = Gravity.BOTTOM;
        //设置dialog的动画
        lp.windowAnimations = R.style.dialog_animation;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable());
        mContainer = inflater.inflate(R.layout.dialog_city_select, null);

        Calendar cal = Calendar.getInstance();
        mEndYear = cal.get(Calendar.YEAR);
        mStartYear = mEndYear - 1;
        mCurrentYear = mEndYear;
        mCurrentMonth = cal.get(Calendar.MONTH) + 1;

        initView();

        initEvent();

        updateYear();
        updateDate();

        return mContainer;
    }

    private void initView() {
        wvProvince = (WheelView) mContainer.findViewById(R.id.id_province);
        wvCity = (WheelView) mContainer.findViewById(R.id.id_city);
//        wvDistrict = (WheelView) mContainer.findViewById(R.id.id_district);
        btnOk = mContainer.findViewById(R.id.btn_ok);
        btnCancel = mContainer.findViewById(R.id.btn_cancel);

    }

    private void initEvent() {
        wvProvince.addChangingListener(this);
        wvCity.addChangingListener(this);
//        wvDistrict.addChangingListener(this);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    public void setOnDialogClickListener(OnDialogClickListener listener) {
        this.mListener = listener;
    }

    /**
     * 初始化年的数据
     */
    private void updateYear() {
        years = new String[mEndYear - mStartYear + 2];
        years[mEndYear - mStartYear + 1] = "最近三个月";
        for (int i = 0; i < mEndYear - mStartYear + 1; i++) {
            years[i] = mStartYear + i + "年";
        }
        wvProvince.setViewAdapter(new ArrayWheelAdapter<>(BaseApplication.getInstance(), years));
        currentYear = years[currentItem].substring(0, years[currentItem].length() - 1);
        wvProvince.setCurrentItem(1);
        updateMoth();
        //updateDate();
    }

    public void setStartYear(int startYear) {
        this.mStartYear = startYear;
    }

    public void setEndYear(int endYear) {
        this.mEndYear = endYear;
    }

    public void setCurrentYear(int currentYear) {
        this.mCurrentYear = currentYear;
    }

    /**
     * 初始化月的数据
     */
    private void updateMoth() {
        if (wvProvince.getCurrentItem() == 2) {
            currentYear = years[wvProvince.getCurrentItem()].substring(0);
        } else {
            currentYear = years[wvProvince.getCurrentItem()].substring(0, years[wvProvince.getCurrentItem()].length() - 1);
        }
        if(wvProvince.getCurrentItem()==1){
            wvCity.setCurrentItem(mCurrentMonth - 1);
        }else{
            wvCity.setCurrentItem(0);
        }
        //当前年
        if (currentYear.equals(mCurrentYear + "")) {
            //统计当前年的当前所有月份
            months = new String[mCurrentMonth];
            for (int i = 0; i < mCurrentMonth; i++) {
                months[i] = i + 1 + "月";
            }
            currentMonth = months[wvCity.getCurrentItem()].substring(0, months[wvCity.getCurrentItem()].length() - 1);
        } else if (currentYear.equals(mCurrentYear - 1 + "")) {
            months = new String[12];
            for (int i = 0; i < 12; i++) {
                months[i] = i + 1 + "月";
            }
            currentMonth = months[wvCity.getCurrentItem()].substring(0, months[wvCity.getCurrentItem()].length() - 1);
        }
        //最近三个月
        else {
            months = new String[1];
//            months[0]=(mCurrentMonth-2)+"月,"+(mCurrentMonth-1)+"月,"+mCurrentMonth+"月";
            months[0] = "  ";
            currentMonth = mCurrentMonth + "";
        }
        wvCity.setViewAdapter(new ArrayWheelAdapter<>(BaseApplication.getInstance(), months));
//        wvCity.setCurrentItem(0);
    }

    private void updateDate() {
//        wvDistrict.setCurrentItem(0);
        currentMonth = months[wvCity.getCurrentItem()].substring(0, months[wvCity.getCurrentItem()].length() - 1);
//
//        int year = Integer.parseInt(currentYear);
//        int month = Integer.parseInt(currentMonth);
//        int days;
//        if (month == 2) {
//            //如果是闰年
//            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
//                days = 29;
//                //如果是平年
//            } else {
//                days = 28;
//            }
//            //如果是第4、6、9、11月
//        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
//            days = 30;
//        } else {
//            days = 31;
//        }
//
//        dates = new String[days];
//        for (int i = 0; i < days; i++) {
//            dates[i] = i + 1 + "日";
//        }
//
//        wvDistrict.setViewAdapter(new ArrayWheelAdapter<String>(BaseApplication.getInstance(), dates));
//        currentDate = dates[0].substring(0, dates[0].length() - 1);
    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == wvProvince) {
            updateMoth();
        } else if (wheel == wvCity) {
            updateDate();
        }
//         else if (wheel == wvDistrict) {
//            currentDate = dates[newValue].substring(0, dates[newValue].length() - 1);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                mListener.onBtnClick(currentYear, currentMonth, "");
//                dismiss();
                break;

            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    public interface OnDialogClickListener {

        void onBtnClick(String year, String month, String data);
    }
}
