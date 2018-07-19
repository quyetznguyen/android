package space.ntq.tutopassport;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
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

public class PostActivity extends AppCompatActivity {
    private static final String TAG = "PostActivity";
    @BindView(R.id.tvPost_Title)
    TextView tvTitle;

    ApiService apiService;
    TokenManager tokenManager;
    Call<PostResponse> call;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);


        ButterKnife.bind(this);

        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if (tokenManager.getToken() == null) {
            startActivity(new Intent(PostActivity.this, LoginActivity.class));
            finish();
        }

        apiService = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itemLogOut) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        String asToken = tokenManager.getToken().getAccessToken();
        Call<AccessToken> callLogout;
        callLogout = apiService.logout(asToken);
        callLogout.enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                Log.w(TAG, "onResponse: " + response);
                if (response.isSuccessful()) {
                    tokenManager.deleteToken();
                    startActivity(new Intent(PostActivity.this, LoginActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.btnPost)
    void getPosts() {
        call = apiService.posts();
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                Log.w(TAG, "onResponse: " + response);

                if (response.isSuccessful()) {
                    if (response.errorBody()==null){
                        Toast.makeText(PostActivity.this, "U don't have data!", Toast.LENGTH_SHORT).show();
                    }else {
                        tvTitle.setText(response.body().getData().get(0).getTitle());

                    }

                } else {

                    if (response.code() == 401) {
                        tokenManager.deleteToken();
                        startActivity(new Intent(PostActivity.this, LoginActivity.class));
                        finish();

                    }

                }


            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call != null) {
            call.cancel();
            call = null;
        }
    }
}
