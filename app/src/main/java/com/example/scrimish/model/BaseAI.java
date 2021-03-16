package com.example.scrimish.model;

import java.util.ArrayList;

public abstract class BaseAI extends Player {

    protected ArrayList<ArrayList<Carta>> mOpponentCards;

    protected BaseAI() {
        mOpponentCards = initUnknown();
    }

    @Override
    public final void attack(int mazoYo, Carta top, int mazoEl, Game.CardRequestedCallback callback) {
        callback.onAttackResolved(topOfMazo(mazoEl));
    }

    @Override
    public final void onDiscard(int mazoYo) {
        mOpponentCards.get(mazoYo).remove(0);
    }

    @Override
    public void sendInfo(int mazoInt, Carta cartaTop, boolean wasCardRemoved) {
        ArrayList<Carta> mazo = mOpponentCards.get(mazoInt);
        if (wasCardRemoved) mazo.remove(0);
        else mazo.set(0, cartaTop);
    }
}
