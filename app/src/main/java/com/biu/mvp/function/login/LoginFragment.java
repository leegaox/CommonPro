package com.biu.mvp.function.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.biu.modulebase.common.base.BaseFragment;
import com.biu.modulebase.common.util.Util;
import com.biu.mvp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author Lee
 * @Title: {标题}
 * @Description:{描述}
 * @date 2017/1/19
 */
public class LoginFragment extends BaseFragment implements LoginContract.View {
    @BindView(R.id.back)
    TextView back;
    @BindView(R.id.edit_phoneNum)
    EditText mEditPhoneNum;
    @BindView(R.id.edit_password)
    EditText mEditPassword;
    @BindView(R.id.btn_login)
    Button mBtnLogin;
    private TextView regist;
    private TextView forgot;
    private String phoneNume;
    private String passWord;
    private ImageView imgBiyan;
    private TextView bak;

    private Unbinder unbinder;

    private LoginContract.Presenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initView(View rootView) {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mEditPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        regist = (TextView) rootView.findViewById(R.id.regist);
        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent4Register();
            }
        });
        imgBiyan = (ImageView) rootView.findViewById(R.id.img_biyan);
        imgBiyan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                visiblePwd();

            }
        });
        forgot = (TextView) rootView.findViewById(R.id.tv_forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntent4ForgetPwd();
            }
        });
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.requestLogin( mEditPhoneNum.getText().toString(),mEditPassword.getText().toString(), Util.getDeviceId(getActivity()));
            }
        });
    }

    @Override
    public void loadData() {

    }

    /**
     * 通过该方法，view获得了presenter得实例，从而可以调用presenter代码来处理业务逻辑
     * @param presenter
     */
    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.mPresenter =presenter;
    }

    @Override
    public void showNetSnackBar() {
        showSetNetworkSnackbar();
    }

    @Override
    public void showLoginSnackBar() {
        showUnLoginSnackbar(getBaseActivity());
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if(active){
            showProgress(getClass().getSimpleName().toString());
        }else{
            dismissProgress();
        }

    }

    @Override
    public void showToast(String message) {
        showTost(message, Toast.LENGTH_SHORT);
    }

    @Override
    public void visiblePwd() {
//        if (mEditPassword.getText().toString().equals("")) {
//            return;
//        } else if (mEditPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
//            mEditPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//            imgBiyan.setImageResource(R.mipmap.regist);
//            mEditPassword.setSelection(mEditPassword.getText().length());
//
//
//        } else if (mEditPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
//            mEditPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            imgBiyan.setImageResource(R.mipmap.biyan);
//            mEditPassword.setSelection(mEditPassword.getText().length());
//
//        }
    }

    @Override
    public void startIntent4Register() {
//        startActivity(new Intent(getActivity(), RegisterActivity.class));
    }

    @Override
    public void startIntent4ForgetPwd() {
//        startActivity(new Intent(getActivity(), ForgotPasswordActivity.class));
//        getActivity().finish();
    }



}
