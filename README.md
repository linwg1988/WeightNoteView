# WeightNoteView

因为产品需求，需要一个可支持垂直水平滑动的图表控件，趁机就自己写了一个.</br>

gradle中的引用：
~~~Java
gradle:
dependencies {
    implementation 'org.linwg1988:weightnoteview:1.3'
}
~~~
xml中各个属性的解释（文字颜色以及大小，分割线颜色，大小，表格背景色等不再列出）：</br>


| xml属性名称 | 中文释义 |
| --- | --- |
| title | 标题 |
| bottom_label_content | 底部标题 |
| top_corner_label |顶部角标 |
| top_corner_label | 顶部角标 |
| bottom_corner_label | 底部角标 |
| cell_padding | 表格的内间距 |
| draw_bottom | 是否绘制底部 |
| support_scale | 是否支持缩放，默认不支持（现在效果不好） |
| min_row_width | 最小行（标题）表格宽度 |
| min_row_height | 最小行表格高度 |
| min_column_width | 最小行（内容）表格宽度 |
| fade_column_title_border | 列标题与内容之间的分割线是否颜色淡化处理 |

为控件填充数据的方法：</br>
~~~Java
weightNoteView.setDataGenerator(new WeightNoteView.DataGenerator() {
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
~~~

在行标题或列标题已经设置的情况下也支持单列或单行数据添加:</br>
~~~Java
//添加单列数据
public void addColumn(@NonNull CharSequence columnTitle, @Nullable ArrayList<CharSequence> list) {
    ...
}

//添加单行数据
public void addRow(@Nullable CharSequence rowTitle, @NonNull ArrayList<CharSequence> rowList) {
    ...
}
~~~

此外，如果选择单独设置行标题，列标题后，使用setContentDataByHorizontal()或setContentDataByVertical()</br>
进行数据填充时，在填充后需使用onContentPrepareFinished()通知表格展示数据。</br>

此控件除可支持双向滑动外，还支持对内容的自定义样式；在上述填充数据的代码中已经可以看到，</br>
每个表格绘制的是CharSequence这个类型，因而可以对文本的样式进行自定义（现在只支持颜色自定义）</br>
如果想要对内容表格更深层次的自定义的话，可以使用如下代码：</br>
~~~Java
view.addRule(new Rule() {
    @Override
    public CustomFormat convert(CharSequence charSequence, int rowIndex, int columnIndex) {
        //第1行第1列的表格背景色改变了
        if(rowIndex ==0 && columnIndex == 0){
            CustomFormat customFormat = new CustomFormat();
            customFormat.bgColor = Color.GREEN;
            return customFormat;
        }
        //第2行第2列的表格文字颜色改变了
        if(rowIndex == 1 && columnIndex == 1){
            CustomFormat customFormat = new CustomFormat();
            customFormat.textColor = Color.RED;
            return customFormat;
        }
        //第3行第3列的表格部分文字颜色改变了
        if(rowIndex == 2 && columnIndex == 2){
            CustomFormat customFormat = new CustomFormat();
            SpannableStringBuilder sp = new SpannableStringBuilder(charSequence);
            sp.setSpan(new ForegroundColorSpan(Color.BLUE),0,1,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            customFormat.charSequence = sp;
            return customFormat;
        }
        return null;
    }
});
~~~
根据实际需求可以添加多个rule，最后添加的rule优先执行。</br>






