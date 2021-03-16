package com.example.scrimish.model;

import android.os.Handler;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AI extends BaseAI {

    public AI() {
        mOpponentCards = initUnknown();
    }

    private static int value(Carta carta){
        if (carta == null) return 0;
        if (carta.type.equals(Carta.Type.KING)) return 8;
        if (carta.type.equals(Carta.Type.ARCHER)) return 7;
        if (carta.type.equals(Carta.Type.SHIELD)) return -1; //Shouldn't happen
        return carta.toIndex();
    }

    @Override
    public ArrayList<ArrayList<Carta>> initCartas() {
        return initRandom();
    }

    @Override
    public void doTurn(final Game.TurnRequestedCallback callback) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Pair<Carta, Integer>> topsEl = new ArrayList<>();
                List<Pair<Carta, Integer>> topsYo = new ArrayList<>();
                List<Pair<Carta, Integer>> shieldsYo = new ArrayList<>();

                for (int i = 0; i < mOpponentCards.size(); i++) {
                    if (mOpponentCards.get(i).size() != 0) {
                        topsEl.add(new Pair<>(mOpponentCards.get(i).get(0), i));
                    }
                }
                for (int i = 0; i < getCartas().size(); i++) {
                    if (getCartas().get(i).size() != 0) {
                        if (topOfMazo(i).type.equals(Carta.Type.SHIELD)) {
                            shieldsYo.add(new Pair<>(topOfMazo(i), i));
                        } else {
                            topsYo.add(new Pair<>(topOfMazo(i), i));
                        }
                    }
                }
                if (topsYo.size() == 0) {
                    callback.onDiscard(shieldsYo.get(0).second);
                    return;
                }

                Collections.sort(topsEl, getCardComparator());
                Collections.reverse(topsEl);
                Collections.sort(topsYo, getCardComparator());

                for (Pair<Carta, Integer> pE : topsEl) {
                    for (Pair<Carta, Integer> pY : topsYo) {
                        if (pE.first != null && pY.first.beatsOff(pE.first) && !pE.first.beatsDef(pY.first)) {
                            callback.onTurnResolved(pY.second, pY.first, pE.second);
                            return;
                        }
                    }
                    for (Pair<Carta, Integer> pY : topsYo) {
                        if (pE.first != null && pY.first.beatsOff(pE.first)) {
                            callback.onTurnResolved(pY.second, pY.first, pE.second);
                            return;
                        }
                    }
                }
                callback.onTurnResolved(topsYo.get(0).second, topsYo.get(0).first, topsEl.get(0).second);
            }
        }, 2000);
        return;
    }

    private static Comparator<Pair<Carta, Integer>> getCardComparator() {
        return new Comparator<Pair<Carta, Integer>>() {
            @Override
            public int compare(Pair<Carta, Integer> p1, Pair<Carta, Integer> p2) {
                return value(p1.first) - value(p2.first);
            }
        };
    }

}
