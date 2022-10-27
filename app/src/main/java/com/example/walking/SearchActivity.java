package com.example.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.walking.R;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.snu.ids.kkma.ma.MExpression;
import org.snu.ids.kkma.ma.MorphemeAnalyzer;
import org.snu.ids.kkma.ma.Sentence;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class SearchActivity extends AppCompatActivity {
    List<String> data = new ArrayList();
    private ArrayAdapter<String> adapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extractTest();
                //xls();
            }
        });
       // extractTest();



    }
//xls 파일 읽어오기
    public void xls(String location){
        try {
            InputStream is = getBaseContext().getResources().getAssets().open("park_in_seoul_3.xls");
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
                    for(int row=rowIndexStart;row<rowTotal;row++) {
                        sb = new StringBuilder();

                        System.out.println("지역 : " +  sheet.getCell(0, row).getContents().toString() +" : "+location+ "\n");
                        if(location.equals(sheet.getCell(0, row).getContents())) {
                            for (int col = 0; col < colTotal; col++) {
                                String contents = sheet.getCell(col, row).getContents();    // col row에 해당하는 것 하나씩 읽기
                                sb.append( contents + " ");

                            }
                        }
                        System.out.println("test : " +  sb.toString() + "\n");
                        if(sb.length()>10)
                            data.add(sb.toString());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }

        ListView list = (ListView) findViewById(R.id.listView1);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, data);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                Toast.makeText(getApplicationContext(),data.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);

                intent.putExtra("data", data.get(position));
                startActivity(intent);

            }
        });
    }


    //모든 용언 분석
    public void maTest() {
        EditText edit = (EditText) findViewById(R.id.search);
        String str="";
        TextView text = (TextView)findViewById(R.id.test);
        try {
            MorphemeAnalyzer ma = new MorphemeAnalyzer();
            ma.createLogger(null);
            List<MExpression> ret = ma.analyze(edit.getText().toString());
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
        text.setText(str);
    }
    //명사 분서
    public void extractTest(){
        EditText edit = (EditText) findViewById(R.id.search);
        //String string = "광진구 예쁜길이 좋아요";
        String str="";
        TextView text = (TextView)findViewById(R.id.test);
        KeywordExtractor ke = new KeywordExtractor();
        //KeywordList kl = ke.extractKeyword(string, true);
        KeywordList kl = ke.extractKeyword(edit.getText().toString(), true);
        for( int i = 0; i < kl.size(); i++ ){
            Keyword kwrd = kl.get(i);
            System.out.println(kwrd.getString() + "\t" + kwrd.getCnt());
            str += (kwrd.getString() + " " + kwrd.getCnt()) + "\t";
        }

        text.setText(str);
        Keyword kwrd = kl.get(0);

        xls(kwrd.getString()+"구");
    }

}