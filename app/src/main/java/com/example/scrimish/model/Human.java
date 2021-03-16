package com.example.scrimish.model;

import java.util.ArrayList;

public class Human extends Player {

    public Human() {
        super();
    }

    public Human(ArrayList<ArrayList<Carta>> cartas) {
        super(cartas);
    }

    @Override
    public ArrayList<ArrayList<Carta>> initCartas() {
        return initVacio();
    }

    @Override
    public void doTurn(Game.TurnRequestedCallback callback) {

    }

    @Override
    public void sendInfo(int mazo, Carta cartaTop, boolean wasCardRemoved) {
        //Handled by human
    }

    @Override
    public void attack(int mazoYo, Carta top, int mazoEl, Game.CardRequestedCallback callback) {
        //Handled by human
    }

    @Override
    public void onDiscard(int mazoYo) {
        //Handled by human
    }
}
