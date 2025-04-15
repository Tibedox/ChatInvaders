package ru.tibedox.chatinvaders;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {
    ListView listView;
    EditText editText;
    Retrofit retrofit;
    MyApi api;
    Handler handler;
    Runnable runnable;
    String name;
    String message;
    List<DataFromDB> db = new ArrayList<>();
    List<String> allMessages = new ArrayList<>();
    int numMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.listView);
        editText = findViewById(R.id.editMessage);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("name")) {
            name = intent.getStringExtra("name");
        }

        retrofit = new Retrofit.Builder()
                .baseUrl("https://sch120.ru")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(MyApi.class);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                getMessagesFromDB();
                updateListView();
                handler.postDelayed(runnable, 1000);
            }
        };
        handler.post(runnable);
    }

    public void sendMessage(View view) {
        if(editText.getText().toString().isEmpty()) return;
        message = editText.getText().toString();
        sendMessageToDB();
        editText.setText("");
    }

    public void getMessagesFromDB(){
        api.sendToServer("chat").enqueue(new Callback<List<DataFromDB>>() {
            @Override
            public void onResponse(Call<List<DataFromDB>> call, Response<List<DataFromDB>> response) {
                db = response.body();
            }

            @Override
            public void onFailure(Call<List<DataFromDB>> call, Throwable t) {

            }
        });
    }

    public void sendMessageToDB(){
        api.sendToServer(name, message).enqueue(new Callback<List<DataFromDB>>() {
            @Override
            public void onResponse(Call<List<DataFromDB>> call, Response<List<DataFromDB>> response) {
                db = response.body();
                updateListView();
            }

            @Override
            public void onFailure(Call<List<DataFromDB>> call, Throwable t) {

            }
        });
    }

    private void updateListView(){
        if(numMessages < db.size()){
            allMessages.clear();
            for(DataFromDB a: db) allMessages.add(a.name+"        "+a.created+"\n"+a.message);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, allMessages);
            listView.setAdapter(adapter);
            scrollDown();
            numMessages=db.size();
        }
    }

    void scrollDown(){
        int itemCount = listView.getAdapter().getCount();
        if (itemCount > 0) {
            listView.setSelection(itemCount - 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}