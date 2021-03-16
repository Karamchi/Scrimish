package com.example.scrimish.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.example.scrimish.model.Game.MAZOS;

public abstract class Player {

    private ArrayList<ArrayList<Carta>> cartas;

    Player() {
        this.cartas = initCartas();
    }

    protected static ArrayList<ArrayList<Carta>> initRandom() {
        ArrayList<ArrayList<Carta>> res = initVacio();

        List<Carta> inicial = Carta.mazoInicial();
        inicial.remove(0);
        res.get((new Random().nextInt() % MAZOS + MAZOS) % MAZOS).add(new Carta(Carta.Type.KING));
        addRandomly(res, inicial);
        return res;
    }

    protected static ArrayList<ArrayList<Carta>> initUnknown() {
        ArrayList<ArrayList<Carta>> res = new ArrayList<>();
        for (int i = 0; i < MAZOS; i++) {
            ArrayList<Carta> a = new ArrayList<>();
            for (int j = 0; j < MAZOS; j++)
                a.add(null);
            res.add(a);
        }
        return res;
    }

    protected static ArrayList<ArrayList<Carta>> initVacio() {
        ArrayList<ArrayList<Carta>> res = new ArrayList<>();
        for (int i = 0; i < MAZOS; i++) {
            res.add(new ArrayList<Carta>());
        }
        return res;
    }

    Player(ArrayList<ArrayList<Carta>> cartas) {
        this.cartas = cartas;
    }

    public final ArrayList<Integer> tamanoPilones() {
        ArrayList<Integer> res = new ArrayList<>();
        for (ArrayList a : cartas) {
            res.add(a.size());
        }
        return res;
    }

    public abstract ArrayList<ArrayList<Carta>> initCartas();

    public final ArrayList<ArrayList<Carta>> getCartas() {
        return cartas;
    }

    public final Carta topOfMazo(int mazo) {
        return cartas.get(mazo).get(0);
    }

    public abstract void doTurn(Game.TurnRequestedCallback callback);

    public abstract void sendInfo(int mazo, Carta cartaTop, boolean wasCardRemoved);

    public abstract void attack(int mazoYo, Carta top, int mazoEl, Game.CardRequestedCallback callback);

    public void finishRandom(int[] pilonesAway) {

        List<List<Carta>> vacios = new ArrayList<>();
        if (pilonesAway[new Carta(Carta.Type.KING).toIndex()] > 0) {
            for (List<Carta> mazo : cartas) {
                if (mazo.size() == 0) {
                    vacios.add(mazo);
                }
            }
        }
        if (vacios.size() > 0) {
            Collections.shuffle(vacios);
            vacios.get(0).add(0, new Carta(Carta.Type.KING));
            pilonesAway[new Carta(Carta.Type.KING).toIndex()] = 0;
        }

        List<Carta> faltantes = new ArrayList<>();
        for (int i = 0; i < pilonesAway.length; i++) {
            for (int j = 0; j < pilonesAway[i]; j++) {
                faltantes.add(Carta.FromIndex(i));
            }
        }
        addRandomly(cartas, faltantes);
    }

    private static void addRandomly(ArrayList<ArrayList<Carta>> cartas, List<Carta> faltantes) {
        Collections.shuffle(faltantes);
        for (List<Carta> mazo : cartas) {
            while (mazo.size() < 5) {
                mazo.add(0, faltantes.get(0));
                faltantes.remove(0);
            }
        }
    }

    public abstract void onDiscard(int mazoYo);
}
