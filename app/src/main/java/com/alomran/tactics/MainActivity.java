
package com.alomran.tactics;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.provider.MediaStore;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import android.text.InputType;
import java.io.OutputStream;
import java.util.*;

public class MainActivity extends Activity {
    private TacticsBoardView board;
    private final ArrayList<EditText> fields = new ArrayList<>();
    private EditText titleField, subField;
    private CheckBox arrowsBox, numbersBox, panelsBox;

    String[] labels = {"الحارس", "مدافع يسار", "مدافع وسط", "مدافع يمين", "وسط يسار", "ارتكاز", "وسط يمين", "رأس حربة"};
    String[] defaults = {"ابو الليث", "حسونة", "عبدالله", "عبدالخالق", "سلوم", "جليبيب", "ابو احمد", "يوسف"};

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(7,19,13));
        getWindow().setNavigationBarColor(Color.rgb(7,19,13));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(7,19,13));
        root.setPadding(dp(10), dp(8), dp(10), dp(8));

        TextView head = new TextView(this);
        head.setText("العمران - مصمم الخطط");
        head.setTextColor(Color.WHITE);
        head.setTextSize(22);
        head.setGravity(Gravity.CENTER);
        head.setTypeface(Typeface.DEFAULT_BOLD);
        root.addView(head, new LinearLayout.LayoutParams(-1, dp(40)));

        ScrollView scroll = new ScrollView(this);
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(0, dp(4), 0, dp(10));
        scroll.addView(form);

        titleField = makeInput("عنوان الخطة", "3-3-1");
        form.addView(titleField);

        for (int i=0; i<labels.length; i++) {
            EditText e = makeInput(labels[i], defaults[i]);
            fields.add(e);
            form.addView(e);
        }
        subField = makeInput("البدلاء - اكتب كل بديل بسطر أو افصل بينهم بفاصلة", "مصطفى عثمان");
        subField.setMinLines(1);
        subField.setMaxLines(4);
        form.addView(subField);

        arrowsBox = new CheckBox(this);
        arrowsBox.setText("إظهار أسهم التحركات الهجومية والدفاعية");
        arrowsBox.setTextColor(Color.WHITE);
        arrowsBox.setTextSize(16);
        arrowsBox.setChecked(false);
        form.addView(arrowsBox);

        numbersBox = new CheckBox(this);
        numbersBox.setText("إظهار أرقام اللاعبين");
        numbersBox.setTextColor(Color.WHITE);
        numbersBox.setTextSize(16);
        numbersBox.setChecked(true);
        form.addView(numbersBox);

        panelsBox = new CheckBox(this);
        panelsBox.setText("إظهار اللوغو ومربع البدلاء ومفتاح المراكز");
        panelsBox.setTextColor(Color.WHITE);
        panelsBox.setTextSize(16);
        panelsBox.setChecked(true);
        form.addView(panelsBox);

        LinearLayout btns = new LinearLayout(this);
        btns.setOrientation(LinearLayout.HORIZONTAL);
        Button apply = makeButton("تطبيق");
        Button save = makeButton("حفظ صورة");
        Button clearArrows = makeButton("بدون أسهم");
        btns.addView(apply, new LinearLayout.LayoutParams(0, dp(48), 1));
        btns.addView(save, new LinearLayout.LayoutParams(0, dp(48), 1));
        btns.addView(clearArrows, new LinearLayout.LayoutParams(0, dp(48), 1));
        form.addView(btns);

        board = new TacticsBoardView(this);
        board.setPlan(makeData());
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        root.addView(board, new LinearLayout.LayoutParams(-1, dp(360)));
        setContentView(root);

        apply.setOnClickListener(v -> { board.setPlan(makeData()); Toast.makeText(this,"تم تحديث التصميم",Toast.LENGTH_SHORT).show(); });
        clearArrows.setOnClickListener(v -> { arrowsBox.setChecked(false); board.setPlan(makeData()); });
        save.setOnClickListener(v -> { board.setPlan(makeData()); saveBoardImage(); });
    }

    private PlanData makeData() {
        PlanData d = new PlanData();
        d.title = safe(titleField.getText().toString(), "3-3-1");
        for (int i=0; i<8; i++) d.players[i] = safe(fields.get(i).getText().toString(), defaults[i]);
        d.subs = parseSubs(subField.getText().toString());
        d.showArrows = arrowsBox.isChecked();
        d.showNumbers = numbersBox.isChecked();
        d.showPanels = panelsBox.isChecked();
        return d;
    }

    private String safe(String s, String def) { s=s.trim(); return s.isEmpty()?def:s; }
    private ArrayList<String> parseSubs(String s) {
        ArrayList<String> out = new ArrayList<>();
        String[] parts = s.replace('،', ',').split("[,\\n]");
        for(String p: parts){ p=p.trim(); if(!p.isEmpty()) out.add(p); }
        if(out.isEmpty()) out.add("مصطفى عثمان");
        return out;
    }

    private EditText makeInput(String hint, String value) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setText(value);
        e.setTextSize(16);
        e.setSingleLine(false);
        e.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        e.setTextDirection(View.TEXT_DIRECTION_RTL);
        e.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        e.setTextColor(Color.WHITE);
        e.setHintTextColor(Color.rgb(200,210,205));
        e.setPadding(dp(12),0,dp(12),0);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(13,34,28));
        bg.setStroke(dp(1), Color.rgb(211,177,89));
        bg.setCornerRadius(dp(10));
        e.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(48));
        lp.setMargins(0, dp(4),0,dp(4));
        e.setLayoutParams(lp);
        return e;
    }

    private Button makeButton(String txt) {
        Button b = new Button(this);
        b.setText(txt);
        b.setTextColor(Color.WHITE);
        b.setTextSize(15);
        b.setTypeface(Typeface.DEFAULT_BOLD);
        GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{Color.rgb(19,73,100), Color.rgb(5,27,40)});
        bg.setStroke(dp(1), Color.rgb(218,179,81));
        bg.setCornerRadius(dp(12));
        b.setBackground(bg);
        return b;
    }

    private void saveBoardImage() {
        Bitmap bmp = Bitmap.createBitmap(1080, 1600, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        board.drawPoster(c, 1080, 1600);
        String name = "AlOmran_Tactics_" + System.currentTimeMillis() + ".png";
        try {
            Uri uri;
            OutputStream os;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues cv = new ContentValues();
                cv.put(MediaStore.Images.Media.DISPLAY_NAME, name);
                cv.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                cv.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/AlOmranTactics");
                uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
                os = getContentResolver().openOutputStream(uri);
            } else {
                java.io.File dir = new java.io.File(getExternalFilesDir(null), "AlOmranTactics");
                dir.mkdirs();
                java.io.File file = new java.io.File(dir, name);
                os = new java.io.FileOutputStream(file);
                uri = Uri.fromFile(file);
            }
            bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush(); os.close();
            Toast.makeText(this, "تم حفظ الصورة في المعرض", Toast.LENGTH_LONG).show();
        } catch(Exception ex) {
            Toast.makeText(this, "تعذر الحفظ: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    int dp(int v){ return (int)(v*getResources().getDisplayMetrics().density + 0.5f); }

    static class PlanData {
        String title = "3-3-1";
        String[] players = new String[8];
        ArrayList<String> subs = new ArrayList<>();
        boolean showArrows=false, showNumbers=true, showPanels=true;
    }

    public static class TacticsBoardView extends View {
        private PlanData data = new PlanData();
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        TextPaint tp = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        public TacticsBoardView(Context c){ super(c); setLayerType(View.LAYER_TYPE_SOFTWARE, null); }
        public void setPlan(PlanData d){ data=d; invalidate(); }

        @Override protected void onDraw(Canvas canvas){ super.onDraw(canvas); drawPoster(canvas, getWidth(), getHeight()); }

        public void drawPoster(Canvas canvas, int W, int H) {
            float scale = Math.min(W/1080f, H/1600f);
            canvas.save();
            canvas.scale(scale, scale);
            float offX = (W/scale - 1080)/2f;
            float offY = (H/scale - 1600)/2f;
            canvas.translate(offX, offY);
            render(canvas);
            canvas.restore();
        }

        private void render(Canvas c) {
            p.setStyle(Paint.Style.FILL);
            LinearGradient bg = new LinearGradient(0,0,0,1600, Color.rgb(5,26,10), Color.rgb(10,48,16), Shader.TileMode.CLAMP);
            p.setShader(bg); c.drawRect(0,0,1080,1600,p); p.setShader(null);
            drawTitle(c);
            RectF pitch = new RectF(70,190,1010,1340);
            drawStadium(c, pitch);
            drawPitch(c, pitch);
            if (data.showArrows) drawMovementArrows(c, pitch);
            drawPlayers(c, pitch);
            if (data.showPanels) drawBottomPanels(c);
        }

        private void drawTitle(Canvas c) {
            tp.setTextAlign(Paint.Align.CENTER);
            tp.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            tp.setTextSize(56); tp.setColor(Color.WHITE);
            c.drawText("خطة " + data.title, 540, 83, tp);
            p.setStrokeWidth(3); p.setColor(Color.rgb(218,179,81));
            c.drawLine(160,65,335,65,p); c.drawLine(745,65,920,65,p);
            p.setStyle(Paint.Style.FILL); c.drawCircle(335,65,5,p); c.drawCircle(745,65,5,p);
        }

        private void drawStadium(Canvas c, RectF r) {
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.rgb(220,215,207));
            c.drawRoundRect(r.left-48,r.top-70,r.right+48,r.bottom+42,16,16,p);
            p.setColor(Color.rgb(113,29,26));
            c.drawRoundRect(r.left-22,r.top-18,r.right+22,r.bottom+20,8,8,p);
            p.setColor(Color.rgb(220,215,207));
            c.drawRect(r.left-35,r.top-70,r.right+35,r.top-18,p);
            // stands
            p.setColor(Color.rgb(50,34,39));
            for(int i=0;i<5;i++) c.drawRect(r.left+80, r.top-55+i*9, r.right-80, r.top-51+i*9, p);
            // buildings around pitch
            p.setColor(Color.rgb(198,187,177));
            c.drawRoundRect(450,r.top-92,630,r.top-8,8,8,p);
            p.setColor(Color.rgb(55,35,42)); c.drawRect(488,r.top-22,592,r.top-8,p);
            p.setColor(Color.rgb(198,187,177)); c.drawRoundRect(895,r.top-70,998,r.top-10,8,8,p);
            p.setColor(Color.rgb(30,30,30)); c.drawRect(918,r.top-55,970,r.top-25,p);
        }

        private void drawPitch(Canvas c, RectF r) {
            p.setStyle(Paint.Style.FILL);
            for (int i=0;i<12;i++) {
                p.setColor(i%2==0 ? Color.rgb(38,145,28) : Color.rgb(31,125,24));
                float x1 = r.left + i*r.width()/12f;
                c.drawRect(x1, r.top, x1 + r.width()/12f + 1, r.bottom, p);
            }
            // subtle checker
            p.setAlpha(25);
            for(int y=0;y<12;y++) for(int x=0;x<8;x++) if((x+y)%2==0){
                p.setColor(Color.WHITE);
                c.drawRect(r.left+x*r.width()/8f, r.top+y*r.height()/12f, r.left+(x+1)*r.width()/8f, r.top+(y+1)*r.height()/12f, p);
            }
            p.setAlpha(255);
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(5); p.setColor(Color.WHITE);
            c.drawRect(r,p);
            float cx=r.centerX();
            c.drawLine(r.left, r.centerY(), r.right, r.centerY(), p);
            c.drawCircle(cx, r.centerY(), 95, p);
            // penalty boxes bottom and top
            c.drawRect(cx-150, r.bottom-170, cx+150, r.bottom, p);
            c.drawRect(cx-82, r.bottom-84, cx+82, r.bottom, p);
            c.drawRect(cx-150, r.top, cx+150, r.top+170, p);
            c.drawRect(cx-82, r.top, cx+82, r.top+84, p);
            // corner arcs
            c.drawArc(new RectF(r.left-35,r.top-35,r.left+35,r.top+35),0,90,false,p);
            c.drawArc(new RectF(r.right-35,r.top-35,r.right+35,r.top+35),90,90,false,p);
            c.drawArc(new RectF(r.left-35,r.bottom-35,r.left+35,r.bottom+35),270,90,false,p);
            c.drawArc(new RectF(r.right-35,r.bottom-35,r.right+35,r.bottom+35),180,90,false,p);
            // goals
            p.setStyle(Paint.Style.FILL); p.setColor(Color.rgb(225,225,220));
            c.drawRect(cx-70,r.bottom, cx+70, r.bottom+24,p);
            c.drawRect(cx-70,r.top-24, cx+70, r.top,p);
        }

        private void drawMovementArrows(Canvas c, RectF r) {
            float[][] pos = positions(r);
            // Yellow attack: solid/curved
            yellowArrow(c, pos[7][0], pos[7][1]-55, pos[7][0], r.top+85, false);
            yellowArrow(c, pos[7][0]-25, pos[7][1]-40, pos[7][0]-130, pos[7][1]-120, true);
            yellowArrow(c, pos[7][0]+25, pos[7][1]-40, pos[7][0]+130, pos[7][1]-120, true);
            yellowArrow(c, pos[4][0]-70, pos[4][1]+20, pos[4][0]-120, pos[4][1]-100, true);
            yellowArrow(c, pos[4][0]+25, pos[4][1]-10, pos[4][0]+90, pos[4][1]-90, false);
            yellowArrow(c, pos[6][0]+70, pos[6][1]+20, pos[6][0]+120, pos[6][1]-100, true);
            yellowArrow(c, pos[6][0]-25, pos[6][1]-10, pos[6][0]-90, pos[6][1]-90, false);
            yellowArrow(c, pos[5][0], pos[5][1]-55, pos[5][0], pos[5][1]-185, false);
            yellowArrow(c, pos[5][0]-40, pos[5][1]-5, pos[5][0]-140, pos[5][1]-70, true);
            yellowArrow(c, pos[5][0]+40, pos[5][1]-5, pos[5][0]+140, pos[5][1]-70, true);
            yellowArrow(c, pos[1][0]-35, pos[1][1], pos[1][0]-95, pos[1][1]-150, true);
            yellowArrow(c, pos[1][0], pos[1][1]-50, pos[1][0], pos[1][1]-190, false);
            yellowArrow(c, pos[3][0]+35, pos[3][1], pos[3][0]+95, pos[3][1]-150, true);
            yellowArrow(c, pos[3][0], pos[3][1]-50, pos[3][0], pos[3][1]-190, false);
            yellowArrow(c, pos[2][0], pos[2][1]-55, pos[2][0], pos[2][1]-170, false);
            yellowArrow(c, pos[0][0]-35, pos[0][1]-50, pos[0][0]-150, pos[0][1]-135, false);
            yellowArrow(c, pos[0][0]+35, pos[0][1]-50, pos[0][0]+150, pos[0][1]-135, false);
            // Red defense return
            redDashed(c, pos[7][0], pos[7][1]+55, pos[7][0], pos[7][1]+145);
            redDashed(c, pos[4][0], pos[4][1]+50, pos[4][0]+45, pos[4][1]+150);
            redDashed(c, pos[6][0], pos[6][1]+50, pos[6][0]-45, pos[6][1]+150);
            redDashed(c, pos[5][0], pos[5][1]+55, pos[5][0], pos[5][1]+150);
            redDashed(c, pos[2][0], pos[2][1]+55, pos[2][0], pos[2][1]+160);
            redDashed(c, pos[1][0]+40, pos[1][1]+60, pos[1][0]+95, pos[1][1]+145);
            redDashed(c, pos[3][0]-40, pos[3][1]+60, pos[3][0]-95, pos[3][1]+145);
            redDashed(c, pos[0][0], pos[0][1]+55, pos[0][0], r.bottom+15);
        }

        private void yellowArrow(Canvas c, float x1,float y1,float x2,float y2, boolean curved){
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(8); p.setStrokeCap(Paint.Cap.ROUND); p.setColor(Color.rgb(255,220,35));
            if(curved){
                Path path=new Path(); path.moveTo(x1,y1);
                float cx=(x1+x2)/2 + (x2>x1?35:-35); float cy=(y1+y2)/2;
                path.quadTo(cx,cy,x2,y2); c.drawPath(path,p);
            } else c.drawLine(x1,y1,x2,y2,p);
            drawHead(c,x1,y1,x2,y2,Color.rgb(255,220,35));
        }
        private void redDashed(Canvas c, float x1,float y1,float x2,float y2){
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(6); p.setStrokeCap(Paint.Cap.ROUND); p.setColor(Color.rgb(255,72,48));
            p.setPathEffect(new DashPathEffect(new float[]{18,14},0)); c.drawLine(x1,y1,x2,y2,p); p.setPathEffect(null);
            drawHead(c,x1,y1,x2,y2,Color.rgb(255,72,48));
        }
        private void drawHead(Canvas c,float x1,float y1,float x2,float y2,int color){
            double ang=Math.atan2(y2-y1,x2-x1); float len=24; float a=0.55f;
            Path h=new Path(); h.moveTo(x2,y2);
            h.lineTo((float)(x2-len*Math.cos(ang-a)), (float)(y2-len*Math.sin(ang-a)));
            h.lineTo((float)(x2-len*Math.cos(ang+a)), (float)(y2-len*Math.sin(ang+a)));
            h.close(); p.setStyle(Paint.Style.FILL); p.setColor(color); c.drawPath(h,p);
        }

        private float[][] positions(RectF r) {
            float cx = r.centerX();
            return new float[][]{
                {cx, r.bottom-78},
                {r.left+r.width()*0.20f, r.bottom-275},
                {cx, r.bottom-275},
                {r.right-r.width()*0.20f, r.bottom-275},
                {r.left+r.width()*0.28f, r.centerY()-75},
                {cx, r.centerY()+120},
                {r.right-r.width()*0.28f, r.centerY()-75},
                {cx, r.top+220}
            };
        }

        private void drawPlayers(Canvas c, RectF r) {
            float[][] pos = positions(r);
            for(int i=0;i<8;i++) drawPlayer(c, pos[i][0], pos[i][1], i+1, data.players[i]);
        }

        private void drawPlayer(Canvas c, float x, float y, int num, String name) {
            // circle shadow
            p.setStyle(Paint.Style.FILL); p.setColor(Color.argb(120,0,0,0)); c.drawCircle(x+5,y+6,32,p);
            RadialGradient rg = new RadialGradient(x-12,y-14,58, Color.rgb(34,137,255), Color.rgb(0,49,160), Shader.TileMode.CLAMP);
            p.setShader(rg); c.drawCircle(x,y,32,p); p.setShader(null);
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(3); p.setColor(Color.WHITE); c.drawCircle(x,y,32,p);
            if(data.showNumbers){
                tp.setTextAlign(Paint.Align.CENTER); tp.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD)); tp.setTextSize(34); tp.setColor(Color.WHITE);
                c.drawText(String.valueOf(num), x, y+12, tp);
            }
            float bw = Math.max(92, textWidth(name, 26)+34);
            RectF box = new RectF(x-bw/2, y+38, x+bw/2, y+78);
            p.setStyle(Paint.Style.FILL); p.setColor(Color.rgb(7,23,31)); c.drawRoundRect(box,8,8,p);
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(2); p.setColor(Color.rgb(216,176,78)); c.drawRoundRect(box,8,8,p);
            tp.setTextAlign(Paint.Align.CENTER); tp.setTextSize(26); tp.setColor(Color.WHITE); tp.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            c.drawText(name, x, y+68, tp);
        }

        private float textWidth(String s, float size){ tp.setTextSize(size); tp.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD)); return tp.measureText(s); }

        private void drawBottomPanels(Canvas c) {
            drawLogo(c, 540, 1462, 62);
            tp.setTextAlign(Paint.Align.CENTER); tp.setTextSize(30); tp.setColor(Color.WHITE); tp.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            RectF logoName = new RectF(460, 1515, 620, 1560);
            p.setStyle(Paint.Style.FILL); p.setColor(Color.rgb(7,24,31)); c.drawRoundRect(logoName,9,9,p);
            c.drawText("العمران",540,1548,tp);
            // left key
            RectF key = new RectF(55, 1380, 390, 1560);
            darkPanel(c,key);
            tp.setTextAlign(Paint.Align.CENTER); tp.setTextSize(28); c.drawText("مفتاح المراكز",222,1420,tp);
            tp.setTextSize(22); tp.setTextAlign(Paint.Align.RIGHT);
            c.drawText("حارس مرمى     1",350,1460,tp);
            c.drawText("مدافعون        3",350,1490,tp);
            c.drawText("لاعبي وسط      3",350,1520,tp);
            c.drawText("مهاجم          1",350,1550,tp);
            // right subs
            RectF subs = new RectF(690, 1380, 1025, 1560);
            darkPanel(c,subs);
            tp.setTextAlign(Paint.Align.CENTER); tp.setTextSize(28); c.drawText("البدلاء",858,1420,tp);
            int n=12; int y=1470;
            for(String s: data.subs){ drawSub(c, 850, y, n++, s); y+=50; if(y>1550) break; }
        }
        private void darkPanel(Canvas c, RectF r){
            p.setStyle(Paint.Style.FILL); p.setColor(Color.rgb(5,23,28)); c.drawRoundRect(r,14,14,p);
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(2); p.setColor(Color.rgb(215,177,85)); c.drawRoundRect(r,14,14,p);
        }
        private void drawSub(Canvas c, int x, int y, int num, String name){
            p.setStyle(Paint.Style.FILL); p.setColor(Color.rgb(10,45,70)); c.drawCircle(x-100,y-10,25,p);
            p.setStyle(Paint.Style.STROKE); p.setColor(Color.WHITE); p.setStrokeWidth(2); c.drawCircle(x-100,y-10,25,p);
            tp.setTextAlign(Paint.Align.CENTER); tp.setTextSize(24); tp.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD)); tp.setColor(Color.WHITE);
            c.drawText(String.valueOf(num),x-100,y,tp);
            tp.setTextAlign(Paint.Align.RIGHT); tp.setTextSize(25); c.drawText(name,x+95,y,tp);
        }
        private void drawLogo(Canvas c, float x, float y, float rad){
            p.setStyle(Paint.Style.FILL); p.setColor(Color.rgb(6,24,31)); c.drawCircle(x,y,rad,p);
            p.setStyle(Paint.Style.STROKE); p.setStrokeWidth(4); p.setColor(Color.rgb(218,179,81)); c.drawCircle(x,y,rad-4,p);
            p.setStrokeWidth(8); p.setStrokeCap(Paint.Cap.ROUND);
            c.drawLine(x-30,y+12,x+25,y+12,p); c.drawLine(x-20,y-5,x+33,y-5,p); c.drawLine(x-10,y-22,x+20,y-22,p);
            p.setStrokeWidth(5); c.drawArc(new RectF(x-38,y-38,x+38,y+38),-70,140,false,p);
        }
    }

    static class TextPaint extends Paint { TextPaint(int flags){ super(flags); } }
}
