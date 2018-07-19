package space.ntq.tutopassport;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import space.ntq.tutopassport.entites.AccessToken;
import space.ntq.tutopassport.entites.ApiError;
import space.ntq.tutopassport.network.ApiService;
import space.ntq.tutopassport.network.RetrofitBuilder;
import space.ntq.tutopassport.utils.Utils;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    @BindView(R.id.til_email)
    TextInputLayout tilEmail;
    @BindView(R.id.til_password)
    TextInputLayout tilPassword;
    @BindView(R.id.rlContainer)
    RelativeLayout rlContainer;
    @BindView(R.id.form_container)
    LinearLayout formContainer;
    @BindView(R.id.loader)
    ProgressBar loader;

    ApiService apiService;
    TokenManager tokenManager;
    AwesomeValidation awesomeValidation;
    Call<AccessToken> call;
    FacebookManager facebookManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        apiService = RetrofitBuilder.createService(ApiService.class);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));
        awesomeValidation = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        facebookManager = new FacebookManager(apiService,tokenManager);
        setupValiadation();
        if (tokenManager.getToken().getAccessToken() !=null){
            startActivity(new Intent(LoginActivity.this,PostActivity.class));
            finish();
        }

    }

    private void showLoading(){
        TransitionManager.beginDelayedTransition(rlContainer);
        formContainer.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);

    }
    private void showForm(){
        TransitionManager.beginDelayedTransition(rlContainer);
        formContainer.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);
    }

    @OnClick(R.id.btnFacebook)
    void loginFacebook(){
        showLoading();
        facebookManager.login(this, new FacebookManager.FacebookLoginListener() {
            @Override
            public void onSuccess() {
                facebookManager.clearSession();
                startActivity(new Intent(LoginActivity.this,PostActivity.class));
                finish();
            }

            @Override
            public void onError(String message) {
                showForm();
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.btn_login)
    void login() {
        String email = tilEmail.getEditText().getText().toString();
        String password = tilPassword.getEditText().getText().toString();

        tilEmail.setError(null);
        tilPassword.setError(null);

        awesomeValidation.clear();

        if (awesomeValidation.validate()) {
            showLoading();
            call = apiService.login(email, password);
            call.enqueue(new Callback<AccessToken>() {
                @Override
                public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                    Log.w(TAG, "onResponse:" + response);
                    if (response.isSuccessful()) {
                        tokenManager.saveToken(response.body());
                        startActivity(new Intent(LoginActivity.this,PostActivity.class));
                        finish();
                    } else {
                        if (response.code() == 422) {
                            handleErrors(response.errorBody());
                        }
                        if (response.code() == 401) {
                            ApiError apiError = Utils.convertError(response.errorBody());
                            Toast.makeText(LoginActivity.this, apiError.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        showForm();
                    }
                }

                @Override
                public void onFailure(Call<AccessToken> call, Throwable t) {
                    Log.w("onFailure", t.getMessage());
                    showForm();
                }
            });
        }


    }

    @OnClick(R.id.go_to_register)
    void gotoRegister(){
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }

    private void handleErrors(ResponseBody responseBody) {

        ApiError apiError = Utils.convertError(responseBody);

        for (Map.Entry<String, List> error : apiError.getErrors().entrySet()) {

            if (error.getKey().equals("username")) {
                tilEmail.setError((CharSequence) error.getValue().get(0));
            }
            if (error.getKey().equals("password")) {
                tilPassword.setError((CharSequence) error.getValue().get(0));
            }
        }
    }

    public void setupValiadation() {

        awesomeValidation.addValidation(this, R.id.til_email, Patterns.EMAIL_ADDRESS, R.string.err_email);
        awesomeValidation.addValidation(this, R.id.til_password, RegexTemplate.NOT_EMPTY, R.string.err_password);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
        facebookManager.onDestroy();
    }
}
