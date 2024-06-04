package com.example.uibestpractice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    private List<Message> msgList = new ArrayList<>();

    private EditText inputText;

    private Button sendButton;

    private RecyclerView msgRecyleView;

    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMessages();
        inputText = (EditText) findViewById(R.id.input_text);

        sendButton = (Button) findViewById(R.id.send_button);

        msgRecyleView = (RecyclerView) findViewById(R.id.msg_recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyleView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(msgList);
        msgRecyleView.setAdapter(adapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = inputText.getText().toString();
                if (!"".equals(text)) {
                    Message msg = new Message(text,Message.TYPE_SEND);
                    msgList.add(msg);
                    // 刷新 recyleView的内容
                    adapter.notifyItemInserted(msgList.size()-1);
                    // 移动 recycleView到最后一行
                    msgRecyleView.scrollToPosition(msgList.size() - 1);

                    inputText.setText("");


                }
            }
        });

    }

    public void initMessages() {
        Message msg1 = new Message("Hello man.", Message.TYPE_RECEIVED);
        msgList.add(msg1);
        Message msg2 = new Message("Hello, who am i speaking to?", Message.TYPE_SEND);
        msgList.add(msg2);
        Message msg3 = new Message("This is Jack from China", Message.TYPE_SEND);
        msgList.add(msg3);
    }
}