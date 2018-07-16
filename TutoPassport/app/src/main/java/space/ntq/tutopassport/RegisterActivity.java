package space.ntq.tutopassport;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import space.ntq.tutopassport.entites.AccessToken;
import space.ntq.tutopassport.network.ApiService;
import space.ntq.tutopassport.network.RetrofitBuilder;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG= "RegisterActivity";

    @BindView(R.id.til_name)  TextInputLayout tilName;
    @BindView(R.id.til_email) TextInputLayout tilEmail;
    @BindView(R.id.til_password) TextInputLayout tilPassword;

    ApiService service;
    Call<AccessToken> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

      //  service = RetrofitBuilder.createService(ApiService.class);
    }

    @OnClick(R.id.btn_register)
    void register(){
        final String name =  tilName.getEditText().getText().toString();
        final String email = tilEmail.getEditText().getText().toString();
        String password = tilPassword.getEditText().getText().toString();

      //  call = service.register(name,email,password);
        call.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                Log.w(TAG,"onResponse"+response);
                Toast.makeText(RegisterActivity.this, ""+name, Toast.LENGTH_SHORT).show();

                if (response.isSuccessful()){
                    
                }else {
                    
                }

            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.w(TAG,"onFailure"+t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null){
            call.cancel();
            call = null;
        }
    }
}
