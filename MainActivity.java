package com.syriapokervip;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.*;
import android.view.*;
import java.util.*;

public class MainActivity extends Activity {
    @Override public void onCreate(Bundle b) { super.onCreate(b); setContentView(new GameView(this)); }
}

class GameView extends View {
    private final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG), stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random rng = new Random();
    private final ArrayList<Card> deck = new ArrayList<>();
    private final ArrayList<Card> hole = new ArrayList<>();
    private final ArrayList<Card> community = new ArrayList<>();
    private final ArrayList<Room> rooms = new ArrayList<>();
    private final ArrayList<Player> players = new ArrayList<>();
    private final float d;
    private final RectF r = new RectF();
    private int screen = 0, roomIndex = 0, street = 0, dealer = 0, turn = 0;
    private long coins = 25680, pot = 0;
    private int currentBet = 1000, playerBet = 0;
    private String status = "دورك الآن";
    private boolean fbClaimed = false, showdown = false;
    private final int gold=Color.rgb(229,178,58), green=Color.rgb(7,72,43), dark=Color.rgb(3,13,10), panel=Color.rgb(8,25,19), white=Color.rgb(240,244,238);

    GameView(Context c) {
        super(c); d=getResources().getDisplayMetrics().density;
        p.setTypeface(Typeface.create("sans", Typeface.BOLD)); stroke.setStyle(Paint.Style.STROKE); stroke.setStrokeWidth(2*d);
        rooms.add(new Room("غرفة المبتدئين", 1000, 2)); rooms.add(new Room("الغرفة الذهبية", 5000, 4));
        rooms.add(new Room("VIP 1", 10000, 6)); rooms.add(new Room("VIP 2", 25000, 6));
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
    float X(float x){return x*d;}
    void txt(Canvas c,String s,float x,float y,float size,int col,Paint.Align a){p.setTextSize(X(size));p.setColor(col);p.setTextAlign(a);p.setStyle(Paint.Style.FILL);c.drawText(s,X(x),X(y),p);}
    void box(Canvas c,float l,float t,float rr,float bb,int col,float rad){p.setColor(col);p.setStyle(Paint.Style.FILL);r.set(X(l),X(t),X(rr),X(bb));c.drawRoundRect(r,X(rad),X(rad),p);}
    void outline(Canvas c,float l,float t,float rr,float bb,int col,float rad){stroke.setColor(col);r.set(X(l),X(t),X(rr),X(bb));c.drawRoundRect(r,X(rad),X(rad),stroke);}
    String money(long n){return String.format(Locale.US,"%,d",Math.max(0,n));}

    @Override protected void onDraw(Canvas c){super.onDraw(c);c.drawColor(dark); if(screen==0)lobby(c);else if(screen==1)rooms(c);else if(screen==2)table(c);else if(screen==3)profile(c);else if(screen==4)shop(c);else rewards(c);}
    void header(Canvas c){box(c,0,0,360,66,Color.rgb(4,20,14),0);txt(c,"بوكر سوريا VIP",180,31,22,gold,Paint.Align.CENTER);txt(c,"♛ VIP 1",320,29,14,white,Paint.Align.CENTER);txt(c,"🪙 "+money(coins),80,52,14,white,Paint.Align.CENTER);}
    void nav(Canvas c){box(c,0,710,360,800,Color.rgb(3,18,13),0);String[] a={"الرئيسية","الغرف","المتجر","المكافآت","الملف"};int[] sc={0,1,4,5,3};for(int i=0;i<5;i++)txt(c,a[i],36+i*72,750,12,sc[i]==screen?gold:white,Paint.Align.CENTER);}
    void button(Canvas c,float l,float t,float rr,float bb,String s,int col){box(c,l,t,rr,bb,col,14);outline(c,l,t,rr,bb,gold,14);txt(c,s,(l+rr)/2,t+38,15,white,Paint.Align.CENTER);}
    void card(Canvas c,float l,float t,float rr,float bb,String icon,String title,String sub,int col){box(c,l,t,rr,bb,col,18);txt(c,icon,(l+rr)/2,t+43,28,white,Paint.Align.CENTER);txt(c,title,(l+rr)/2,t+79,18,white,Paint.Align.CENTER);txt(c,sub,(l+rr)/2,t+105,12,gold,Paint.Align.CENTER);outline(c,l,t,rr,bb,gold,18);}

    void lobby(Canvas c){header(c);box(c,0,66,360,710,Color.rgb(5,34,22),0);txt(c,"★  ★  ★",180,105,18,Color.RED,Paint.Align.CENTER);txt(c,"مرحباً بك في بوكر سوريا VIP",180,137,20,white,Paint.Align.CENTER);card(c,18,160,174,285,"♠ ♣","غرف اللعب","2–6 لاعبين",green);card(c,186,160,342,285,"♛","البطولات","جوائز كبيرة",Color.rgb(27,59,85));card(c,18,300,174,425,"🎁","المكافآت اليومية","احصل على جائزتك",Color.rgb(82,43,18));card(c,186,300,342,425,"▶","مشاهدة إعلان","+10,000,000",Color.rgb(9,88,53));box(c,18,450,342,545,panel,18);txt(c,"مكافأة تسجيل Facebook",180,480,17,white,Paint.Align.CENTER);txt(c,"10,000,000 🪙",180,516,27,gold,Paint.Align.CENTER);button(c,18,570,342,630,"ادخل إلى غرف اللعب",green);button(c,18,640,342,690,"بدء اللعب",Color.rgb(20,77,150));nav(c);}

    void rooms(Canvas c){header(c);txt(c,"اختيار غرفة اللعب",180,100,23,gold,Paint.Align.CENTER);for(int i=0;i<rooms.size();i++){Room rm=rooms.get(i);float y=125+i*112;box(c,15,y,345,y+92,panel,16);txt(c,rm.name,55,y+30,17,white,Paint.Align.LEFT);txt(c,rm.occupied+" / "+rm.max+" لاعبين",55,y+60,12,Color.LTGRAY,Paint.Align.LEFT);txt(c,"دخول "+money(rm.blind)+" 🪙",250,y+32,13,gold,Paint.Align.CENTER);button(c,265,y+50,330,y+82,"دخول",green);}nav(c);}

    void startRoom(int idx){roomIndex=idx;Room rm=rooms.get(idx);players.clear();players.add(new Player("أنت", coins, true));String[] names={"أحمد","رامي","سامي","زين","نور"};int count=Math.min(rm.max-1,5);for(int i=0;i<count;i++)players.add(new Player(names[i],70000,false));rm.occupied=players.size();startHand();}
    void startHand(){deck.clear();hole.clear();community.clear();pot=0;currentBet=rooms.get(roomIndex).blind;playerBet=0;street=0;showdown=false;status="دورك الآن";dealer=(dealer+1)%players.size();turn=(dealer+1)%players.size();
        String[] suits={"♠","♥","♦","♣"};String[] ranks={"2","3","4","5","6","7","8","9","10","J","Q","K","A"};for(String s:suits)for(String q:ranks)deck.add(new Card(q,s));Collections.shuffle(deck,rng);
        hole.add(draw());hole.add(draw());for(Player pl:players){pl.hand.clear();pl.bet=0;pl.folded=false;if(!pl.me){pl.hand.add(draw());pl.hand.add(draw());}}
        for(Player pl:players){long blind=rooms.get(roomIndex).blind; if(!pl.me){pl.bet=blind;pot+=blind;}else{pl.bet=blind;playerBet=blind;pot+=blind;coins=Math.max(0,coins-blind);}}
        status="دورك الآن — الرهان "+money(currentBet);
    }
    Card draw(){return deck.remove(deck.size()-1);}
    int rank(Card c){if(c.rank.equals("A"))return 14;if(c.rank.equals("K"))return 13;if(c.rank.equals("Q"))return 12;if(c.rank.equals("J"))return 11;return Integer.parseInt(c.rank);}
    int red(Card c){return (c.suit.equals("♥")||c.suit.equals("♦"))?Color.rgb(190,35,35):Color.BLACK;}
    void playingCard(Canvas c,Card cd,float x,float y,float w,float h){box(c,x,y,x+w,y+h,Color.WHITE,6);txt(c,cd.rank+cd.suit,x+w/2,y+h*.64f,17,red(cd),Paint.Align.CENTER);}
    void backCard(Canvas c,float x,float y){box(c,x,y,x+42,y+62,Color.rgb(16,53,39),6);outline(c,x,y,x+42,y+62,gold,6);txt(c,"★",x+21,y+39,20,gold,Paint.Align.CENTER);}

    void table(Canvas c){header(c);Room rm=rooms.get(roomIndex);txt(c,rm.name+"  •  Texas Hold'em",180,88,16,white,Paint.Align.CENTER);box(c,15,105,345,650,Color.rgb(5,70,39),170);outline(c,15,105,345,650,gold,170);
        String[] names={"أحمد","رامي","سامي","زين","نور","أنت"};float[][] pos={{70,160},{290,160},{55,350},{305,350},{95,550},{265,550}};for(int i=0;i<6;i++){if(i>=players.size())continue;Player pl=players.get(i);box(c,pos[i][0]-35,pos[i][1]-25,pos[i][0]+35,pos[i][1]+25,pl.folded?Color.rgb(25,25,25):Color.rgb(2,22,16),20);txt(c,names[Math.min(i,names.length-1)],pos[i][0],pos[i][1]+5,12,pl.folded?Color.GRAY:white,Paint.Align.CENTER);txt(c,i==players.size()-1?money(coins):money(pl.stack),pos[i][0],pos[i][1]+21,10,gold,Paint.Align.CENTER);if(!pl.me&&!pl.folded&&street<3){backCard(c,pos[i][0]-25,pos[i][1]+30);backCard(c,pos[i][0]+22,pos[i][1]+30);}}
        txt(c,"البطاقات المشتركة",180,255,11,Color.LTGRAY,Paint.Align.CENTER);for(int i=0;i<5;i++){if(i<community.size())playingCard(c,community.get(i),88+i*38,265,34,55);else box(c,88+i*38,265,122+i*38,320,Color.rgb(12,40,30),5);}
        txt(c,"Pot: "+money(pot)+" 🪙",180,350,21,gold,Paint.Align.CENTER);txt(c,"أوراقك",180,385,11,Color.LTGRAY,Paint.Align.CENTER);if(hole.size()>0)playingCard(c,hole.get(0),128,395,48,68);if(hole.size()>1)playingCard(c,hole.get(1),184,395,48,68);
        txt(c,status,180,485,14,status.contains("فزت")?gold:white,Paint.Align.CENTER);
        button(c,20,510,112,565,"Fold",Color.rgb(150,30,30));button(c,118,510,242,565,showdown?"كشف":"Call "+money(currentBet),Color.rgb(20,77,150));button(c,248,510,340,565,"Raise",Color.rgb(153,101,12));
        button(c,75,585,285,635,showdown?"جولة جديدة":"التالي",green);nav(c);
    }

    void call(){if(showdown)return;long need=Math.max(0,currentBet-playerBet);if(need>coins){status="الرصيد غير كافٍ";return;}coins-=need;playerBet+=need;pot+=need;status="تمت المساواة — الدور التالي";advanceIfReady();}
    void raise(){if(showdown)return;int add=Math.max(rooms.get(roomIndex).blind,currentBet);long need=add; if(need>coins){status="الرصيد غير كافٍ";return;}currentBet+=add;coins-=need;playerBet+=need;pot+=need;status="رفعت الرهان إلى "+money(currentBet);advanceIfReady();}
    void fold(){if(showdown)return;showdown=true;status="انسحبت من الجولة — الخسارة "+money(pot/2);coins=Math.max(0,coins-pot/2);}
    void advanceIfReady(){if(street<3){int n=street==0?3:1;for(int i=0;i<n;i++)community.add(draw());street++;currentBet=Math.max(currentBet,rooms.get(roomIndex).blind);status=street==3?"آخر جولة — اختر Call أو كشف":"牌面已更新 — دورك الآن";}else{showdown();}}
    void showdown(){if(showdown)return;showdown=true;ArrayList<Card> mine=new ArrayList<>(hole);mine.addAll(community);long my=bestScore(mine);long best=my;String winner="أنت";for(Player pl:players){if(pl.me||pl.folded)continue;ArrayList<Card> h=new ArrayList<>(pl.hand);h.addAll(community);long sc=bestScore(h);if(sc>best){best=sc;winner=pl.name;}}if(winner.equals("أنت")){coins+=pot;status="أنت فزت! +"+money(pot)+" 🪙";}else{status=winner+" فاز بالجولة";}}
    long bestScore(ArrayList<Card> cs){
        int[] cnt=new int[15];
        HashMap<String,Integer> suitCount=new HashMap<>();
        HashMap<String,ArrayList<Integer>> bySuit=new HashMap<>();
        for(Card cd:cs){int v=rank(cd);cnt[v]++;if(!bySuit.containsKey(cd.suit))bySuit.put(cd.suit,new ArrayList<Integer>());bySuit.get(cd.suit).add(v);}
        // Straight flush
        long sf=straightFlush(bySuit); if(sf>0)return 800000+sf;
        int four=0,triple=0,pair=0;
        for(int v=14;v>=2;v--){if(cnt[v]>=4&&four==0)four=v;if(cnt[v]>=3&&triple==0)triple=v;if(cnt[v]>=2&&pair==0)pair=v;}
        if(four>0)return 700000+four;
        int secondTriple=0;for(int v=14;v>=2;v--)if(v!=triple&&cnt[v]>=3){secondTriple=v;break;}
        if(triple>0&&(secondTriple>0||pair>0))return 600000+triple;
        long flush=0;for(ArrayList<Integer> vals:bySuit.values())if(vals.size()>=5){Collections.sort(vals,Collections.reverseOrder());flush=vals.get(0);break;}if(flush>0)return 500000+flush;
        int straight=straightHigh(cnt);if(straight>0)return 400000+straight;
        if(triple>0)return 300000+triple;
        if(pair>0){int second=0;for(int v=14;v>=2;v--)if(v!=pair&&cnt[v]>=2){second=v;break;}if(second>0)return 200000+pair*100+second;return 200000+pair;}
        for(int v=14;v>=2;v--)if(cnt[v]>0)return 100000+v;
        return 0;
    }
    int straightHigh(int[] cnt){for(int v=14;v>=5;v--)if(cnt[v]>0&&cnt[v-1]>0&&cnt[v-2]>0&&cnt[v-3]>0&&cnt[v-4]>0)return v;if(cnt[14]>0&&cnt[2]>0&&cnt[3]>0&&cnt[4]>0&&cnt[5]>0)return 5;return 0;}
    long straightFlush(HashMap<String,ArrayList<Integer>> suits){long best=0;for(ArrayList<Integer> vals:suits.values())if(vals.size()>=5){int[] c=new int[15];for(int v:vals)c[v]=1;best=Math.max(best,straightHigh(c));}return best;}


    void profile(Canvas c){header(c);txt(c,"الملف الشخصي",180,105,24,gold,Paint.Align.CENTER);box(c,18,135,342,330,panel,20);txt(c,"👤",80,220,50,white,Paint.Align.CENTER);txt(c,"Player_12345",210,190,20,white,Paint.Align.CENTER);txt(c,"ID: 1000123",210,220,13,Color.LTGRAY,Paint.Align.CENTER);txt(c,"VIP 1",210,255,16,gold,Paint.Align.CENTER);txt(c,"الرصيد: "+money(coins)+" 🪙",180,300,15,white,Paint.Align.CENTER);nav(c);}
    void shop(Canvas c){header(c);txt(c,"المتجر",180,100,24,gold,Paint.Align.CENTER);String[] vals={"100,000","250,000","500,000","1,000,000"};for(int i=0;i<4;i++){float l=12+i*87;box(c,l,145,l+80,300,panel,14);txt(c,"🎟",l+40,190,27,white,Paint.Align.CENTER);txt(c,vals[i],l+40,230,14,gold,Paint.Align.CENTER);button(c,l+7,250,l+73,285,"شراء",green);}box(c,18,330,342,460,panel,18);txt(c,"مكافأة مشاهدة الفيديو",180,365,18,white,Paint.Align.CENTER);txt(c,"10,000,000 🪙 لكل فيديو",180,405,22,gold,Paint.Align.CENTER);button(c,80,425,280,455,"مشاهدة الآن",green);nav(c);}
    void rewards(Canvas c){header(c);txt(c,"المكافآت",180,100,24,gold,Paint.Align.CENTER);box(c,18,130,342,285,panel,18);txt(c,"مكافأة التسجيل",180,165,18,white,Paint.Align.CENTER);txt(c,fbClaimed?"تم الاستلام":"10,000,000 🪙",180,205,24,gold,Paint.Align.CENTER);button(c,75,225,285,265,fbClaimed?"تم الاستلام":"استلام المكافأة",green);box(c,18,305,342,470,panel,18);txt(c,"مكافأة الفيديو",180,340,18,white,Paint.Align.CENTER);txt(c,"10,000,000 🪙 لكل فيديو",180,380,20,gold,Paint.Align.CENTER);txt(c,"نسخة تجريبية محلية",180,410,11,Color.LTGRAY,Paint.Align.CENTER);button(c,75,425,285,465,"مشاهدة الفيديو",green);nav(c);}

    @Override public boolean onTouchEvent(MotionEvent e){if(e.getAction()!=MotionEvent.ACTION_UP)return true;float x=e.getX()/d,y=e.getY()/d;
        if(y>700){if(x<72)screen=0;else if(x<144)screen=1;else if(x<216)screen=4;else if(x<288)screen=5;else screen=3;invalidate();return true;}
        if(screen==0){if(y>140&&y<300||y>560)screen=1;}
        else if(screen==1&&y>=120&&y<600){int idx=Math.max(0,Math.min(3,(int)((y-120)/112)));startRoom(idx);screen=2;}
        else if(screen==2){if(y>500&&y<575){if(x<115)fold();else if(x<245)call();else raise();}else if(y>580&&y<650){if(showdown)startHand();else advanceIfReady();}}
        else if(screen==5&&y>215&&y<280&&!fbClaimed){coins+=10000000;fbClaimed=true;}
        invalidate();return true;
    }
    static class Card{String rank,suit;Card(String r,String s){rank=r;suit=s;}}
    static class Player{String name;long stack;boolean me,folded=false;long bet=0;ArrayList<Card> hand=new ArrayList<>();Player(String n,long s,boolean m){name=n;stack=s;me=m;}}
    static class Room{String name;int blind,max,occupied=0;Room(String n,int b,int m){name=n;blind=b;max=m;}}
}
