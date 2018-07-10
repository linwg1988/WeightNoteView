package www.linwg.org;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import www.linwg.org.lib.R;

public class WeightNoteView extends View implements ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnGestureListener {
    private CharSequence title = "WeightNoteView";
    private CharSequence topCornerLabel = "编号";
    private CharSequence bottomCornerLabel = "总计";
    private CharSequence bottomLabelContent = "表格合计内容";

    private float titleTextSize = 48;
    private int titleTextColor = Color.parseColor("#333333");
    private int titleBackgroundColor = Color.WHITE;
    private ArrayList<CharSequence> rowLabelList = new ArrayList<>();
    private float rowLabelTextSize = 36;
    private int rowLabelTextColor = Color.WHITE;
    private int rowLabelBackgroundColor = Color.parseColor("#333333");
    private ArrayList<CharSequence> columnLabelList = new ArrayList<>();
    private float columnLabelTextSize = 36;
    private int columnLabelTextColor = Color.WHITE;
    private int columnLabelBackgroundColor = Color.parseColor("#999999");
    private int columnDividerColor = Color.parseColor("#e4e4e4");
    private int columnTitleDividerColor = Color.parseColor("#e4e4e4");
    private int rowTitleDividerColor = Color.parseColor("#333333");
    private int borderColor = Color.parseColor("#333333");
    private float contentLabelTextSize = 36;
    private int contentLabelTextColor = Color.parseColor("#333333");
    private int contentLabelBackgroundColor = Color.WHITE;
    private int contentDividerColor = Color.parseColor("#e4e4e4");
    private int cellPadding = 36;
    private int bottomBackgroundColor = Color.parseColor("#333333");
    private int bottomLabelTextSize = 36;
    private int bottomLabelTextColor = Color.WHITE;
    private boolean fadeColumnTitleBorder = true;

    private boolean drawBottom = true;
    private boolean supportScale = false;

    private int dividerSize = 1;
    private int borderWidth = 2;

    private int titleCellHeight = 150;
    private int bottomCellHeight = 150;
    private int minRowHeight = 50;
    private int minColumnWidth = 300;
    private int minRowColumnWidth = 150;
    private Paint paint = new Paint();
    private Paint dividerPaint = new Paint();
    private int viewWidth;
    private int viewHeight;
    private float rowLabelWidth;
    private float rowLabelHeight;
    private float holdContentWidth;
    private float holdContentHeight;

    private ArrayList<ArrayList<CharSequence>> content = new ArrayList<>();
    private ArrayList<Float> columnWidthList = new ArrayList<>();

    //平滑滚动中要用到Scroller
    private Scroller scroller;
    ScaleGestureDetector scaleGestureDetector;
    GestureDetector gestureDetector;
    private float preScale = 1.0f;
    private float curScale = 1.0f;
    private ArrayList<Rule> ruleList;
    private OnCellItemClickListener mOnCellItemClickListener;
    //速度约束方向
    private boolean velocityConstraintOrientation = false;


    public WeightNoteView(Context context) {
        this(context, null);
    }

    public WeightNoteView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeightNoteView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeightNoteView, defStyleAttr, 0);
        int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int index = typedArray.getIndex(i);
            if (index == R.styleable.WeightNoteView_title) {
                title = typedArray.getString(index);
            } else if (index == R.styleable.WeightNoteView_title_bg_color) {
                titleBackgroundColor = typedArray.getColor(index, Color.WHITE);
            } else if (index == R.styleable.WeightNoteView_title_text_color) {
                titleTextColor = typedArray.getColor(index, Color.BLACK);
            } else if (index == R.styleable.WeightNoteView_title_text_size) {
                titleTextSize = typedArray.getDimensionPixelSize(index, 40);
            } else if (index == R.styleable.WeightNoteView_row_label_list) {
                CharSequence[] textArray = typedArray.getTextArray(index);
                if (textArray != null) {
                    for (CharSequence c : textArray) {
                        rowLabelList.add(c);
                    }
                }
            } else if (index == R.styleable.WeightNoteView_row_label_text_size) {
                rowLabelTextSize = typedArray.getDimensionPixelSize(index, 16);
            } else if (index == R.styleable.WeightNoteView_row_label_text_color) {
                rowLabelTextColor = typedArray.getColor(index, Color.parseColor("#fafafa"));
            } else if (index == R.styleable.WeightNoteView_row_label_bg_color) {
                rowLabelBackgroundColor = typedArray.getColor(index, Color.parseColor("#333333"));
            } else if (index == R.styleable.WeightNoteView_row_title_divider_color) {
                rowTitleDividerColor = typedArray.getColor(index, Color.parseColor("#333333"));
            } else if (index == R.styleable.WeightNoteView_column_label_list) {
                CharSequence[] textArray2 = typedArray.getTextArray(index);
                if (textArray2 != null) {
                    for (CharSequence c : textArray2) {
                        columnLabelList.add(c);
                    }
                }
            } else if (index == R.styleable.WeightNoteView_column_label_text_size) {
                columnLabelTextSize = typedArray.getDimensionPixelSize(index, 16);
            } else if (index == R.styleable.WeightNoteView_column_label_text_color) {
                columnLabelTextColor = typedArray.getColor(index, Color.parseColor("#fafafa"));
            } else if (index == R.styleable.WeightNoteView_column_label_bg_color) {
                columnLabelBackgroundColor = typedArray.getColor(index, Color.parseColor("#999999"));
            } else if (index == R.styleable.WeightNoteView_column_divider_color) {
                columnDividerColor = typedArray.getColor(index, Color.parseColor("#ffffff"));
            } else if (index == R.styleable.WeightNoteView_column_title_divider_color) {
                columnTitleDividerColor = typedArray.getColor(index, Color.parseColor("#e4e4e4"));
            } else if (index == R.styleable.WeightNoteView_content_label_text_size) {
                contentLabelTextSize = typedArray.getDimensionPixelSize(index, 16);
            } else if (index == R.styleable.WeightNoteView_content_label_text_color) {
                contentLabelTextColor = typedArray.getColor(index, Color.parseColor("#333333"));
            } else if (index == R.styleable.WeightNoteView_content_label_bg_color) {
                contentLabelBackgroundColor = typedArray.getColor(index, Color.parseColor("#ffffff"));
            } else if (index == R.styleable.WeightNoteView_content_divider_color) {
                contentDividerColor = typedArray.getColor(index, Color.parseColor("#c1c1c1"));
            } else if (index == R.styleable.WeightNoteView_bottom_bg_color) {
                bottomBackgroundColor = typedArray.getColor(index, Color.parseColor("#333333"));
            } else if (index == R.styleable.WeightNoteView_bottom_text_size) {
                bottomLabelTextSize = typedArray.getDimensionPixelSize(index, 36);
            } else if (index == R.styleable.WeightNoteView_bottom_text_color) {
                bottomLabelTextColor = typedArray.getColor(index, Color.WHITE);
            } else if (index == R.styleable.WeightNoteView_cell_padding) {
                cellPadding = typedArray.getDimensionPixelSize(index, 10);
            } else if (index == R.styleable.WeightNoteView_draw_bottom) {
                drawBottom = typedArray.getBoolean(index, true);
            } else if (index == R.styleable.WeightNoteView_support_scale) {
                supportScale = typedArray.getBoolean(index, false);
            } else if (index == R.styleable.WeightNoteView_bottom_corner_label) {
                bottomCornerLabel = typedArray.getString(index);
            } else if (index == R.styleable.WeightNoteView_top_corner_label) {
                topCornerLabel = typedArray.getString(index);
            } else if (index == R.styleable.WeightNoteView_bottom_label_content) {
                bottomLabelContent = typedArray.getString(index);
            } else if (index == R.styleable.WeightNoteView_min_row_height) {
                minRowHeight = typedArray.getDimensionPixelSize(index, 50);
            } else if (index == R.styleable.WeightNoteView_min_row_width) {
                minRowColumnWidth = typedArray.getDimensionPixelSize(index, 150);
            } else if (index == R.styleable.WeightNoteView_min_column_width) {
                minColumnWidth = typedArray.getDimensionPixelSize(index, 300);
            } else if (index == R.styleable.WeightNoteView_border_width) {
                borderWidth = typedArray.getDimensionPixelSize(index, 1);
            } else if (index == R.styleable.WeightNoteView_divider_size) {
                dividerSize = typedArray.getDimensionPixelSize(index, 1);
            } else if (index == R.styleable.WeightNoteView_fade_column_title_border) {
                fadeColumnTitleBorder = typedArray.getBoolean(index, true);
            }
        }
        typedArray.recycle();

        scroller = new Scroller(context);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
        gestureDetector = new GestureDetector(context, this);

        if (columnLabelList.isEmpty()) {
            for (int i = 1; i <= 2; i++) {
                columnLabelList.add("Label" + (i < 10 ? ("0" + i) : String.valueOf(i)));
                ArrayList<CharSequence> contentList = new ArrayList<>();
                for (int x = 1; x <= 2; x++) {
                    String str = "Row " + x + "  Column " + i;
                    contentList.add(str);
                }
                content.add(contentList);
            }
        }
        if (rowLabelList.isEmpty()) {
            for (int i = 1; i <= 2; i++) {
                rowLabelList.add(i < 10 ? ("0" + i) : String.valueOf(i));
            }
        }

        paint.setDither(true);
        paint.setAntiAlias(true);
        dividerPaint.setDither(true);
        dividerPaint.setAntiAlias(true);

        measureColumnMaxWidth();
    }

    private void measureColumnMaxWidth() {
        paint.setTextSize(rowLabelTextSize);
        float maxLen = 0;
        for (int i = 0; i < rowLabelList.size(); i++) {
            CharSequence cs = rowLabelList.get(i);
            maxLen = (int) Math.max(maxLen, paint.measureText(cs, 0, cs.length()));
        }
        float tLen = paint.measureText(topCornerLabel, 0, topCornerLabel.length());
        maxLen = Math.max(maxLen, tLen);
        float bLen = paint.measureText(bottomCornerLabel, 0, bottomCornerLabel.length());
        maxLen = Math.max(maxLen, bLen);

        rowLabelWidth = Math.max(minRowColumnWidth, maxLen + cellPadding * 2);
        rowLabelHeight = Math.max(minRowHeight, rowLabelTextSize + cellPadding * 2);

        holdContentWidth = 0;
        if (content.size() == 0) {
            return;
        }
        holdContentHeight = content.get(0).size() * rowLabelHeight + (content.get(0).size() - 1) * dividerSize;
        columnWidthList.clear();
        for (int i = 0; i < columnLabelList.size(); i++) {
            CharSequence label = columnLabelList.get(i);
            paint.setTextSize(columnLabelTextSize);
            float max = paint.measureText(label, 0, label.length());
            for (int j = 0; j < content.get(i).size(); j++) {
                paint.setTextSize(contentLabelTextSize);
                CharSequence con = content.get(i).get(j);
                max = Math.max(max, paint.measureText(con, 0, con.length()));
            }
            max = Math.max(minColumnWidth, max + cellPadding * 2);
            holdContentWidth += max + (i < columnLabelList.size() - 1 ? dividerSize : 0);
            columnWidthList.add(max);
        }

        judgeContentWidth();
    }

    private void judgeContentWidth() {
        if (viewWidth - rowLabelWidth - borderWidth > holdContentWidth && columnWidthList.size() > 0) {
            float offset = (viewWidth - rowLabelWidth - borderWidth - holdContentWidth - (columnWidthList.size() - 1) * dividerSize) / columnWidthList.size();
            holdContentWidth = viewWidth - rowLabelWidth - borderWidth;
            for (int i = 0; i < columnWidthList.size(); i++) {
                columnWidthList.set(i, columnWidthList.get(i) + offset);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
        int width = measureWidth(minimumWidth, widthMeasureSpec);
        int height = measureHeight(minimumHeight, heightMeasureSpec);
        viewWidth = width;
        viewHeight = height;

        judgeContentWidth();

        setMeasuredDimension(width, height);
    }

    private int measureWidth(int defaultWidth, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                paint.setTextSize(titleTextSize);
                float tLen = paint.measureText(title, 0, title.length()) + cellPadding * 2;
                paint.setTextSize(bottomLabelTextSize);
                float bLen = paint.measureText(bottomLabelContent, 0, bottomLabelContent.length()) + borderWidth + rowLabelWidth + cellPadding * 2;
                float cLen = rowLabelWidth + borderWidth + holdContentWidth;
                bLen = Math.max(cLen, bLen);
                defaultWidth = (int) (Math.max(tLen, bLen) + getPaddingLeft() + getPaddingRight());
                break;
            case MeasureSpec.EXACTLY:
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultWidth = Math.max(defaultWidth, specSize);
        }
        return defaultWidth;
    }


    private int measureHeight(int defaultHeight, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultHeight = (int) (titleCellHeight + (drawBottom ? bottomCellHeight : 0) + getPaddingTop() + getPaddingBottom() + rowLabelHeight * 2 + borderWidth * 2 + holdContentHeight - rowLabelHeight);
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultHeight = Math.max(defaultHeight, specSize);
                break;
        }
        return defaultHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        viewWidth = getWidth();
        viewHeight = getHeight();

        calculateDrawLabels();

        drawTitleArea(canvas);

        drawCornerArea(canvas);

        drawRowLabel(canvas);

        drawColumnLabel(canvas);

        drawContent(canvas);

        if (drawBottom) {
            drawBottomLabel(canvas);
        }
    }

    int startRowIndex;
    int stopRowIndex;

    int startColumnIndex;
    int stopColumnIndex;

    private void calculateDrawLabels() {
        startRowIndex = 0;
        stopRowIndex = 0;
        startColumnIndex = 0;
        stopColumnIndex = 0;

        float baseTop = titleCellHeight + rowLabelHeight + borderWidth;
        float baseBottom = drawBottom ? viewHeight - bottomCellHeight - borderWidth : viewHeight;

        for (int i = 0; i < rowLabelList.size(); i++) {
            int cellTop = (int) (tranY + baseTop + rowLabelHeight * curScale * i + dividerSize * i);
            int cellBottom = (int) (tranY + baseTop + rowLabelHeight * curScale * (i + 1) + dividerSize * i);
            if (cellBottom >= baseTop && cellTop <= baseTop) {
                startRowIndex = i;
            }
            if (cellTop <= baseBottom && cellBottom >= baseBottom) {
                stopRowIndex = i;
            }
            if (stopRowIndex != 0) {
                break;
            }
        }

        if (stopRowIndex == 0) {
            stopRowIndex = rowLabelList.size() - 1;
        }

        float baseLeft = rowLabelWidth + borderWidth;
        int startX = 0;
        for (int i = 0; i < columnLabelList.size(); i++) {
            int columnWidth = (int) (columnWidthList.get(i) * curScale);
            int cellLeft = (int) (tranX + baseLeft + startX);
            int cellRight = (int) (tranX + baseLeft + startX + columnWidth);

            if (cellLeft <= baseLeft && cellRight >= baseLeft) {
                startColumnIndex = i;
            }
            if (cellLeft <= viewWidth && cellRight >= viewWidth) {
                stopColumnIndex = i;
            }
            if (stopColumnIndex != 0) {
                break;
            }
            startX += columnWidth + dividerSize;
        }
        if (stopColumnIndex == 0) {
            stopColumnIndex = columnLabelList.size() - 1;
        } else if (stopColumnIndex < columnLabelList.size() - 1) {
            stopColumnIndex++;
        }
    }

    private void drawTitleArea(Canvas canvas) {
        paint.setColor(titleBackgroundColor);
        canvas.drawRect(0, 0, viewWidth, titleCellHeight, paint);
        float len = paint.measureText(title, 0, title.length());
        drawCharSequence(canvas, title, titleTextColor, titleTextSize, paint, (viewWidth - len) / 2, (titleCellHeight + titleTextSize) / 2);
    }

    void drawCharSequence(Canvas canvas, CharSequence charSequence, int defaultColor, float textSize, Paint paint, float startX, float startY) {
        paint.setTextSize(textSize);
        paint.setColor(defaultColor);
        if (charSequence instanceof Spannable) {
            Spannable sp = (Spannable) charSequence;
            ForegroundColorSpan[] spans = sp.getSpans(0, sp.length(), ForegroundColorSpan.class);
            int start = 0;
            float consumeWidth = 0;
            for (ForegroundColorSpan span : spans) {
                int spanStart = sp.getSpanStart(span);
                int spanEnd = sp.getSpanEnd(span);
                if (start < spanStart) {
                    paint.setColor(defaultColor);
                    canvas.drawText(charSequence, start, spanStart, startX + consumeWidth, startY, paint);
                    consumeWidth += paint.measureText(charSequence, start, spanStart);
                }

                paint.setColor(span.getForegroundColor());
                canvas.drawText(charSequence, spanStart, spanEnd, startX + consumeWidth, startY, paint);
                consumeWidth += paint.measureText(charSequence, spanStart, spanEnd);

                start = spanEnd;
            }
            if (start < charSequence.length()) {
                paint.setColor(defaultColor);
                canvas.drawText(charSequence, start, charSequence.length(), startX + consumeWidth, startY, paint);
            }
        } else {
            canvas.drawText(charSequence, 0, charSequence.length(), startX, startY, paint);
        }
    }


    private void drawCornerArea(Canvas canvas) {
        canvas.save();

        paint.setColor(rowLabelBackgroundColor);
        canvas.drawRect(0, titleCellHeight, rowLabelWidth, viewHeight, paint);

        paint.setTextSize(rowLabelTextSize);
        paint.setColor(rowLabelTextColor);

        float y = (rowLabelHeight + rowLabelTextSize) / 2;
        float len = paint.measureText(topCornerLabel, 0, topCornerLabel.length());

        drawCharSequence(canvas, topCornerLabel, rowLabelTextColor, rowLabelTextSize, paint, (rowLabelWidth - len) / 2, titleCellHeight + y);

        dividerPaint.setColor(borderColor);
        canvas.drawRect(0, titleCellHeight + rowLabelHeight, rowLabelWidth, titleCellHeight + rowLabelHeight + borderWidth, dividerPaint);
        dividerPaint.setColor(borderColor);
        canvas.drawRect(rowLabelWidth, titleCellHeight, rowLabelWidth + borderWidth, titleCellHeight + rowLabelHeight + borderWidth, dividerPaint);

        canvas.restore();
    }

    private void drawRowLabel(Canvas canvas) {
        canvas.save();
        canvas.clipRect(0, titleCellHeight + rowLabelHeight + borderWidth, rowLabelWidth + borderWidth, drawBottom ? viewHeight - bottomCellHeight - borderWidth : viewHeight);

        paint.setTextSize(rowLabelTextSize * curScale);
        paint.setColor(rowLabelTextColor);
        dividerPaint.setColor(rowTitleDividerColor);
        float y = (rowLabelHeight * curScale + rowLabelTextSize * curScale) / 2;
        float baseTop = titleCellHeight + rowLabelHeight + borderWidth;
        for (int i = startRowIndex; i <= stopRowIndex; i++) {
            CharSequence txt = rowLabelList.get(i);
            float len = paint.measureText(txt, 0, txt.length());

            drawCharSequence(canvas, txt, rowLabelTextColor, rowLabelTextSize * curScale, paint, (rowLabelWidth - len) / 2, baseTop + y + (rowLabelHeight * curScale + dividerSize) * i + tranY);

            canvas.drawRect(0,
                    baseTop + rowLabelHeight * curScale * (i + 1) + dividerSize * (i) + tranY,
                    rowLabelWidth,
                    baseTop + rowLabelHeight * curScale * (i + 1) + dividerSize * (i + 1) + tranY, dividerPaint);
        }
        dividerPaint.setColor(borderColor);
        canvas.drawRect(rowLabelWidth, titleCellHeight + rowLabelHeight + borderWidth, rowLabelWidth + borderWidth, viewHeight, dividerPaint);

        canvas.restore();
    }

    private void drawColumnLabel(Canvas canvas) {
        canvas.save();
        canvas.clipRect(rowLabelWidth + borderWidth, titleCellHeight, viewWidth, titleCellHeight + rowLabelHeight + borderWidth);

        paint.setColor(columnLabelBackgroundColor);
        canvas.drawRect(rowLabelWidth + borderWidth, titleCellHeight, viewWidth, titleCellHeight + rowLabelHeight, paint);

        float startX = rowLabelWidth + borderWidth + tranX + getOffsetStartX();
        for (int i = startColumnIndex; i <= stopColumnIndex; i++) {
            float columnWidth = columnWidthList.get(i) * curScale;
            float columnHeight = rowLabelHeight;

            CharSequence cs = columnLabelList.get(i);
            float tLen = paint.measureText(cs, 0, cs.length());
            drawCharSequence(canvas, cs, columnLabelTextColor, columnLabelTextSize * curScale, paint, startX + (columnWidth - tLen) / 2, titleCellHeight + (columnHeight + columnLabelTextSize * curScale) / 2);

            dividerPaint.setColor(columnTitleDividerColor);
            canvas.drawRect(startX + columnWidth, titleCellHeight, startX + columnWidth + dividerSize, titleCellHeight + columnHeight, dividerPaint);

            startX += columnWidth + dividerSize;
        }

        if (fadeColumnTitleBorder) {
            dividerPaint.setColor(contentDividerColor);
        } else {
            dividerPaint.setColor(borderColor);
        }
        canvas.drawRect(rowLabelWidth + borderWidth, titleCellHeight + rowLabelHeight, viewWidth, titleCellHeight + rowLabelHeight + borderWidth, dividerPaint);

        canvas.restore();
    }

    private float getOffsetStartX() {
        int x = 0;
        for (int i = 0; i < startColumnIndex; i++) {
            x += columnWidthList.get(i) * curScale + dividerSize;
        }
        return x;
    }

    private void drawContent(Canvas canvas) {
        canvas.save();
        canvas.clipRect(rowLabelWidth + borderWidth, titleCellHeight + rowLabelHeight + borderWidth, viewWidth, drawBottom ? viewHeight - bottomCellHeight - borderWidth : viewHeight);

        paint.setColor(contentLabelBackgroundColor);
        canvas.drawRect(rowLabelWidth + borderWidth,
                titleCellHeight + rowLabelHeight + borderWidth,
                viewWidth,
                drawBottom ? viewHeight - bottomCellHeight - borderWidth : viewHeight, paint);

        float startX = (rowLabelWidth + borderWidth + tranX + getOffsetStartX());
        float baseTop = titleCellHeight + rowLabelHeight + borderWidth;
        for (int i = startColumnIndex; i <= stopColumnIndex; i++) {
            float columnWidth = (columnWidthList.get(i) * curScale);
            float columnHeight = rowLabelHeight * curScale;

            ArrayList<CharSequence> contentArr = content.get(i);
            for (int j = startRowIndex; j <= stopRowIndex; j++) {

                CharSequence cs = contentArr.get(j);

                CustomFormat customFormat = findFormat(cs, j, i);
                if (customFormat == null) {
                    float cLen = paint.measureText(cs, 0, cs.length());

                    drawCharSequence(canvas, cs, contentLabelTextColor,
                            contentLabelTextSize * curScale, paint,
                            startX + (columnWidth - cLen) / 2,
                            baseTop + (columnHeight * (j)) + (columnHeight + contentLabelTextSize * curScale) / 2 + tranY + dividerSize * (j));
                } else {
                    cs = customFormat.charSequence == null ? cs : customFormat.charSequence;
                    float textSize = contentLabelTextSize * curScale;
                    int textColor = customFormat.textColor != -1 ? customFormat.textColor : contentLabelTextColor;
                    float cLen = paint.measureText(cs, 0, cs.length());

                    if (customFormat.bgColor != -1) {
                        paint.setColor(customFormat.bgColor);
                        canvas.drawRect(startX,
                                baseTop + (columnHeight * (j) + dividerSize * j) + tranY,
                                startX + columnWidth,
                                baseTop + (columnHeight * (j + 1) + dividerSize * (j)) + tranY, paint);
                    }
                    drawCharSequence(canvas, cs, textColor,
                            textSize, paint,
                            startX + (columnWidth - cLen) / 2,
                            baseTop + (columnHeight * (j)) + (columnHeight + textSize) / 2 + tranY + dividerSize * (j));
                }

                dividerPaint.setColor(contentDividerColor);
                canvas.drawRect(startX,
                        baseTop + (columnHeight * (j + 1) + dividerSize * j) + tranY,
                        startX + columnWidth,
                        baseTop + (columnHeight * (j + 1) + dividerSize * (j + 1)) + tranY, dividerPaint);
            }

            dividerPaint.setColor(columnDividerColor);
            canvas.drawRect(startX + columnWidth,
                    baseTop,
                    startX + columnWidth + dividerSize,
                    viewHeight, dividerPaint);

            startX += columnWidth + dividerSize;

        }
        canvas.restore();
    }

    private CustomFormat findFormat(CharSequence cs, int rowIndex, int columnIndex) {
        if (ruleList == null) {
            return null;
        }
        for (Rule rule : ruleList) {
            CustomFormat convert = rule.convert(cs, rowIndex, columnIndex);
            if (convert != null) {
                return convert;
            }
        }
        return null;
    }

    private void drawBottomLabel(Canvas canvas) {
        dividerPaint.setColor(borderColor);
        canvas.drawRect(0,
                viewHeight - bottomCellHeight - borderWidth,
                viewWidth,
                viewHeight - bottomCellHeight, dividerPaint);

        dividerPaint.setColor(borderColor);
        canvas.drawRect(rowLabelWidth,
                viewHeight - bottomCellHeight - borderWidth,
                rowLabelWidth + borderWidth,
                viewHeight, dividerPaint);

        float y = viewHeight - bottomCellHeight + (bottomCellHeight + bottomLabelTextSize) / 2;
        paint.setTextSize(bottomLabelTextSize);
        float len = paint.measureText(bottomCornerLabel, 0, bottomCornerLabel.length());

        drawCharSequence(canvas, bottomCornerLabel, bottomLabelTextColor, bottomLabelTextSize, paint, (rowLabelWidth - len) / 2, y);

        paint.setColor(bottomBackgroundColor);
        canvas.drawRect(rowLabelWidth + borderWidth, viewHeight - bottomCellHeight, viewWidth, viewHeight, paint);

        float tLen = paint.measureText(bottomLabelContent, 0, bottomLabelContent.length());

        drawCharSequence(canvas, bottomLabelContent, bottomLabelTextColor, bottomLabelTextSize, paint,
                rowLabelWidth + borderWidth + (viewWidth - rowLabelWidth - dividerSize - tLen) / 2,
                y);
    }

    float downX, downY;
    float tranX, tranY;
    float lastTranX, lastTranY;
    float activeTranX, activeTranY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return scaleGestureDetector.onTouchEvent(event) && gestureDetector.onTouchEvent(event);
    }

    private float getHoldContentWidth() {
        holdContentWidth = 0;
        for (int i = 0; i < columnWidthList.size(); i++) {
            holdContentWidth += columnWidthList.get(i) * curScale + dividerSize;
        }
        return holdContentWidth;
    }

    private float getHoldContentHeight() {
        holdContentHeight = content.get(0).size() * rowLabelHeight * curScale + (content.get(0).size() - 1) * dividerSize;
        return holdContentHeight;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            tranX = scroller.getCurrX();
            tranY = scroller.getCurrY();
            postInvalidate();
        }
    }

    private void judgeEdge() {
        tranX = Math.min(0, tranX);
        tranY = Math.min(0, tranY);

        float xRange = (viewWidth - rowLabelWidth - borderWidth) - getHoldContentWidth();
        float yRange = ((drawBottom ? viewHeight - bottomCellHeight - borderWidth : viewHeight) - titleCellHeight - rowLabelHeight - borderWidth) - getHoldContentHeight();
        xRange = Math.min(0, xRange);
        yRange = Math.min(0, yRange);

        tranX = Math.max(tranX, xRange);
        tranY = Math.max(tranY, yRange);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        curScale = detector.getScaleFactor() * preScale;//当前的伸缩值*之前的伸缩值 保持连续性
        //当放大倍数大于2或者缩小倍数小于0.5倍 就不伸缩图片 返回true取消处理伸缩手势事件
        if (curScale > 2) {
            curScale = 2f;
            preScale = 2f;
        }

        if (curScale < 0.5) {
            curScale = 0.5f;
            preScale = 0.5f;
        }

        if ((curScale < 2 && preScale < 2 && curScale != preScale) || (curScale > 0.5 && preScale > 0.5 && curScale != preScale)) {
            tranX = activeTranX * curScale;
            tranY = activeTranY * curScale;
            judgeEdge();
            postInvalidate();
            preScale = curScale;
            return true;
        }

        return false;
    }

    boolean isScaleMode = false;

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        isScaleMode = true;
        return supportScale;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        isScaleMode = false;
    }

    /**
     * 添加表格内容的自定义样式
     *
     * @param rule
     */
    public void addRule(Rule rule) {
        if (ruleList == null) {
            ruleList = new ArrayList<>();
        }
        if (rule != null) {
            ruleList.add(0, rule);
        }
        invalidate();
    }

    /**
     * 设置行标题
     *
     * @param collection
     */
    public void setRowTileData(Collection<CharSequence> collection) {
        rowLabelList.clear();
        for (CharSequence c : collection) {
            rowLabelList.add(c);
        }
    }

    /**
     * 设置列标题
     *
     * @param collection
     */
    public void setColumnTitleData(Collection<CharSequence> collection) {
        columnLabelList.clear();
        for (CharSequence c : collection) {
            columnLabelList.add(c);
        }
    }

    /**
     * 按照行数据设置表格内容，此二级集合，外层为行，内层为列
     *
     * @param collections
     */
    public void setContentDataByHorizontal(Collection<Collection<CharSequence>> collections) {
        content.clear();
        Collection<CharSequence>[] arr = new Collection[collections.size()];
        arr = collections.toArray(arr);
        int columnSize = arr[0].size();
        int rowSize = arr.length;
        for (int i = 0; i < columnSize; i++) {
            content.add(new ArrayList<CharSequence>());
        }
        CharSequence[] csArr = new CharSequence[columnSize];
        for (int row = 0; row < rowSize; row++) {
            Collection<CharSequence> collection = arr[row];
            csArr = collection.toArray(csArr);
            for (int column = 0; column < csArr.length; column++) {
                content.get(column).add(csArr[column]);
            }
        }
    }

    /**
     * 添加单行数据，行数据的个数必须与列数相等
     *
     * @param rowTitle
     * @param rowList
     */
    public void addRow(@Nullable CharSequence rowTitle, @NonNull ArrayList<CharSequence> rowList) {
        if (columnLabelList.size() == 0) {
            throw new IllegalStateException("There is not column title has been set.Please init column title first.");
        }

        if (columnLabelList.size() != rowList.size()) {
            throw new IllegalArgumentException("The row data's size must equals column title's size.");
        }
        if (rowTitle == null) {
            int index = rowLabelList.size() + 1;
            rowLabelList.add(index < 10 ? ("0" + index) : (String.valueOf(index)));
        } else {
            rowLabelList.add(rowTitle);
        }

        for (int i = 0; i < content.size(); i++) {
            content.get(i).add(rowList.get(i));
        }
        measureColumnMaxWidth();
        postInvalidate();
    }

    /**
     * 按照列数据设置表格内容，此二级集合，外层为列，内层为行
     *
     * @param collections
     */
    public void setContentDataByVertical(Collection<Collection<CharSequence>> collections) {
        content.clear();
        Collection<CharSequence>[] arr = new Collection[collections.size()];
        arr = collections.toArray(arr);
        int rowSize = arr[0].size();
        int columnSize = arr.length;
        CharSequence[] csArr = new CharSequence[columnSize];

        for (int column = 0; column < columnSize; column++) {
            Collection<CharSequence> collection = arr[column];
            csArr = collection.toArray(csArr);
            ArrayList<CharSequence> list = new ArrayList<>();
            for (int j = 0; j < rowSize; j++) {
                list.add(csArr[j]);
            }
            content.add(list);
        }
    }

    /**
     * 添加单列数据一级标题，列数据个数必须与行标题个数一直
     *
     * @param columnTitle
     * @param list
     */
    public void addColumn(@NonNull CharSequence columnTitle, @Nullable ArrayList<CharSequence> list) {
        columnLabelList.add(columnTitle);
        if (list != null) {
            if (list.size() != rowLabelList.size()) {
                throw new IllegalArgumentException("The column data's size must equals row title's size.");
            }
            content.add(list);
        } else {
            if (rowLabelList.size() > 0) {
                throw new IllegalArgumentException("The row size is bigger then 0,but the column data is empty.");
            }
            content.add(new ArrayList<CharSequence>());
        }
        measureColumnMaxWidth();
        postInvalidate();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (scroller != null && !scroller.isFinished()) {
            scroller.abortAnimation();
        }
        downX = e.getRawX();
        downY = e.getRawY();
        lastTranX = tranX;
        lastTranY = tranY;
        activeTranX = lastTranX;
        activeTranY = lastTranY;
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (mOnCellItemClickListener != null) {
            int column = findColumn(e.getX());
            int row = findRow(e.getY());
            if (row != -1 && column != -1) {
                mOnCellItemClickListener.onCellItemClicked(row, column);
            }
        }
        return false;
    }

    private int findRow(float y) {
        float v = -tranY + y - titleCellHeight - rowLabelHeight;
        float top = 0;
        float bottom = 0;
        for (int i = 0; i < rowLabelList.size(); i++) {
            top = rowLabelHeight * curScale * i + dividerSize * i;
            bottom = rowLabelHeight * curScale * (i + 1) + dividerSize * i;
            if (v > top && v < bottom) {
                return i;
            }
        }
        return -1;
    }

    private int findColumn(float x) {
        float v = -tranX + x - rowLabelWidth;
        float left = 0;
        float right = 0;
        for (int i = 0; i < columnWidthList.size(); i++) {
            left = right + (i == 0 ? 0 : dividerSize);
            right = left + columnWidthList.get(i) * curScale;
            if (v > left && v < right) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        int pointerCount = e2.getPointerCount();
        //如果是多点触摸则不进行平移操作
        if (pointerCount > 1) {
            return false;
        }

        tranX -= distanceX;
        tranY -= distanceY;

        tranX = Math.min(0, tranX);
        tranY = Math.min(0, tranY);

        float xRange = (viewWidth - rowLabelWidth - borderWidth) - getHoldContentWidth();
        float yRange = ((drawBottom ? viewHeight - bottomCellHeight - borderWidth : viewHeight) - titleCellHeight - rowLabelHeight - borderWidth) - getHoldContentHeight();
        xRange = Math.min(0, xRange);
        yRange = Math.min(0, yRange);

        tranX = Math.max(tranX, xRange);
        tranY = Math.max(tranY, yRange);

        invalidate();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (Math.abs(velocityX) > 0 || Math.abs(velocityY) > 0) {
            float xRange = (viewWidth - rowLabelWidth - borderWidth) - getHoldContentWidth();
            float yRange = ((drawBottom ? viewHeight - bottomCellHeight - borderWidth : viewHeight) - titleCellHeight - rowLabelHeight - borderWidth) - getHoldContentHeight();
            xRange = Math.min(0, xRange);
            yRange = Math.min(0, yRange);

            if (velocityConstraintOrientation) {
                if (Math.abs(velocityX) > Math.abs(velocityY) * 3) {
                    scroller.fling((int) tranX, (int) tranY, (int) velocityX, (int) 0, (int) xRange, 0, (int) yRange, 0);
                } else if (Math.abs(velocityY) > Math.abs(velocityX) * 3) {
                    scroller.fling((int) tranX, (int) tranY, (int) 0, (int) velocityY, (int) xRange, 0, (int) yRange, 0);
                } else {
                    scroller.fling((int) tranX, (int) tranY, (int) velocityX, (int) velocityY, (int) xRange, 0, (int) yRange, 0);
                }
            } else {
                scroller.fling((int) tranX, (int) tranY, (int) velocityX, (int) velocityY, (int) xRange, 0, (int) yRange, 0);
            }
        }
        invalidate();
        return true;
    }

    public void removeRule(Rule rule) {
        if (ruleList != null) {
            ruleList.remove(rule);
        }
    }

    public interface OnCellItemClickListener {
        void onCellItemClicked(int rowIndex, int columnIndex);
    }

    public void setOnCellItemClickListener(OnCellItemClickListener l) {
        this.mOnCellItemClickListener = l;
    }

    public interface DataGenerator {
        int getRowCount();

        CharSequence getRowTitle(int rowIndex);

        int getColumnCount();

        CharSequence getColumnTitle(int columnIndex);

        CharSequence getContentData(int rowIndex, int columnIndex);
    }

    /**
     * 设置数据构造器，推荐使用
     *
     * @param dataGenerator
     */
    public void setDataGenerator(DataGenerator dataGenerator) {
        if (dataGenerator != null) {
            rowLabelList.clear();
            columnLabelList.clear();
            content.clear();

            int rowCount = dataGenerator.getRowCount();
            int columnCount = dataGenerator.getColumnCount();
            for (int row = 0; row < rowCount; row++) {
                rowLabelList.add(dataGenerator.getRowTitle(row));
            }
            for (int column = 0; column < columnCount; column++) {
                columnLabelList.add(dataGenerator.getColumnTitle(column));
                ArrayList<CharSequence> columnList = new ArrayList<>();
                for (int row = 0; row < rowCount; row++) {
                    columnList.add(dataGenerator.getContentData(row, column));
                }
                content.add(columnList);
            }
            onContentPrepareFinished();
        }
    }

    public void onContentPrepareFinished() {
        if (columnLabelList.isEmpty()) {
            for (int i = 1; i <= 2; i++) {
                columnLabelList.add("Label" + (i < 10 ? ("0" + i) : String.valueOf(i)));

                ArrayList<CharSequence> contentList = new ArrayList<>();
                for (int x = 1; x <= 2; x++) {
                    String str = String.valueOf(new Random().nextInt(100) * new Random().nextInt(100));
                    contentList.add(str);
                }
                content.add(contentList);
            }
        }
        if (rowLabelList.isEmpty()) {
            for (int i = 1; i <= 2; i++) {
                rowLabelList.add(i < 10 ? ("0" + i) : String.valueOf(i));
            }
        }
        if (content.size() == 0) {
            throw new RuntimeException("The content date has not set yet");
        }
        measureColumnMaxWidth();
        postInvalidate();
    }

    public void setSupportScale(boolean b) {
        this.supportScale = b;
    }

    public void setTitle(CharSequence title) {
        this.title = title;
        postInvalidate();
    }

    public void setTopCornerLabel(CharSequence topCornerLabel) {
        this.topCornerLabel = topCornerLabel;
        postInvalidate();
    }

    public void setBottomCornerLabel(CharSequence bottomCornerLabel) {
        this.bottomCornerLabel = bottomCornerLabel;
        postInvalidate();
    }

    public void setBottomLabelContent(CharSequence bottomLabelContent) {
        this.bottomLabelContent = bottomLabelContent;
        postInvalidate();
    }

    public void setColumnTitleDividerColor(int columnTitleDividerColor) {
        this.columnTitleDividerColor = columnTitleDividerColor;
        postInvalidate();
    }

    public void setRowTitleDividerColor(int rowTitleDividerColor) {
        this.rowTitleDividerColor = rowTitleDividerColor;
        postInvalidate();
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        postInvalidate();
    }

    public void setFadeColumnTitleBorder(boolean fadeColumnTitleBorder) {
        this.fadeColumnTitleBorder = fadeColumnTitleBorder;
    }

    public void setTitleTextSize(float titleTextSize) {
        this.titleTextSize = titleTextSize;
        postInvalidate();
    }

    public void setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
        postInvalidate();
    }

    public void setTitleBackgroundColor(int titleBackgroundColor) {
        this.titleBackgroundColor = titleBackgroundColor;
        postInvalidate();
    }

    public void setRowLabelTextSize(float rowLabelTextSize) {
        this.rowLabelTextSize = rowLabelTextSize;
        postInvalidate();
    }

    public void setRowLabelTextColor(int rowLabelTextColor) {
        this.rowLabelTextColor = rowLabelTextColor;
        postInvalidate();
    }

    public void setRowLabelBackgroundColor(int rowLabelBackgroundColor) {
        this.rowLabelBackgroundColor = rowLabelBackgroundColor;
        postInvalidate();
    }

    public void setColumnLabelTextSize(float columnLabelTextSize) {
        this.columnLabelTextSize = columnLabelTextSize;
        postInvalidate();
    }

    public void setColumnLabelTextColor(int columnLabelTextColor) {
        this.columnLabelTextColor = columnLabelTextColor;
        postInvalidate();
    }

    public void setColumnLabelBackgroundColor(int columnLabelBackgroundColor) {
        this.columnLabelBackgroundColor = columnLabelBackgroundColor;
        postInvalidate();
    }

    public void setColumnDividerColor(int columnDividerColor) {
        this.columnDividerColor = columnDividerColor;
        postInvalidate();
    }

    public void setContentLabelTextSize(float contentLabelTextSize) {
        this.contentLabelTextSize = contentLabelTextSize;
        postInvalidate();
    }

    public void setContentLabelTextColor(int contentLabelTextColor) {
        this.contentLabelTextColor = contentLabelTextColor;
        postInvalidate();
    }

    public void setContentLabelBackgroundColor(int contentLabelBackgroundColor) {
        this.contentLabelBackgroundColor = contentLabelBackgroundColor;
        postInvalidate();
    }

    public void setContentDividerColor(int contentDividerColor) {
        this.contentDividerColor = contentDividerColor;
        postInvalidate();
    }

    public void setCellPadding(int cellPadding) {
        this.cellPadding = cellPadding;
        postInvalidate();
    }

    public void setBottomBackgroundColor(int bottomBackgroundColor) {
        this.bottomBackgroundColor = bottomBackgroundColor;
        postInvalidate();
    }

    public void setBottomLabelTextSize(int bottomLabelTextSize) {
        this.bottomLabelTextSize = bottomLabelTextSize;
        postInvalidate();
    }

    public void setBottomLabelTextColor(int bottomLabelTextColor) {
        this.bottomLabelTextColor = bottomLabelTextColor;
        postInvalidate();
    }

    public void setDrawBottom(boolean drawBottom) {
        this.drawBottom = drawBottom;
        postInvalidate();
    }

    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize;
        postInvalidate();
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        postInvalidate();
    }

    public void setTitleCellHeight(int titleCellHeight) {
        this.titleCellHeight = titleCellHeight;
        postInvalidate();
    }

    public void setBottomCellHeight(int bottomCellHeight) {
        this.bottomCellHeight = bottomCellHeight;
        postInvalidate();
    }

    public void setMinRowHeight(int minRowHeight) {
        this.minRowHeight = minRowHeight;
        postInvalidate();
    }

    public void setMinColumnWidth(int minColumnWidth) {
        this.minColumnWidth = minColumnWidth;
        postInvalidate();
    }

    public void setMinRowColumnWidth(int minRowColumnWidth) {
        this.minRowColumnWidth = minRowColumnWidth;
        postInvalidate();
    }

    public void setVelocityConstraintOrientation(boolean b) {
        this.velocityConstraintOrientation = b;
    }
}
