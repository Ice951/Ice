package com.example.setka;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.net.Uri;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class tor_com extends AppCompatActivity implements OnClickListener {

    // Объявление и описание различных объектов и констант
    TextView TVShortInf, tvColor;
    final String LOG_TAG = "myLogs";
    Cursor c, cp00,cp02, cp022, cp03, ca, ct1,ct1fin, ct2,ct2fin,ct2check, ct3,ct3fin, ct3check;
    DBHelper dbh;
    SQLiteDatabase db;
    final String DB_NAME = "setka"; // имя БД
    final int DB_VERSION = 3; // версия БД
    final String DB_TABLET = "tournaments as T";
    final String DB_COLUMNTID[] ={"T._id"};
    Button btnMain, BtnFinT,btnt1,btnt2,btnt3;
    String ThisT, LogidT, PT1, PT21, PT22, PT3, idv, PT1fin,PT2fin,PT3fin;
    int idT, intidv, test123, PT2Check,PT3Check;
    ListView LVPlayers,LVLongInf;
    GridView lvTour1, lvTour2, lvTour3;
    SimpleCursorAdapter adapterAllInfo,adapterT1,adapterT2,adapterT3;
    ArrayList<String> PinTour1New = new ArrayList<>();
    ArrayList<String> PinTour21New = new ArrayList<>();
    ArrayList<String> PinTour22New = new ArrayList<>();
    ArrayList<String> PinTour3New = new ArrayList<>();
    ArrayList<String> PinTour1Done = new ArrayList<>();
    ArrayList<String> PinTour2Done = new ArrayList<>();
    ArrayList<String> PinTour3Done = new ArrayList<>();
    ArrayList<Integer> PinTour2Check = new ArrayList<>();
    ArrayList<Integer> PinTour3Check = new ArrayList<>();
    String[] P1T1,P2T1,P3T1,P4T1, P1T2,P2T2,P3T2,P4T2, P1T3,P2T3,P3T3,P4T3;
    private static final int CM_SET_WINNER = 1;
    private static final int CM_SET_WINNER2 = 2;
    private static final int CM_SET_WINNER3 = 3;

    // Реализация функционала при создании страницы
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tor_com);

        // Привязка элементов к объектам и константам
        btnMain = (Button) findViewById(R.id.BtnBack);
        btnMain.setOnClickListener(this);

        BtnFinT =(Button) findViewById(R.id.BtnFinT);
        BtnFinT.setOnClickListener(this);
        BtnFinT.setEnabled(false);

        btnt1 = (Button) findViewById(R.id.btnt1);
        btnt1.setOnClickListener(this);
        btnt1.setText("Начать");

        btnt2 = (Button) findViewById(R.id.btnt2);
        btnt2.setOnClickListener(this);
        btnt2.setText("Не активно");
        btnt2.setEnabled(false);

        btnt3 = (Button) findViewById(R.id.btnt3);
        btnt3.setOnClickListener(this);
        btnt3.setText("Не активно");
        btnt3.setEnabled(false);

        lvTour1 = (GridView) findViewById(R.id.lvTour1);
        lvTour2 = (GridView) findViewById(R.id.lvTour2);
        lvTour3 = (GridView) findViewById(R.id.lvTour3);


        // Название турнира
        Intent intent1 = getIntent();
        ThisT = intent1.getStringExtra("TourComName");
        Log.d(LOG_TAG, "1" + ThisT);

        if (ThisT == null) {
            Intent intent = getIntent();
            ThisT = intent.getStringExtra("TNTNText");
            Log.d(LOG_TAG, "2" + ThisT);
        }
        TVShortInf = (TextView) findViewById(R.id.TVShortInf);
        TVShortInf.setText(ThisT);

        // Подключение к БД
        dbh = new DBHelper(this, DB_NAME, null, DB_VERSION);
        db = dbh.getWritableDatabase();

        //Получение ID Турнира по названию Турнира
        String selection = "TName=?";
        String selectionArgs[] = {ThisT};
        c = db.query(DB_TABLET, DB_COLUMNTID, selection, selectionArgs, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    idT = c.getInt(c.getColumnIndex("_id"));
                    LogidT = Integer.toString(idT);
                    Log.d(LOG_TAG, LogidT);
                } while (c.moveToNext());
            }
        }

        // Получаем и заполняем полную инфу о Турнире
        ca = db.query(DB_TABLET, null, selection, selectionArgs, null, null, null);
        startManagingCursor(ca);
        setAllInfo(ca);

        //Регистрация контекстного меню для списков с возможностью выбора победителя
        registerForContextMenu(lvTour1);
        registerForContextMenu(lvTour2);
        registerForContextMenu(lvTour3);

        // Собираем Массив игроков для первого Тура
        String pt1 = "idT =? AND idT1=?";
        String pt1Args [] = {LogidT,"5"};
        cp00 = db.query("players as P inner join tournaments as T on P.idT = T._id",null,pt1,pt1Args,null,null,null);
        if (cp00 != null) {
            if (cp00.moveToFirst()) {
                do {
                    PT1 = cp00.getString(cp00.getColumnIndex("PName"));
                    Log.d(LOG_TAG, PT1);
                    PinTour1New.add(PT1);
                } while (cp00.moveToNext());
            }
        }
        cp00.close();

        // Инфа для проверки окончания первого тура
        ct1fin = db.query("players",new String[]{"_id","PName"},"idT=? AND WIN=?",new String[]{LogidT,"1"},null,null,null);
        if (ct1fin != null) {
            if (ct1fin.moveToFirst()) {
                do {
                    PT1fin = ct1fin.getString(ct1fin.getColumnIndex("PName"));
                    PinTour1Done.add(PT1fin);
                } while (ct1fin.moveToNext());
            }
        }

        // Инфа для проверки отображения сетки 2го тура
        ct2check = db.query("players",null,"idT=? AND idT2=?",new String[]{LogidT,"5"},null,null,null);
        if (ct2check != null) {
            if (ct2check.moveToFirst()) {
                do {
                    PT2Check = ct2check.getInt(ct2check.getColumnIndex("idT2"));
                    PinTour2Check.add(PT2Check);
                } while (ct2check.moveToNext());
            }
        }

        // Инфа для проверки окончания второго тура
        ct2fin = db.query("players",new String[]{"_id","PName"},"idT=? AND WIN=?",new String[]{LogidT,"2"},null,null,null);
        if (ct2fin != null) {
            if (ct2fin.moveToFirst()) {
                do {
                    PT1fin = ct2fin.getString(ct2fin.getColumnIndex("PName"));
                    PinTour2Done.add(PT1fin);
                } while (ct2fin.moveToNext());
            }
        }

        // Инфа для проверки отображения сетки 3го тура
        ct3check = db.query("players",null,"idT=? AND idT3=?",new String[]{LogidT,"5"},null,null,null);
        if (ct3check != null) {
            if (ct3check.moveToFirst()) {
                do {
                    PT3Check = ct3check.getInt(ct3check.getColumnIndex("idT3"));
                    PinTour3Check.add(PT3Check);
                } while (ct3check.moveToNext());
            }
        }

        // Инфа для проверки окончания третьего тура
        ct3fin = db.query("players",new String[]{"_id","PName"},"idT=? AND WIN=?",new String[]{LogidT,"3"},null,null,null);
        if (ct3fin != null) {
            if (ct3fin.moveToFirst()) {
                do {
                    PT1fin = ct3fin.getString(ct3fin.getColumnIndex("PName"));
                    PinTour3Done.add(PT1fin);
                } while (ct3fin.moveToNext());
            }
        }

        // Спискок 1го Тура при создании стр, когда пары уже известны
        if (PinTour1New.size() != 8) {
            if (PinTour1Done.size() == 4) {
                btnt1.setText("Завершен");
                btnt1.setEnabled(false);
                btnt2.setText("Начать");
                btnt2.setEnabled(true);
            } else {
                btnt1.setText("Завершить");
            }
            ct1 = db.query("players",new String[]{"_id","PName"},"idT=?",new String[]{LogidT},null,null,"idT1");
            startManagingCursor(ct1);
            setElvTour1(ct1);
        }

        // Спискок 2го Тура при создании стр, когда пары уже известны
        if (PinTour21New.size() != 4) {
            if (PinTour2Done.size() == 2) {
                btnt2.setText("Завершен");
                btnt2.setEnabled(false);
                btnt3.setText("Начать");
                btnt3.setEnabled(true);
            } else {
                btnt2.setText("Завершить");
            }
            if (PinTour2Check.size() != 8) {
                ct2 = db.query("players", new String[]{"_id", "PName"}, "idT=?", new String[]{LogidT}, null, null, "idT2");
                startManagingCursor(ct2);
                setElvTour2(ct2);
            }
        }
        // Спискок 3го Тура при создании стр, когда пары уже известны
        if (PinTour3New.size() != 4) {
            if (PinTour3Done.size() == 1) {
                btnt1.setText("Завершен");
                btnt3.setText("Завершен");
                btnt3.setEnabled(false);
                btnt2.setText("Завершен");
                btnt1.setEnabled(false);
                BtnFinT.setEnabled(true);
            } else {
                btnt3.setText("Завершить");
            }
            if (PinTour3Check.size() != 8) {
                ct3 = db.query("players", new String[]{"_id", "PName"}, "idT=?", new String[]{LogidT}, null, null, "idT3");
                startManagingCursor(ct3);
                setElvTour3(ct3);
            }
        }

    }

    // Обработчик нажатий на кнопки
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.BtnBack:
                // На список всех турниров
                Intent intent = new Intent(this, all_tor.class);
                startActivity(intent);
                break;
            case R.id.BtnFinT:
                // Завершить турнир
                ContentValues cvs = new ContentValues();
                cvs.put("idS", 3);
                db.update("tournaments", cvs, "TName=?",
                        new String[] { ThisT });


                Intent intent1 = new Intent(this, tor_fin.class);
                intent1.putExtra("TourComName", ThisT);
                startActivity(intent1);
                break;
            case R.id.btnt1:
                // Рандомное распределение игроков из массива по парам
                if (PinTour1New.size() == 8) {
                    String pt1log;
                    Log.d(LOG_TAG, pt1log = PinTour1New.toString());
                    Collections.shuffle(PinTour1New);
                    Log.d(LOG_TAG, pt1log = PinTour1New.toString());
                    P1T1 = new String[2];
                    P1T1[0] = PinTour1New.get(0);
                    P1T1[1] = PinTour1New.get(1);
                    Log.d(LOG_TAG,pt1log = Arrays.toString(P1T1));

                    P2T1 = new String[2];
                    P2T1[0] = PinTour1New.get(2);
                    P2T1[1] = PinTour1New.get(3);
                    Log.d(LOG_TAG,pt1log = Arrays.toString(P2T1));

                    P3T1 = new String[2];
                    P3T1[0] = PinTour1New.get(4);
                    P3T1[1] = PinTour1New.get(5);
                    Log.d(LOG_TAG,pt1log = Arrays.toString(P3T1));

                    P4T1 = new String[2];
                    P4T1[0] = PinTour1New.get(6);
                    P4T1[1] = PinTour1New.get(7);
                    Log.d(LOG_TAG,pt1log = Arrays.toString(P4T1));

                    btnt1.setText("Завершить");

                    // Изменение статуса пар после распределения
                    ContentValues Pair1 = new ContentValues();
                    Pair1.put("idT1",1);
                    db.update("players", Pair1, "idT=? AND PName=? OR idT=? AND PName=?",
                            new String[] {new String(Integer.toString(idT)), P1T1[0], new String(Integer.toString(idT)), P1T1[1]});

                    ContentValues Pair2 = new ContentValues();
                    Pair2.put("idT1",2);
                    db.update("players", Pair2, "idT=? AND PName=? OR idT=? AND PName=?",
                            new String[] {new String(Integer.toString(idT)), P2T1[0], new String(Integer.toString(idT)), P2T1[1]});

                    ContentValues Pair3 = new ContentValues();
                    Pair3.put("idT1",3);
                    db.update("players", Pair3, "idT=? AND PName=? OR idT=? AND PName=?",
                            new String[] {new String(Integer.toString(idT)), P3T1[0], new String(Integer.toString(idT)), P3T1[1]});

                    ContentValues Pair4 = new ContentValues();
                    Pair4.put("idT1",4);
                    db.update("players", Pair4, "idT=? AND PName=? OR idT=? AND PName=?",
                            new String[] {new String(Integer.toString(idT)), P4T1[0], new String(Integer.toString(idT)), P4T1[1]});

                    // Создания списка Тур 1 при первичном распределении
                    ct1 = db.query("players",new String[]{"_id","PName"},"idT=?",new String[]{LogidT},null,null,"idT1");
                    startManagingCursor(ct1);
                    setElvTour1(ct1);

                }
                // Завершение тура
                if (PinTour1New.size() != 8) {
                    btnt1.setText("Завершен");
                    btnt1.setEnabled(false);

                    // Добавление поражения к игрокам не имеющих победы
                    ContentValues cvlose = new ContentValues();
                    cvlose.put("LOSE",1);
                    db.update("players",cvlose,"idT=? AND WIN=?" ,new String[]{new String(Integer.toString(idT)),"0"});
                    btnt2.setText("Начать");
                    btnt2.setEnabled(true);
                }
                // Очистка первичного масссива игроков для реализации проверки
                PinTour1New.clear();
                break;
            case R.id.btnt2:
                // Рандомное распределение игроков из массивов WIN и LOSE по парам
                if (PinTour2Check.size() == 8) {

                    // Собираем Массив игроков для второго Тура по WIN = 1
                    String pt2 = "idT =? AND idT2=? AND WIN=?";
                    String pt2Args [] = {LogidT,"5","1"};
                    cp02 = db.query("players",null,pt2,pt2Args,null,null,null);
                    if (cp02 != null) {
                        if (cp02.moveToFirst()) {
                            do {
                                PT21 = cp02.getString(cp02.getColumnIndex("PName"));
                                Log.d(LOG_TAG, PT21);
                                PinTour21New.add(PT21);
                            } while (cp02.moveToNext());
                        }
                    }
                    cp02.close();

                    // Собираем Массив игроков для второго Тура по LOSE = 1
                    String pt3 = "idT =? AND idT2=? AND LOSE=?";
                    String pt3Args [] = {LogidT,"5","1"};
                    cp022 = db.query("players",null,pt3,pt3Args,null,null,null);
                    if (cp022 != null) {
                        if (cp022.moveToFirst()) {
                            do {
                                PT22 = cp022.getString(cp022.getColumnIndex("PName"));
                                Log.d(LOG_TAG, PT22);
                                PinTour22New.add(PT22);
                            } while (cp022.moveToNext());
                        }
                    }
                    cp022.close();

                    String pt1log;
                    Log.d(LOG_TAG, pt1log = PinTour21New.toString());
                    Log.d(LOG_TAG, pt1log = PinTour22New.toString());
                    Collections.shuffle(PinTour21New);
                    Collections.shuffle(PinTour22New);
                    Log.d(LOG_TAG, pt1log = PinTour21New.toString());
                    Log.d(LOG_TAG, pt1log = PinTour22New.toString());

                    P1T2 = new String[2];
                    P1T2[0] = PinTour21New.get(0);
                    P1T2[1] = PinTour21New.get(1);
                    Log.d(LOG_TAG, pt1log = Arrays.toString(P1T2));

                    P2T2 = new String[2];
                    P2T2[0] = PinTour21New.get(2);
                    P2T2[1] = PinTour21New.get(3);
                    Log.d(LOG_TAG, pt1log = Arrays.toString(P2T2));

                    P3T2 = new String[2];
                    P3T2[0] = PinTour22New.get(0);
                    P3T2[1] = PinTour22New.get(1);
                    Log.d(LOG_TAG, pt1log = Arrays.toString(P3T2));

                    P4T2 = new String[2];
                    P4T2[0] = PinTour22New.get(2);
                    P4T2[1] = PinTour22New.get(3);
                    Log.d(LOG_TAG, pt1log = Arrays.toString(P4T2));

                    btnt2.setText("Завершить");

                    // Изменение статуса пар после распределения
                    ContentValues Pair21 = new ContentValues();
                    Pair21.put("idT2", 1);
                    db.update("players", Pair21, "idT=? AND PName=? OR idT=? AND PName=?",
                            new String[]{new String(Integer.toString(idT)), P1T2[0], new String(Integer.toString(idT)), P1T2[1]});

                    ContentValues Pair22 = new ContentValues();
                    Pair22.put("idT2", 2);
                    db.update("players", Pair22, "idT=? AND PName=? OR idT=? AND PName=?",
                            new String[]{new String(Integer.toString(idT)), P2T2[0], new String(Integer.toString(idT)), P2T2[1]});

                    ContentValues Pair23 = new ContentValues();
                    Pair23.put("idT2", 3);
                    db.update("players", Pair23, "idT=? AND PName=? OR idT=? AND PName=?",
                            new String[]{new String(Integer.toString(idT)), P3T2[0], new String(Integer.toString(idT)), P3T2[1]});

                    ContentValues Pair24 = new ContentValues();
                    Pair24.put("idT2", 4);
                    db.update("players", Pair24, "idT=? AND PName=? OR idT=? AND PName=?",
                            new String[]{new String(Integer.toString(idT)), P4T2[0], new String(Integer.toString(idT)), P4T2[1]});

                    // Создания списка Тур 2 при первичном распределении
                    ct2 = db.query("players", new String[]{"_id", "PName"}, "idT=?", new String[]{LogidT}, null, null, "idT2");
                    startManagingCursor(ct2);
                    setElvTour2(ct2);
                }
                if (PinTour21New.size() == 4) {

                    // Собираем Массив игроков для второго Тура по WIN = 1
                    String pt2 = "idT =? AND idT2=? AND WIN=?";
                    String pt2Args [] = {LogidT,"5","1"};
                    cp02 = db.query("players",null,pt2,pt2Args,null,null,null);
                    if (cp02 != null) {
                        if (cp02.moveToFirst()) {
                            do {
                                PT21 = cp02.getString(cp02.getColumnIndex("PName"));
                                Log.d(LOG_TAG, PT21);
                                PinTour21New.add(PT21);
                            } while (cp02.moveToNext());
                        }
                    }
                    cp02.close();

                    // Собираем Массив игроков для второго Тура по LOSE = 1
                    String pt3 = "idT =? AND idT2=? AND LOSE=?";
                    String pt3Args [] = {LogidT,"5","1"};
                    cp022 = db.query("players",null,pt3,pt3Args,null,null,null);
                    if (cp022 != null) {
                        if (cp022.moveToFirst()) {
                            do {
                                PT22 = cp022.getString(cp022.getColumnIndex("PName"));
                                Log.d(LOG_TAG, PT22);
                                PinTour22New.add(PT22);
                            } while (cp022.moveToNext());
                        }
                    }
                    cp022.close();

                    String pt1log;
                    Log.d(LOG_TAG, pt1log = PinTour21New.toString());
                    Log.d(LOG_TAG, pt1log = PinTour22New.toString());
                    Collections.shuffle(PinTour21New);
                    Collections.shuffle(PinTour22New);
                    Log.d(LOG_TAG, pt1log = PinTour21New.toString());
                    Log.d(LOG_TAG, pt1log = PinTour22New.toString());

                    P1T2 = new String[2];
                    P1T2[0] = PinTour21New.get(0);
                    P1T2[1] = PinTour21New.get(1);
                    Log.d(LOG_TAG, pt1log = Arrays.toString(P1T2));

                    P2T2 = new String[2];
                    P2T2[0] = PinTour21New.get(2);
                    P2T2[1] = PinTour21New.get(3);
                    Log.d(LOG_TAG, pt1log = Arrays.toString(P2T2));

                    P3T2 = new String[2];
                    P3T2[0] = PinTour22New.get(0);
                    P3T2[1] = PinTour22New.get(1);
                    Log.d(LOG_TAG, pt1log = Arrays.toString(P3T2));

                    P4T2 = new String[2];
                    P4T2[0] = PinTour22New.get(2);
                    P4T2[1] = PinTour22New.get(3);
                    Log.d(LOG_TAG, pt1log = Arrays.toString(P4T2));

                    btnt2.setText("Завершить");

                    // Изменение статуса пар после распределения
                    ContentValues Pair21 = new ContentValues();
                    Pair21.put("idT2", 1);
                    db.update("players", Pair21, "idT=? AND PName=? OR idT=? AND PName=?",
                            new String[]{new String(Integer.toString(idT)), P1T2[0], new String(Integer.toString(idT)), P1T2[1]});

                    ContentValues Pair22 = new ContentValues();
                    Pair22.put("idT2", 2);
                    db.update("players", Pair22, "idT=? AND PName=? OR idT=? AND PName=?",
                            new String[]{new String(Integer.toString(idT)), P2T2[0], new String(Integer.toString(idT)), P2T2[1]});

                    ContentValues Pair23 = new ContentValues();
                    Pair23.put("idT2", 3);
                    db.update("players", Pair23, "idT=? AND PName=? OR idT=? AND PName=?",
                            new String[]{new String(Integer.toString(idT)), P3T2[0], new String(Integer.toString(idT)), P3T2[1]});

                    ContentValues Pair24 = new ContentValues();
                    Pair24.put("idT2", 4);
                    db.update("players", Pair24, "idT=? AND PName=? OR idT=? AND PName=?",
                            new String[]{new String(Integer.toString(idT)), P4T2[0], new String(Integer.toString(idT)), P4T2[1]});

                    // Создания списка Тур 2 при первичном распределении
                    ct2 = db.query("players", new String[]{"_id", "PName"}, "idT=?", new String[]{LogidT}, null, null, "idT2");
                    startManagingCursor(ct2);
                    setElvTour2(ct2);

                }
                    // Завершение тура
                if (PinTour2Check.size() != 8) {
                    btnt2.setText("Завершен");
                    btnt2.setEnabled(false);

                    // Добавление поражения к игрокам при условиях 0 Побед, 1 победа и 0 поражений
                    ContentValues cvlose2 = new ContentValues();
                    cvlose2.put("LOSE",2);
                    db.update("players",cvlose2,"idT=? AND WIN=?" ,new String[]{new String(Integer.toString(idT)),"0"});
                    cvlose2.clear();
                    cvlose2.put("LOSE",1);
                    db.update("players",cvlose2,"idT=? AND WIN=? AND LOSE=?",new String[]{new String(Integer.toString(idT)),"1","0"});
                    cvlose2.clear();
                    btnt3.setText("Начать");
                    btnt3.setEnabled(true);
                }
                // Очистка вторичного масссива игроков для реализации проверки
                PinTour21New.clear();
                PinTour22New.clear();
                PinTour2Check.clear();
                break;
            case R.id.btnt3:
                // Рандомное распределение игроков из массива WIN = 1 LOSE = 1 по парам
                if (PinTour3Check.size() == 8) {
                    // Собираем Массив игроков для третьего Тура по WIN = 1, LOSE = 1
                    String pt4 = "idT =? AND idT3=? AND WIN=? AND LOSE=?";
                    String pt4Args [] = {LogidT,"5","1","1"};
                    cp03 = db.query("players",null,pt4,pt4Args,null,null,null);
                    if (cp03 != null) {
                        if (cp03.moveToFirst()) {
                            do {
                                PT3 = cp03.getString(cp03.getColumnIndex("PName"));
                                Log.d(LOG_TAG, PT3);
                                PinTour3New.add(PT3);
                            } while (cp03.moveToNext());
                        }
                    }
                    cp03.close();

                    String pt1log;
                    Log.d(LOG_TAG, pt1log = PinTour3New.toString());
                    Collections.shuffle(PinTour21New);
                    Log.d(LOG_TAG, pt1log = PinTour3New.toString());

                    P2T3 = new String[2];
                    P2T3[0] = PinTour3New.get(0);
                    P2T3[1] = PinTour3New.get(1);
                    Log.d(LOG_TAG, pt1log = Arrays.toString(P2T3));

                    P3T3 = new String[2];
                    P3T3[0] = PinTour3New.get(2);
                    P3T3[1] = PinTour3New.get(3);
                    Log.d(LOG_TAG, pt1log = Arrays.toString(P3T3));

                    btnt3.setText("Завершить");

                    // Изменение статуса пар после распределения
                    ContentValues Pair31 = new ContentValues();
                    Pair31.put("idT3", 1);
                    db.update("players", Pair31, "idT=? AND WIN=?",
                            new String[]{new String(Integer.toString(idT)), "2"});

                    ContentValues Pair32 = new ContentValues();
                    Pair32.put("idT3", 2);
                    db.update("players", Pair32, "idT=? AND PName=? OR idT=? AND PName=?",
                            new String[]{new String(Integer.toString(idT)), P2T3[0], new String(Integer.toString(idT)), P2T3[1]});

                    ContentValues Pair33 = new ContentValues();
                    Pair33.put("idT3", 3);
                    db.update("players", Pair33, "idT=? AND PName=? OR idT=? AND PName=?",
                            new String[]{new String(Integer.toString(idT)), P3T3[0], new String(Integer.toString(idT)), P3T3[1]});

                    ContentValues Pair34 = new ContentValues();
                    Pair34.put("idT3", 4);
                    db.update("players", Pair34, "idT=? AND LOSE=?",
                            new String[]{new String(Integer.toString(idT)), "2"});

                    // Создания списка Тур 3 при первичном распределении
                    ct3 = db.query("players", new String[]{"_id", "PName"}, "idT=?", new String[]{LogidT}, null, null, "idT3");
                    startManagingCursor(ct3);
                    setElvTour3(ct3);
                }
                if (PinTour3New.size() == 4) {

                    // Собираем Массив игроков для третьего Тура по WIN = 1, LOSE = 1
                    String pt4 = "idT =? AND idT3=? AND WIN=? AND LOSE=?";
                    String pt4Args [] = {LogidT,"5","1","1"};
                    cp03 = db.query("players",null,pt4,pt4Args,null,null,null);
                    if (cp03 != null) {
                        if (cp03.moveToFirst()) {
                            do {
                                PT3 = cp03.getString(cp03.getColumnIndex("PName"));
                                Log.d(LOG_TAG, PT3);
                                PinTour3New.add(PT3);
                            } while (cp03.moveToNext());
                        }
                    }
                    cp03.close();

                    String pt1log;
                    Log.d(LOG_TAG, pt1log = PinTour3New.toString());
                    Collections.shuffle(PinTour21New);
                    Log.d(LOG_TAG, pt1log = PinTour3New.toString());

                    P2T3 = new String[2];
                    P2T3[0] = PinTour3New.get(0);
                    P2T3[1] = PinTour3New.get(1);
                    Log.d(LOG_TAG, pt1log = Arrays.toString(P2T3));

                    P3T3 = new String[2];
                    P3T3[0] = PinTour3New.get(2);
                    P3T3[1] = PinTour3New.get(3);
                    Log.d(LOG_TAG, pt1log = Arrays.toString(P3T3));

                    btnt3.setText("Завершить");

                    // Изменение статуса пар после распределения
                    ContentValues Pair31 = new ContentValues();
                    Pair31.put("idT3", 1);
                    db.update("players", Pair31, "idT=? AND WIN=?",
                            new String[]{new String(Integer.toString(idT)), "2"});

                    ContentValues Pair32 = new ContentValues();
                    Pair32.put("idT3", 2);
                    db.update("players", Pair32, "idT=? AND PName=? OR idT=? AND PName=?",
                            new String[]{new String(Integer.toString(idT)), P2T3[0], new String(Integer.toString(idT)), P2T3[1]});

                    ContentValues Pair33 = new ContentValues();
                    Pair33.put("idT3", 3);
                    db.update("players", Pair33, "idT=? AND PName=? OR idT=? AND PName=?",
                            new String[]{new String(Integer.toString(idT)), P3T3[0], new String(Integer.toString(idT)), P3T3[1]});

                    ContentValues Pair34 = new ContentValues();
                    Pair34.put("idT3", 4);
                    db.update("players", Pair34, "idT=? AND LOSE=?",
                            new String[]{new String(Integer.toString(idT)), "2"});

                    // Создания списка Тур 3 при первичном распределении
                    ct3 = db.query("players", new String[]{"_id", "PName"}, "idT=?", new String[]{LogidT}, null, null, "idT3");
                    startManagingCursor(ct3);
                    setElvTour3(ct3);

                }
                // Завершение тура
                if (PinTour3Check.size() != 8) {
                    btnt3.setText("Завершен");
                    btnt3.setEnabled(false);
                    BtnFinT.setEnabled(true);

                    // Добавление поражения к игрокам при условиях 2 Победы 0 поражений, 0 побед и 2 поражений, 1 победа и 1 поражение
                    ContentValues cvlose3 = new ContentValues();
                    cvlose3.put("LOSE",1);
                    db.update("players",cvlose3,"idT=? AND WIN=? AND LOSE=?" ,new String[]{new String(Integer.toString(idT)),"2","0"});
                    cvlose3.clear();
                    cvlose3.put("LOSE",3);
                    db.update("players",cvlose3,"idT=? AND WIN=? AND LOSE=?",new String[]{new String(Integer.toString(idT)),"0","2"});
                    cvlose3.clear();
                    cvlose3.put("LOSE",2);
                    db.update("players",cvlose3,"idT=? AND WIN=? AND LOSE=?" ,new String[]{new String(Integer.toString(idT)),"1","1"});
                    cvlose3.clear();
                }
                // Очистка третьего масссива игроков для реализации проверки
                PinTour3New.clear();
                PinTour3Check.clear();
                break;
            default:
                break;
        }
    }

    // Метод на заполнение списка 1го Тура
    public void setElvTour1(Cursor ct1) {
        String[] test = {"PName"};
        int[] To = {R.id.PP1};
        adapterT1 = new SimpleCursorAdapter(this, R.layout.el_tour_item, ct1, test, To);
        lvTour1.setAdapter(adapterT1);
    }
    // Метод на заполнение списка 1го Тура
    public void setElvTour2(Cursor ct2) {
        String[] test = {"PName"};
        int[] To = {R.id.PP1};
        adapterT2 = new SimpleCursorAdapter(this, R.layout.el_tour_item, ct2, test, To);
        lvTour2.setAdapter(adapterT2);
    }
    // Метод на заполнение списка 1го Тура
    public void setElvTour3(Cursor ct3) {
        String[] test = {"PName"};
        int[] To = {R.id.PP1};
        adapterT3 = new SimpleCursorAdapter(this, R.layout.el_tour_item, ct3, test, To);
        lvTour3.setAdapter(adapterT3);
    }
    // Создание контекстного меню
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
                menu.add(0, CM_SET_WINNER, 0, "Выбать победителем");
        }

    // Добавление победы выбранному игроку
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_SET_WINNER) {

                // получаем из пункта контекстного меню данные по пункту списка
                AdapterContextMenuInfo acmi2 = (AdapterContextMenuInfo) item.getMenuInfo();
                // извлекаем id записи
                ContentValues cwin2 = new ContentValues();
                int win2 = 0;
                Cursor cvwin2 = db.query("players",new String[]{"_id","WIN"},"_id=" + acmi2.id,null,null,null,null);
                if (cvwin2 != null) {
                    if (cvwin2.moveToFirst()) {
                        do {
                            win2 = cvwin2.getInt(cvwin2.getColumnIndex("WIN"));
                        } while (cvwin2.moveToNext());
                    }
                }
                cwin2.put("WIN",win2+1);
                db.update("players",cwin2,"_id=" + acmi2.id,null);
                return true;
        }
        return super.onContextItemSelected(item);
    }
    // Метод для получения всей информации о турнире
    public void setAllInfo(Cursor ca) {
        String[] from = new String[] {"Place","Date","Time","Tel","Email"};
        int[] to = new int[] {R.id.TVAIPlace,R.id.TVAIDate, R.id.TVAITime, R.id.TVAITel, R.id.TVAIEmail};
        adapterAllInfo = new SimpleCursorAdapter(this, R.layout.tor_reg_allinfo, ca, from, to);
        LVLongInf = (ListView) findViewById(R.id.LVLongInf);
        LVLongInf.setAdapter(adapterAllInfo);
    }

    // Отключение от БД при закрытие Окна
    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        dbh.close();
    }
    // Создание класса для БД + обязательные, но неиспользуемые методы создания и апгрейда БД
    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, DB_NAME, factory, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
