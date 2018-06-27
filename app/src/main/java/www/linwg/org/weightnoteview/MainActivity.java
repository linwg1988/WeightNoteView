package www.linwg.org.weightnoteview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import www.linwg.org.CustomFormat;
import www.linwg.org.Rule;
import www.linwg.org.WeightNoteView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final WeightNoteView view = findViewById(R.id.weightNoteView);
        view.setSupportScale(false);


        view.addRule(new Rule() {
            @Override
            public CustomFormat convert(CharSequence charSequence, int rowIndex, int columnIndex) {
                if(rowIndex ==1 && columnIndex == 2){
                    CustomFormat customFormat = new CustomFormat();
                    customFormat.bgColor = Color.GREEN;
                    return customFormat;
                }
                if(rowIndex == 2 && columnIndex == 3){
                    CustomFormat customFormat = new CustomFormat();
                    customFormat.textColor = Color.RED;
                    return customFormat;
                }
                if(rowIndex == 4 && columnIndex == 6){
                    CustomFormat customFormat = new CustomFormat();
                    SpannableStringBuilder sp = new SpannableStringBuilder(charSequence);
                    sp.setSpan(new ForegroundColorSpan(Color.BLUE),1,3,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    customFormat.charSequence = sp;
                    return customFormat;
                }

                return null;
            }
        });

        findViewById(R.id.fb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ArrayList<CharSequence> list = new ArrayList<>();
//                for (int i = 0; i < 10; i++) {
//                    list.add("列标题" + i);
//                }
//                view.setColumnTitleData(list);
//
//                ArrayList<CharSequence> list2 = new ArrayList<>();
//                for (int i = 0; i < 6; i++) {
//                    list2.add("行标题" + i);
//                }
//                view.setRowTileData(list2);
//
////                ArrayList<Collection<CharSequence>> co = new ArrayList<>();
////                for (int row = 0; row < 10; row++) {
////                    ArrayList<CharSequence> col = new ArrayList<>();
////                    for (int column = 0; column < 10; column++) {
////                        col.add("row:" + row + " column:" + column);
////                    }
////                    co.add(col);
////                }
////                view.setContentDataByHorizontal(co);
//
//                ArrayList<Collection<CharSequence>> co = new ArrayList<>();
//                for (int c = 0; c < 10; c++) {
//                    ArrayList<CharSequence> col = new ArrayList<>();
//                    for (int r = 0; r < 6; r++) {
//                        col.add("c:" + c + " r:" + r);
//                    }
//                    co.add(col);
//                }
//                view.setContentDataByVertical(co);
//
//                view.onContentPrepareFinished();
                SpannableStringBuilder sb = new SpannableStringBuilder("自定义表格");
                sb.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")),0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                view.setTitle(sb);
                view.setDataGenerator(new WeightNoteView.DataGenerator() {
                    @Override
                    public int getRowCount() {
                        return 10;
                    }

                    @Override
                    public CharSequence getRowTitle(int rowIndex) {
                        if(rowIndex == 1){
                            SpannableStringBuilder sb = new SpannableStringBuilder("行" + rowIndex);
                            sb.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")),0,1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            return sb;
                        }
                        return "行"+rowIndex;
                    }

                    @Override
                    public int getColumnCount() {
                        return 10;
                    }

                    @Override
                    public CharSequence getColumnTitle(int columnIndex) {
                        return "列"+columnIndex;
                    }

                    @Override
                    public CharSequence getContentData(int rowIndex, int columnIndex) {
                        return "row"+rowIndex+"   column"+columnIndex;
                    }
                });
            }
        });

    }

}
