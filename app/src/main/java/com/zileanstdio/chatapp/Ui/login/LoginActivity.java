package com.zileanstdio.chatapp.Ui.login;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding4.widget.RxTextView;
import com.zileanstdio.chatapp.Base.BaseActivity;
import com.zileanstdio.chatapp.Base.BaseFragment;
import com.zileanstdio.chatapp.R;
import com.zileanstdio.chatapp.Ui.main.MainActivity;

import java.util.regex.Pattern;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class LoginActivity extends BaseActivity {

    private boolean isValidatePhoneNumber, isValidatePassword;
    private FloatingActionButton loginButton;
    private TextInputLayout phoneNumberInputLayout, passwordInputLayout;
    private EditText phoneNumberEditText, passwordEditText;

    private final CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAppBar();
        subscribeObservers();

        loginButton = findViewById(R.id.activity_login_next_btn);
        phoneNumberInputLayout = findViewById(R.id.activity_login_text_input_number_phone);
        phoneNumberEditText = phoneNumberInputLayout.getEditText();
        passwordInputLayout = findViewById(R.id.activity_login_text_input_password);
        passwordEditText = passwordInputLayout.getEditText();

        if (phoneNumberEditText != null) {
            Observable<Boolean> phoneNumberInputObservable = RxTextView.textChanges(phoneNumberEditText)
                    .map(inputText -> validatePhoneNumber(inputText.toString()))
                    .distinctUntilChanged();
            disposable.add(phoneNumberInputObservable.subscribe(isValid -> loginButton.setEnabled(isValid)));
        }

        if (passwordEditText != null) {
            Observable<Boolean> passwordInputObservable = RxTextView.textChanges(passwordEditText)
                    .map(inputText -> validatePassword(inputText.toString()))
                    .distinctUntilChanged();
            disposable.add(passwordInputObservable.subscribe(isValid -> loginButton.setEnabled(isValid)));
        }

        loginButton.setOnClickListener(v -> {
            loginButton.setEnabled(false);
            phoneNumberEditText.setEnabled(false);
            passwordEditText.setEnabled(false);
            ((LoginViewModel) viewModel).login(phoneNumberEditText.getText().toString(), passwordEditText.getText().toString());
        });
    }

    @Override
    public void initAppBar() {
        super.initAppBar();
        setTitleToolbar("Đăng nhập");
        setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public ViewModel getViewModel() {
        if(viewModel != null) {
            return viewModel;
        }
        viewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(LoginViewModel.class);
        return viewModel;
    }

    @Override
    public Integer getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public Integer getViewRootId() {
        return R.id.clLoginActivity;
    }

    @Override
    public void replaceFragment(BaseFragment fragment) {

    }

    @Override
    public void onClick(View v) {

    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber.trim().isEmpty()) {
            phoneNumberEditText.getText().clear();
            phoneNumberEditText.setSelection(0);
            isValidatePhoneNumber = false;
        } else if (!Pattern.matches("^[0-9]{0,2}[\\s-]?[- ]?([0-9]{0,4})[- ]?([0-9]{0,4})[- ]?([0-9]{0,})$", phoneNumber)) {
            phoneNumberInputLayout.setError("Số điện thoại không khả dụng. (e.x:1-888-364-3577).");
            phoneNumberInputLayout.setErrorIconDrawable(null);
            isValidatePhoneNumber = false;
        } else if  (!Pattern.matches("^(?=(?:\\D*\\d){10,}\\D*$)[0-9]{1,2}[\\s-]?[- ]?([0-9]{3,4})[- ]?([0-9]{3,4})[- ]?([0-9]{3,10})$", phoneNumber)) {
            phoneNumberInputLayout.setError("Số điện thoại phải từ 10 đến 20 chữ số");
            phoneNumberInputLayout.setErrorIconDrawable(null);
            isValidatePhoneNumber = false;
        } else {
            phoneNumberInputLayout.setError(null);
            isValidatePhoneNumber = true;
        }
        return (isValidatePhoneNumber && isValidatePassword);
    }

    private boolean validatePassword(String password) {
        if (password.trim().isEmpty()) {
            passwordEditText.getText().clear();
            passwordEditText.setSelection(0);
            isValidatePassword = false;
        } else {
            passwordInputLayout.setError(null);
            isValidatePassword = true;
        }
        return (isValidatePassword && isValidatePhoneNumber);
    }

    public void subscribeObservers() {
        ((LoginViewModel) viewModel).observeLogin().observe(this, stateResource -> {
            if (stateResource != null) {
                switch (stateResource.status) {
                    case LOADING:
                        showLoadingDialog();
                        break;
                    case SUCCESS:
                        Toast.makeText(this, "Chào mừng đến với 'Zimess'", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                        break;
                    case ERROR:
                        closeLoadingDialog();
                        phoneNumberEditText.setEnabled(true);
                        passwordEditText.setEnabled(true);
                        Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        Log.d("AAA", stateResource.message);
                        break;
                }
            }
        });
    }
}