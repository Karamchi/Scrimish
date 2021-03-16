package com.example.scrimish;

import com.example.scrimish.model.Carta;
import com.example.scrimish.model.Player;

import java.util.ArrayList;
import java.util.List;

public class BuildDeckState {

    public Carta top;
    private Player homePlayer;
    private int[] pilonesAway = {5, 5, 3, 3, 2, 2, 2, 2, 1};

    private State state = null;
    public int mazoClickeado;

    public BuildDeckState(Player home) {
        homePlayer = home;
    }

    public void onCardAdded(int mazoEl, int mazoYo, Carta carta) {
        pilonesAway[mazoEl] -= 1;
        homePlayer.getCartas().get(mazoYo).add(0, carta);
        if (cardsLeft(pilonesAway())) {
            state = State.NOT_STARTED;
        } else {
            state = State.NADA_CLICKEADO;
        }
    }

    private boolean cardsLeft(List<Integer> pilonesAway) {
        for (int i : pilonesAway) {
            if (i > 0) return false;
        }
        return true;
    }

    public ArrayList<ArrayList<Carta>> cartas() {
        return homePlayer.getCartas();
    }

    public void setMazoClickeado(int mazo) {
        state = State.CARTA_MIA_CLICKEADA;
        mazoClickeado = mazo;
        top = Carta.FromIndex(mazoClickeado);
    }

    public List<Integer> pilonesHome() {
        return homePlayer.tamanoPilones();
    }

    public List<Integer> pilonesAway() {
        List<Integer> list1 = new ArrayList<>();
        for (int in : pilonesAway) list1.add(in);
        return list1;
    }

    public State getState() {
        return state;
    }

    public void player1() {
        state = State.NADA_CLICKEADO;
    }

    public void onCardRemoved(int mazoInt) {
        ArrayList<Carta> mazo = homePlayer.getCartas().get(mazoInt);
        int mazoEl = mazo.get(0).toIndex();
        pilonesAway[mazoEl] += 1;
        mazo.remove(0);
        if (mazo.size() > 0)
            top = mazo.get(0);
        state = State.NADA_CLICKEADO;
    }

    public void waiting() {
        state = State.WAITING_FOR_AWAY_PLAYER;
    }

    public void randomize() {
        homePlayer.finishRandom(pilonesAway);
        for (int i = 0; i < pilonesAway.length; i++)
            pilonesAway[i] = 0;
        state = State.NOT_STARTED;
    }
}
