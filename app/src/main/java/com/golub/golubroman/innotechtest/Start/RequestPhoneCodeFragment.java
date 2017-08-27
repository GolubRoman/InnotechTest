package com.golub.golubroman.innotechtest.Start;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.golub.golubroman.innotechtest.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;

/**
 * Created by roman on 27.08.17.
 */

//  creating fragment with android annotations
@EFragment(R.layout.fragment_request_phone_code)
public class RequestPhoneCodeFragment extends DialogFragment{

//    fragment argument which was set from bundle
    @FragmentArg("currentPosition")
    protected int currentPosition;

//    view radiogroup for choosing phoneCode
    @ViewById(R.id.radio_group)
    RadioGroup radioGroup;

//    array for radiogroup items from resources
    @StringArrayRes(R.array.phone_code_titles)
    String[] phoneCodeTitles;

//    array for radiogroup items from resources to get results
    @StringArrayRes(R.array.phone_codes)
    String[] phoneCodes;

//    setting radiogroup after views are ready
    @AfterViews
    protected void afterViews(){
        setRadioGroup();
    }

//    listener to radiogroup when user chosed specific item
    private OnPhoneCodeSelectedListener phoneCodeListener;

//    defining of listener to radiogroup item choice
    public interface OnPhoneCodeSelectedListener{
        void onPhoneCodeSelected(int position, String phoneCode);
    }

//    attaching object of interface
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        phoneCodeListener = (OnPhoneCodeSelectedListener)activity;
    }

//    setting radiogroup
    private void setRadioGroup(){
//        adding elements to radiogroup
        for(int i = 0; i < 3; i++){
            RadioButton radioButton = new RadioButton(getActivity());
            radioButton.setTextSize(22);
            radioButton.setText(phoneCodeTitles[i]);
            radioGroup.addView(radioButton);
        }
//        setting start configuration
        ((RadioButton)(radioGroup.getChildAt(currentPosition))).setChecked(true);

//        listener to radiogroup choice
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                View radioButton = radioGroup.findViewById(checkedId);
                int id = radioGroup.indexOfChild(radioButton);
                phoneCodeListener.onPhoneCodeSelected(id, phoneCodes[id]);
//                closing the dialog
                dismissThis();
            }
        });
    }

//    method for closing the dialog
    protected void dismissThis(){
        this.dismiss();
    }

}
