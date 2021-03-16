package com.example.scrimish;

import com.example.scrimish.model.Carta;
import com.example.scrimish.model.Game;
import com.example.scrimish.model.Human;

import java.util.ArrayList;

public class RemoteHuman extends Human {

    @Override
    public ArrayList<ArrayList<Carta>> initCartas() {
        return initUnknown();
    }

    @Override
    public void attack(int mazoYo, Carta top, final int mazoEl, final Game.CardRequestedCallback callback) {
        RoomManager.getInstance().sendMessage(String.format("%d %s %d", mazoYo, top.toString(), mazoEl));
        if (topOfMazo(mazoEl) != null) {
            callback.onAttackResolved(topOfMazo(mazoEl));
            return;
        }
        RoomManager.getInstance().registerCallback(new RoomManager.Callback() {
            @Override
            public void onMessageReceived(String s, long timestamp) {
                Carta carta = new Carta(s);
                getCartas().get(mazoEl).set(0, carta);
                callback.onAttackResolved(carta);

            }
        });
    }

    @Override
    public void onDiscard(int mazoYo) {
        RoomManager.getInstance().sendMessage(String.format("%d %s %d", mazoYo, "DISCARD", -1));
    }

    @Override
    public void doTurn(final Game.TurnRequestedCallback callback) {
        RoomManager.getInstance().registerCallback(new RoomManager.Callback() {
            @Override
            public void onMessageReceived(String s, long timestamp) {
                String[] split = s.split(" ");
                if (split.length != 3) return;

                if (split[1].equals("DISCARD")) {
                    callback.onDiscard(Integer.parseInt(split[0]));
                    return;
                }

                final int mazoEl = Integer.parseInt(split[0]);
                final Carta carta = new Carta(split[1]);
                final int mazoYo = Integer.parseInt(split[2]);

                getCartas().get(mazoEl).set(0, carta);

                callback.onTurnResolved(mazoEl, carta, mazoYo);
            }
        });
    }
}
