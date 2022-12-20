package com.example.cookmemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.UUID;

public class CreateMemo_Activity extends AppCompatActivity {

    // MemoOpenHelperクラスを定義
    MemoOpenHelper helper = null;
    // 新規フラグ
    boolean newFlag = false;
    // id
    String id = "";

    public static final String T
            = "com.example.memoonly.MESSAGE";

    public static final String S
            = "com.example.memoonly.MESSAGE";

    static final int REQUEST_CODE = 1000;
    static final int REQUEST_CODE2 = 2000;

    String[] dropdownItems = {
            "調味料",
            "砂糖",
            "塩",
            "酢",
            "醤油",
            "味噌"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_memo);

        // データベースから値を取得する
        if(helper == null){
            helper = new MemoOpenHelper(CreateMemo_Activity.this);
        }

        // ListActivityからインテントを取得
        Intent intent = this.getIntent();
        // 値を取得
        id = intent.getStringExtra("id");
        // 画面に表示
        if(id.equals("")){
            // 新規作成の場合
            newFlag = true;
        }else{
            // 編集の場合 データベースから値を取得して表示
            // データベースを取得する
            SQLiteDatabase db = helper.getWritableDatabase();
            try {
                // rawQueryというSELECT専用メソッドを使用してデータを取得する
                Cursor c = db.rawQuery("select body from MEMO_TABLE where uuid = '"+ id +"'", null);
                // Cursorの先頭行があるかどうか確認
                boolean next = c.moveToFirst();
                // 取得した全ての行を取得
                while (next) {
                    // 取得したカラムの順番(0から始まる)と型を指定してデータを取得する
                    String dispBody = c.getString(0);
                    EditText body = (EditText)findViewById(R.id.body);
                    body.setText(dispBody, TextView.BufferType.NORMAL);
                    next = c.moveToNext();
                }
            } finally {
                // finallyは、tryの中で例外が発生した時でも必ず実行される
                // dbを開いたら確実にclose
                db.close();
            }
        }

        /**
         * タイマーボタン処理
         */
        // idがtimerのボタンを取得
        Button timerButton = (Button) findViewById(R.id.timer);
        // clickイベント追加
        timerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), TimerActivity.class);
                intent.putExtra(T, 0);
                startActivityForResult(intent, REQUEST_CODE);
            }


        });

        /**
         * 調味料ボタン処理
         */
        // idがtimerのボタンを取得
        /*
        Button seasoningButton = (Button) findViewById(R.id.seasoning);
        // clickイベント追加
        seasoningButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), SeasoningActivity.class);
                intent.putExtra(S, 0);
                startActivityForResult(intent, REQUEST_CODE2);
            }


        });*/

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dropdownItems);
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = (String) parent.getAdapter().getItem(position);
                EditText editText;
                editText = (EditText)findViewById(R.id.body);
                String text1 = editText.getText().toString();
                if (text != "調味料") {
                    TextView textView = (TextView) findViewById(R.id.body);
                    textView.setText(text1 + text + "\n");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        /**
         * 小さじボタン処理
         */
        // idがsmallのボタンを取得

        Button smallButton = (Button) findViewById(R.id.small);
        // clickイベント追加
        smallButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText editText;
                editText = (EditText)findViewById(R.id.body);
                String text1 = editText.getText().toString();
                TextView textView = (TextView) findViewById(R.id.body);
                textView.setText(text1 + "小さじ" + "\n");
            }


        });

        /**
         * 大さじボタン処理
         */
        // idがbigのボタンを取得

        Button bigButton = (Button) findViewById(R.id.big);
        // clickイベント追加
        bigButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText editText;
                editText = (EditText)findViewById(R.id.body);
                String text1 = editText.getText().toString();
                TextView textView = (TextView) findViewById(R.id.body);
                textView.setText(text1 + "大さじ" + "\n");
            }


        });




        /**
         * 登録ボタン処理
         */
        // idがregisterのボタンを取得
        Button registerButton = (Button) findViewById(R.id.register);
        // clickイベント追加
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 入力内容を取得する
                EditText body = (EditText)findViewById(R.id.body);
                EditText Decimal = (EditText)findViewById(R.id.Decimal);
                String bodyStr = body.getText().toString() + '\n' + Decimal.getText().toString();
                //String DecimalStr = Decimal.getText().toString();

                // データベースに保存する
                SQLiteDatabase db = helper.getWritableDatabase();
                try {
                    if(newFlag){
                        // 新規作成の場合
                        // 新しくuuidを発行する
                        id = UUID.randomUUID().toString();
                        // INSERT
                        db.execSQL("insert into MEMO_TABLE(uuid, body, Decimal) VALUES('"+ id +"', '"+ bodyStr +"')");
                        //db.execSQL("insert into MEMO_TABLE(uuid, Decimal) VALUES('"+ id +"', '"+ DecimalStr +"')");
                    }else{
                        // UPDATE
                        db.execSQL("update MEMO_TABLE set body = '"+ bodyStr +"' where uuid = '"+id+"'");
                        //db.execSQL("update MEMO_TABLE set Decimal = '"+ DecimalStr +"' where uuid = '"+id+"'");
                    }
                } finally {
                    // finallyは、tryの中で例外が発生した時でも必ず実行される
                    // dbを開いたら確実にclose
                    db.close();
                }
                // 保存後に一覧へ戻る
                Intent intent = new Intent(CreateMemo_Activity.this, ListActivity.class);
                startActivity(intent);
            }
        });


        /**
         * 戻るボタン処理
         */
        // idがbackのボタンを取得
        Button backButton = (Button) findViewById(R.id.back);
        // clickイベント追加
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 保存せずに一覧へ戻る
                finish();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CreateMemo_Activity.super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    String res = data.getStringExtra(T);

                    TextView textView = (TextView) findViewById(R.id.Decimal);
                    textView.setText(res);
                }
                break;
        }
    }

}
