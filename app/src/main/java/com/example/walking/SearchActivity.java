package com.example.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.walking.R;
import com.kakao.sdk.user.UserApiClient;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.snu.ids.kkma.ma.MExpression;
import org.snu.ids.kkma.ma.MorphemeAnalyzer;
import org.snu.ids.kkma.ma.Sentence;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class SearchActivity extends AppCompatActivity {
    List<String> data = new ArrayList();
    List<String> dataBcakup = new ArrayList();
    List<String> dataStart = new ArrayList();
    private ArrayAdapter<String> adapter = null;
    private String strNick, strEmail;
    private Spinner spinner;
    private String seoul = "강남구";
    private StringBuilder themeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 카카오 로그인 이름 이메일 넣어오는 것 확인 코드 --------------------------------------------
        /*
        Intent intent = getIntent();
        strNick = intent.getStringExtra("name");
        strEmail = intent.getStringExtra("email");

        TextView nickName = findViewById(R.id.nickname);
        TextView email = findViewById(R.id.email);

        nickName.setText(strNick);
        email.setText(strEmail);
        */
        // 카카오 로그인 이름 이메일 넣어오는 것 확인 코드 -------------------------------------------

        //현재 서울의 어디인지 알아오는 것
        spinner = (Spinner)findViewById(R.id.seoul);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                seoul =parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //현재 서울의 어디인지 알아오는 것---------------------------

        // 테마 분석 및 지도
        maTest();
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extractTest();
                //xls();
            }
        });
       // extractTest();


        // 로그아웃
        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().logout(error -> {
                    if (error != null) {
                    }else{
                        Intent intent = new Intent(SearchActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    return null;
                });
            }
        });


    }


    /*
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }
     */

//xls 파일 읽어오기
    public void xls(ArrayList theme){
        dataBcakup.clear();
        data.clear();

        try {
            InputStream is = getBaseContext().getResources().getAssets().open("map_with_theme.xls");
            Workbook wb = Workbook.getWorkbook(is);

            ListView list = (ListView) findViewById(R.id.listView1);

            if(adapter!=null){
                adapter.clear();
                list.setAdapter(adapter);
            }


            if(wb != null) {
                Sheet sheet = wb.getSheet(0);   // 시트 불러오기
                if(sheet != null) {
                    int colTotal = sheet.getColumns();    // 전체 컬럼
                    int rowIndexStart = 1;                  // row 인덱스 시작
                    int rowTotal = sheet.getColumn(colTotal-1).length;
                    System.out.println("전체 row : " + rowTotal+"\n");

                    StringBuilder sb;
                    String[] array;
                    for(int row=rowIndexStart;row<rowTotal;row++) {
                        themeData = new StringBuilder();

                        //System.out.println("지역 : " +  sheet.getCell(0, row).getContents().toString() +" : "+seoul+ "\n");
                        if(seoul.equals(sheet.getCell(0, row).getContents())) {
                            for (int col = 0; col < colTotal; col++) {
                                String contents = sheet.getCell(col, row).getContents();    // col row에 해당하는 것 하나씩 읽기
                                themeData.append( contents + " ");

                            }
                        }
                        //System.out.println("test : " +  themeData.toString() + "\n");
                        //Log.i(String.valueOf(this), "test : " +themeData.toString());
                        String str="";
                        if(themeData.length()>10) {
                            array = themeData.toString().split(" ");
                            Log.i(String.valueOf(this), "test3 : " +array[1]);
                            for(int i =0;i<theme.size();i++){
                                for(int j=0;j<array.length;j++) {
                                    if(array[j].equals(theme.get(i))){
                                        str = "산책로 : "+array[1] + "  테마 : ";
                                        for(int p =6;p<10;p++){
                                            if(!(array[p].equals("없음"))){
                                                str += " " + array[p];
                                            }
                                        }
                                        data.add(str);
                                        dataBcakup.add(themeData.toString());
                                        break;
                                    }
                                }
                            }

                            //data.add(themeData.toString());
                            //dataBcakup.add(themeData.toString());
                        }
                    }
                    if(data.size()==0){
                        for(int row=rowIndexStart;row<rowTotal;row++) {
                            themeData = new StringBuilder();

                            //System.out.println("지역 : " +  sheet.getCell(0, row).getContents().toString() +" : "+seoul+ "\n");
                            if(seoul.equals(sheet.getCell(0, row).getContents())) {
                                for (int col = 0; col < colTotal; col++) {
                                    String contents = sheet.getCell(col, row).getContents();    // col row에 해당하는 것 하나씩 읽기
                                    themeData.append( contents + " ");

                                }
                            }
                            //System.out.println("test : " +  themeData.toString() + "\n");
                            Log.i(String.valueOf(this), "test4 : " +themeData.toString());
                            String str="";
                            if(themeData.length()>10) {
                                array = themeData.toString().split(" ");
                                Log.i(String.valueOf(this), "test5 : " +array[1]);

                                    for(int j=0;j<array.length;j++) {
                                            str = "산책로 : "+array[1] + "  테마 : ";
                                            for(int p =6;p<10;p++){
                                                if(!(array[p].equals("없음"))){
                                                    str += " " + array[p];
                                                }
                                            }
                                            data.add(str);
                                            dataBcakup.add(themeData.toString());
                                            break;

                                    }


                                //data.add(themeData.toString());
                                //dataBcakup.add(themeData.toString());
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
        Log.i(String.valueOf(this), "test2 : " +data);
        Log.i(String.valueOf(this), "test2 : " +dataBcakup);




        ListView list = (ListView) findViewById(R.id.listView1);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, data);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                Toast.makeText(getApplicationContext(),dataBcakup.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);

                intent.putExtra("data", dataBcakup.get(position));
                startActivity(intent);

            }
        });
    }


    //모든 용언 분석
    public void maTest() {
        EditText edit = (EditText) findViewById(R.id.search);
        String string = "광진구 예쁜길이 좋아요";
        String str="";
        //TextView text = (TextView)findViewById(R.id.test);
        try {
            MorphemeAnalyzer ma = new MorphemeAnalyzer();
            ma.createLogger(null);
            List<MExpression> ret = ma.analyze(string);
            ret = ma.postProcess(ret);
            ret = ma.leaveJustBest(ret);
            List<Sentence> stl = ma.divideToSentences(ret);
            for( int i = 0; i < stl.size(); i++ ) {
                Sentence st = stl.get(i);
                System.out.println("=============================================  " + st.getSentence());
                for( int j = 0; j < st.size(); j++ ) {
                    System.out.println(st.get(j));
                    str += st.get(j) + "\n";
                }
            }
            ma.closeLogger();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //text.setText(str);
    }
    //명사 분서
    public void extractTest(){
        EditText edit = (EditText) findViewById(R.id.search);
        ArrayList<String> list = new ArrayList<>();
        //String string = "광진구 예쁜길이 좋아요";
        String str="";
        if(edit.toString() !=null) {
            //TextView text = (TextView) findViewById(R.id.test);
            KeywordExtractor ke = new KeywordExtractor();
            //KeywordList kl = ke.extractKeyword(string, true);
            KeywordList kl = ke.extractKeyword(edit.getText().toString(), true);
            for (int i = 0; i < kl.size(); i++) {
                Keyword kwrd = kl.get(i);
                System.out.println(kwrd.getString() + "\t" + kwrd.getCnt());
                str += (kwrd.getString() + " " + kwrd.getCnt()) + "\t";
                list.add(kwrd.getString());
            }

            //text.setText(str);
            //Keyword kwrd = kl.get(0);
            //int size = kl.size();

            xls(list);
        }
    }

}