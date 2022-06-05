package com.example.walking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.walking.R;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.snu.ids.kkma.ma.MExpression;
import org.snu.ids.kkma.ma.MorphemeAnalyzer;
import org.snu.ids.kkma.ma.Sentence;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extractTest();
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
        String str="";
        TextView text = (TextView)findViewById(R.id.test);
        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(edit.getText().toString(), true);
        for( int i = 0; i < kl.size(); i++ ){
            Keyword kwrd = kl.get(i);
            System.out.println(kwrd.getString() + "\t" + kwrd.getCnt());
            str += (kwrd.getString() + "\t" + kwrd.getCnt()) + "\n";
        }
        text.setText(str);
    }
}