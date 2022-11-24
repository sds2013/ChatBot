package com.cde.chatbot;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Mbti_Istj extends AppCompatActivity {

    TextView chatview_mbti; //채팅방 MBTI
    ImageView chatview_img; //채팅방 MBTI 이미지

    RecyclerView recyclerView;
    EditText editText; //채팅창
    ImageView btn_send; //메세지 보내기 버튼
    ArrayList<Chatsmodal> chatsmodalArrayList;
    ChatAdapter chatAdapter;
    ImageButton btn_back; //뒤로가기 버튼

    private final String USER_KEY = "user";
    private final String BOT_KEY = "bot";

    // 뒤로가기 버튼 관리 (2번 누르면 앱 종료)
    private BackHandler backHandler = new BackHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);

        chatview_mbti = findViewById(R.id.chatview_mbti); //채팅방 mbti
        chatview_img = findViewById(R.id.chatview_img); //채팅방 MBTI 이미지
        chatview_mbti.setText("ISTJ"); //채팅방 입장과 동시에 챗봇 mbti 이름 변경
        chatview_img.setImageResource(R.drawable.istj_name); //채팅방 입장과 동시에 챗봇 mbti 이미지 변경


        recyclerView = findViewById(R.id.chat_recycler2);
        editText = findViewById(R.id.edt_msg);
        btn_send = findViewById(R.id.btn_send);
        chatsmodalArrayList = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatsmodalArrayList, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(chatAdapter);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().isEmpty()) {
                    Toast.makeText(Mbti_Istj.this, "메세지를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                getResponse(editText.getText().toString());
                editText.setText("");
            }
        });

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogShow();
                //finish();
            }
        });
    }


    private void getResponse(String message) {
        chatsmodalArrayList.add(new Chatsmodal(message, USER_KEY));
        chatAdapter.notifyDataSetChanged();
        String url = "\n" +
                "http://api.brainshop.ai/get?bid=170097&key=VvdWJCwsuRJHGw8g&uid=[uid]&msg=" + message;
        String BASE_URL = "http://api.brainshop.ai/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetroFitApi retroFitApi = retrofit.create(RetroFitApi.class);
        Call<MsgModal> call = retroFitApi.getMessage(url);
        call.enqueue(new Callback<MsgModal>() {
            @Override
            public void onResponse(Call<MsgModal> call, Response<MsgModal> response) {
                if (response.isSuccessful()) {
                    MsgModal msgModal = response.body();
                    chatsmodalArrayList.add(new Chatsmodal(msgModal.getCnt(), BOT_KEY));
                    chatAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(chatsmodalArrayList.size() - 1);
                }
            }

            @Override
            public void onFailure(Call<MsgModal> call, Throwable t) {
                chatsmodalArrayList.add(new Chatsmodal("no response", BOT_KEY));
                chatAdapter.notifyDataSetChanged();
            }
        });
    }

    // 뒤로가기 버튼 설정
    @Override
    public void onBackPressed() {
        backHandler.onBackPressed();
    }

    //커스텀다이얼로그 불러오기
    public void dialogShow(){
        CustomDialog dlg = new CustomDialog(Mbti_Istj.this);
        dlg.show();
        dlg.setCanceledOnTouchOutside(false);
        dlg.setCancelable(false);
    }
}

