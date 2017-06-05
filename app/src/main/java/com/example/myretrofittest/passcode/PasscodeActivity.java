package com.example.myretrofittest.passcode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.myretrofittest.HomeActivity;
import com.example.myretrofittest.R;
import com.example.myretrofittest.util.AndroidUtil;
import com.hanks.passcodeview.PasscodeView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PasscodeActivity extends AppCompatActivity {
    @Bind(R.id.passcodeView)
    PasscodeView mPasscodeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        ButterKnife.bind(this);
        mPasscodeView.setPasscodeLength(4).setLocalPasscode("1234").setListener(new PasscodeView.PasscodeViewListener() {
            @Override
            public void onFail() {
            }

            @Override
            public void onSuccess(String number) {
                AndroidUtil.jumptoNextAct(PasscodeActivity.this, HomeActivity.class);
                finish();
            }
        });
    }
}
