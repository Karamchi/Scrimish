package com.example.scrimish.model;

import android.os.Handler;

import com.example.scrimish.State;

import java.util.List;

public class Game {

    public Carta top;
    private Player homePlayer;
    private Player awayPlayer;

    public static final int MAZOS = 5;

    private State state = null;
    public int mazoClickeado;

    public Game(Player home, Player away) {
        homePlayer = home;
        awayPlayer = away;
    }

    public void attack(int mazoYo, Carta top, int mazoEl, final CardRequestedCallback callback) {
        awayPlayer.attack(mazoYo, top, mazoEl, callback);
        state = State.WAITING_FOR_AWAY_PLAYER;
    }

    public void discard(int mazoYo, TurnRequestedCallback awayPlayerCallback) {
        homePlayer.getCartas().get(mazoYo).remove(0);
        awayPlayer.onDiscard(mazoYo);
        state = State.WAITING_FOR_AWAY_PLAYER;
        awayPlayer.doTurn(awayPlayerCallback);
    }

    public void onAttackResolved(int mazoYo, int mazoEl, Carta cartaEl, TurnRequestedCallback callback) {
        Carta cartaYo = homePlayer.topOfMazo(mazoYo);
        awayPlayer.sendInfo(mazoYo, cartaYo, cartaEl.beatsDef(cartaYo));

        if (cartaYo.beatsOff(cartaEl)) {
            awayPlayer.getCartas().get(mazoEl).remove(0);
        }
        if (cartaEl.beatsDef(cartaYo)) {
            homePlayer.getCartas().get(mazoYo).remove(0);
        }
        if (cartaYo.type == Carta.Type.KING || cartaEl.type == Carta.Type.KING) {
            state = State.NOT_STARTED;
        } else {
            state = State.WAITING_FOR_AWAY_PLAYER;
            awayPlayer.doTurn(callback);
        }
    }

    public void onDefense(int mazoYo, int mazoEl, Carta cartaEl) {
        Carta cartaYo = homePlayer.topOfMazo(mazoYo);
        awayPlayer.sendInfo(mazoYo, cartaYo, cartaEl.beatsOff(cartaYo));

        if (cartaYo.beatsDef(cartaEl)) {
            awayPlayer.getCartas().get(mazoEl).remove(0);
        }
        if (cartaEl.beatsOff(cartaYo)) {
            homePlayer.getCartas().get(mazoYo).remove(0);
        }
        if (cartaYo.type == Carta.Type.KING || cartaEl.type == Carta.Type.KING) {
            state = State.NOT_STARTED;
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    state = State.NADA_CLICKEADO;
                }
            }, 800);
        }
    }

    public void onDefenseDiscard(int mazoEl) {
        awayPlayer.getCartas().get(mazoEl).remove(0);
        state = State.NADA_CLICKEADO;
    }

    public void setMazoClickeado(int mazo, boolean fromUser) {
        if (fromUser) state = State.CARTA_MIA_CLICKEADA;
        mazoClickeado = mazo;
        top = homePlayer.topOfMazo(mazo);;
    }

    public int cartasEnMazo(int mazo) {
        return pilonesHome().get(mazo);
    }

    public List<Integer> pilonesHome() {
        return homePlayer.tamanoPilones();
    }

    public List<Integer> pilonesAway() {
        return awayPlayer.tamanoPilones();
    }

    public State getState() {
        return state;
    }

    public void player1() {
        state = State.NADA_CLICKEADO;
    }

    public void player2(TurnRequestedCallback callback) {
        awayPlayer.doTurn(callback);
    }

    public void waiting() {
        state = State.WAITING_FOR_AWAY_PLAYER;
    }

    public interface CardRequestedCallback {
        void onAttackResolved(Carta card);
    }

    public interface TurnRequestedCallback {
        void onTurnResolved(int mazoEl, Carta carta, int mazoYo);

        void onDiscard(int mazoEl);
    }
}
