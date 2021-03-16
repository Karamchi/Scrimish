package com.example.scrimish.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.RawRes;

import com.example.scrimish.R;
import com.example.scrimish.view.MazoView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Carta implements Serializable {

    public @RawRes Integer[] sound = {R.raw.knife_hit1, R.raw.espadas, null, null, null, R.raw.espadas, null, R.raw.flechas_java, null, null};

    public enum Type {
        NUMBER,
        SHIELD,
        ARCHER,
        KING,
        UNKNOWN
    }

    public @DrawableRes int[] images =   {R.drawable.c1,  R.drawable.c2,  R.drawable.c3,  R.drawable.c4,  R.drawable.c5,  R.drawable.c6,  R.drawable.shield,   R.drawable.archer,   R.drawable.king,   R.drawable.unknown,   R.drawable.back};

    public @DrawableRes int[] images_r = {R.drawable.r_1, R.drawable.r_2, R.drawable.r_3, R.drawable.r_4, R.drawable.r_5, R.drawable.r_6, R.drawable.r_shield, R.drawable.r_archer, R.drawable.r_king, R.drawable.r_unknown, R.drawable.r_back};

    public @DrawableRes int[] images_v = {R.drawable.v_1, R.drawable.v_2, R.drawable.v_3, R.drawable.v_4, R.drawable.v_5, R.drawable.v_6, R.drawable.v_shield, R.drawable.v_archer, R.drawable.v_king, R.drawable.v_unknown, R.drawable.v_back};

    public @DrawableRes int[] images_a = {R.drawable.a_1, R.drawable.a_2, R.drawable.a_3, R.drawable.a_4, R.drawable.a_5, R.drawable.a_6, R.drawable.a_shield, R.drawable.a_archer, R.drawable.a_king, R.drawable.a_unknown, R.drawable.a_back};

    public @DrawableRes int [][] images_all = {images, images_r, images_v, images_a};

    private Integer value;
    public Type type;

    public Carta(Type type) {
        this.type = type;
    }

    protected Carta(int i) {
        this.type = Type.NUMBER;
        this.value = i;
    }

    public Carta(String carta) {
        if (carta.equals("SHIELD")) this.type = Type.SHIELD;
        else if (carta.equals("ARCHER")) this.type = Type.ARCHER;
        else if (carta.equals("KING")) this.type = Type.KING;
        else {
            this.type = Type.NUMBER;
            this.value = Integer.parseInt(carta);
        }
    }

    public static Carta FromIndex(int i) {
        if (i < 6) return new Carta(i + 1);
        if (i == 6) return new Carta(Type.SHIELD);
        if (i == 7) return new Carta(Type.ARCHER);
        return new Carta(Type.KING);
    }

    public int toIndex() {
        if (type == Type.SHIELD) return 6;
        if (type == Type.ARCHER) return 7;
        if (type == Type.KING) return 8;
        if (type == Type.UNKNOWN) return 9;
        return value - 1;
    }

    public @DrawableRes int getImage(MazoView.King king) {
        return images_all[king.ordinal()][toIndex()];
    }

    public @DrawableRes int getBack(MazoView.King king) {
        return images_all[king.ordinal()][10];
    }

    //Al atacar, elimina al otro
    public boolean beatsOff(Carta def) {
        if (type == Type.KING && def.type != Type.KING) return false;
        if (type == Type.ARCHER) return def.type != Type.SHIELD;
        if (def.type == Type.KING || def.type == Type.SHIELD || def.type == Type.ARCHER) return true;
        /*if (this.value == null || def.value == null) {
            Log.e("NPE", this.toString() + def.toString());
            return true;
        }*/
        return this.value >= def.value;
    }

    //Al defender, elimina al otro
    public boolean beatsDef(Carta atk) {
        if (atk.type == Type.ARCHER) return false;
        if (atk.type == Type.KING) return this.type != Type.KING;
        if (type == Type.SHIELD) return true;
        if (type == Type.ARCHER || this.type == Type.KING) return false;
        return this.value >= atk.value;
    }

    public static List<Carta> mazoInicial() {
        int[] amount = {5, 5, 3, 3, 2, 2};
        List<Carta> res = new ArrayList<>(Arrays.asList(new Carta(Type.KING), new Carta(Type.ARCHER),
                new Carta(Type.ARCHER), new Carta(Type.SHIELD), new Carta(Type.SHIELD)));
        for (int i = 0; i < amount.length; i++) {
            for (int j = 0; j < amount[i]; j++) {
                res.add(new Carta(i + 1));
            }
        }
        return res;
    }

    @Override
    public String toString() {
        if (this.value != null) return this.value.toString();
        return this.type.toString();
    }

    public Integer getSound() {
        return sound[toIndex()];
    }

}
