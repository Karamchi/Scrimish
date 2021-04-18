package com.example.scrimish;

import androidx.annotation.RawRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.roommanager.RoomManager;
import com.example.scrimish.model.AI;
import com.example.scrimish.model.Carta;
import com.example.scrimish.model.Game;
import com.example.scrimish.model.Human;
import com.example.scrimish.view.MazoView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private int[] mazos_el_ints = {R.id.mazo_1, R.id.mazo_2, R.id.mazo_3, R.id.mazo_4, R.id.mazo_5};
    private int[] mazos_yo_ints = {R.id.mazo_1_yo, R.id.mazo_2_yo, R.id.mazo_3_yo, R.id.mazo_4_yo, R.id.mazo_5_yo};

    private ArrayList<MazoView> mazosYo = new ArrayList<>();
    private ArrayList<MazoView> mazosEl = new ArrayList<>();

    private Game game;
    private TextView mTextStatus;
    private Carta mAttackedCard;
    private MediaPlayer mp;

    //TODO:
    // P2P specific
    // 2 - connectivity listener (or at least handle failures
    // better howto??
    // BETTER SIGNALIZATION OF WHICH ARE YOUR DECKS AND THE OPPONENT'S
    // for both
    // 5 - knowledge to player?
    // 7 - arrastrar en vez de clicks
    // bugs
    // sometimes they are both p2 (perhaps timestamp = 0)

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.deck_build_layout).setVisibility(View.GONE);
        findViewById(R.id.deck_build_layout_2).setVisibility(View.GONE);
        findViewById(R.id.button_confirm).setVisibility(View.GONE);
        findViewById(R.id.button_change_hero).setVisibility(View.GONE);


        mTextStatus = findViewById(R.id.waiting_for_opponent);
        mTextStatus.setText("");

        mTextStatus = findViewById(R.id.title);
        mTextStatus.setText("");

        ArrayList<ArrayList<Carta>> cartas =
                (ArrayList<ArrayList<Carta>>) getIntent().getExtras().getSerializable("Cartas");

        if (getIntent().getBooleanExtra("AI", false)) {
            game = new Game(new Human(cartas), new AI());
        } else {
            game = new Game(new Human(cartas), new RemoteHuman());
        }

        for (int i = 0; i < mazos_yo_ints.length; i++) {
            mazosYo.add((MazoView) findViewById(mazos_yo_ints[i]));
            mazosYo.get(i).setKing((MazoView.King) getIntent().getSerializableExtra("King1"));
            mazosYo.get(i).setCartas(cartas.get(i));
            mazosYo.get(i).showFrontOfAll(true);
        }

        for (int i = 0; i < mazos_el_ints.length; i++) {
            mazosEl.add((MazoView) findViewById(mazos_el_ints[i]));
            mazosEl.get(i).setKing((MazoView.King) getIntent().getSerializableExtra("King2"));
        }

        if (getIntent().getBooleanExtra("First", false)) {
            readyPlayer1();
        } else {
            readyPlayer2();
        }
        setListeners();
        RoomManager.getInstance().setMembershipListener(new RoomManager.MembershipListener() {
            @Override
            public void onMembers(int size) {}

            @Override
            public void onMemberLeave() {
                if (game.getState().equals(State.NOT_STARTED)) return;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextStatus.setText("Opponent disconnected");
                        RoomManager.getInstance().disconnect();
                    }
                });
                game.waiting();
            }
        });

        mp = MediaPlayer.create(this, R.raw.exploracion_4);
        mp.setLooping(true);
//        mp.start();
    }

    public void readyPlayer1() {
        game.player1();
        mTextStatus.setText("");
    }

    public void readyPlayer2() {
        game.player2(getAwayPlayerCallback());
        mTextStatus.setText("Waiting for opponent to play...");
    }

    public void setListeners() {
        for (int mazo = 0; mazo < mazosYo.size(); mazo++) {
            final int mazo2 = mazo;
            mazosYo.get(mazo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    for (MazoView m : mazosYo) {
//                        m.expand(false);
//                    }
                    if ((mazosYo).get(mazo2).isSelected())
                        mazosYo.get(mazo2).expand(false);
                    else
                        mazoClickeadoYo(mazo2);
                }
            });
        }

        for (final MazoView mazo : mazosYo) {
            mazo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
//                    for (MazoView m : mazosYo) {
//                        m.expand(false);
//                    }
                    boolean expand = !mazo.isExpanded();
                    expandMazos(false);
//                    if (game.getState() != State.WAITING_FOR_AWAY_PLAYER)
//                        game.player1();
                    mazo.expand(expand);

//                    findViewById(R.id.button_discard).setVisibility(View.GONE);

                    return true;
                }
            });
        }

        for (int mazo = 0; mazo < mazosEl.size(); mazo++) {
            final int mazo2 = mazo;
            mazosEl.get(mazo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mazoClickeadoEl(mazo2);
                }
            });
        }
        findViewById(R.id.button_discard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discard(game.mazoClickeado);
            }
        });
        findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HowToActivity.class);
                startActivity(intent);
            }
        });
    }

    public void mazoClickeadoYo(int mazo) {
        if (game.getState() != State.NADA_CLICKEADO && game.getState() != State.CARTA_MIA_CLICKEADA) return;
        clearMazos(mazosEl);
        hideTops(mazosEl);
        updateStacks(true);
        if (game.cartasEnMazo(mazo) == 0) return;
        game.setMazoClickeado(mazo, true);
        setSelected(mazosYo, mazo);
        findViewById(R.id.button_discard).setVisibility(game.top.type != Carta.Type.KING ? View.VISIBLE : View.GONE);
    }

    public void setSelected(ArrayList<MazoView> mazos, int mazo) {
        clearMazos(mazos);
        mazos.get(mazo).setSelected(true);
    }

    public void setSelected(ArrayList<MazoView> mazos, int mazo, Carta top) {
        clearMazos(mazos);
        mazos.get(mazo).showTop(top);
    }

    private void clearMazos(ArrayList<MazoView> mazos) {
        for (MazoView mazo : mazos) {
//            mazo.expand(false);
            mazo.setSelected(false);
//            mazo.showTop(false);
        }
    }

    private void expandMazos(boolean expand) {
        for (MazoView mazo : mazosYo) {
            mazo.expand(expand);
        }
    }

    private void hideTops(ArrayList<MazoView> mazos) {
        for (MazoView mazo : mazos) {
            mazo.showTop(false);
        }
    }

    public void mazoClickeadoEl(final int mazoClickeadoEl) {
        if (game.getState() != State.CARTA_MIA_CLICKEADA) return;
        if (game.top.type == Carta.Type.SHIELD) return;
        if (game.pilonesAway().get(mazoClickeadoEl) == 0) return;
        findViewById(R.id.button_discard).setVisibility(View.GONE);
        mazosEl.get(mazoClickeadoEl).doAnimate(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mAttackedCard != null)
                    finishResolveAttack(mazoClickeadoEl, mAttackedCard);
            }
        });
        playSound(game.top.getSound());
        game.attack(game.mazoClickeado, game.top, mazoClickeadoEl, new Game.CardRequestedCallback() {
            @Override
            public void onAttackResolved(final Carta cartaEl) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAttackedCard = cartaEl;
                        setSelected(mazosEl, mazoClickeadoEl, cartaEl);
                        if (!mazosEl.get(mazoClickeadoEl).mIsAnimating)
                            finishResolveAttack(mazoClickeadoEl, mAttackedCard);
                    }
                });
            }
        });
    }

    public void discard(final int mazoClickeadoYo) {
        game.discard(mazoClickeadoYo, getAwayPlayerCallback());
        findViewById(R.id.button_discard).setVisibility(View.GONE);
        mTextStatus.setText("Waiting for opponent to play...");
        updateStacks(false);
    }

    public void finishResolveAttack(int mazoClickeadoEl, Carta cartaEl) {
        game.onAttackResolved(game.mazoClickeado, mazoClickeadoEl, cartaEl, getAwayPlayerCallback());
        mTextStatus.setText("Waiting for opponent to play...");
        if (game.getState() == State.NOT_STARTED) mTextStatus.setText("Game Over");
        else updateStacks(false);
        mAttackedCard = null;
    }

    private Game.TurnRequestedCallback getAwayPlayerCallback() {
        return new Game.TurnRequestedCallback() {
            @Override
            public void onTurnResolved(final int mazoEl, final Carta carta, final int mazoYo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateStacks(true);
                        hideTops(mazosEl);
                        setSelected(mazosEl, mazoEl, carta);
                        game.setMazoClickeado(mazoYo, false);
                        setSelected(mazosYo, mazoYo);
                        game.onDefense(mazoYo, mazoEl, carta);
                        RoomManager.getInstance().sendMessage(game.top.toString());
                        mTextStatus.setText("");
                        if (game.getState() == State.NOT_STARTED) mTextStatus.setText("Game Over");
                        else updateStacks(false);
                        playSound(carta.getSound());
                    }
                });
            }

            @Override
            public void onDiscard(int mazoEl) {
                updateStacks(true);
                game.onDefenseDiscard(mazoEl);
                mTextStatus.setText("");
                updateStacks(false);
            }
        };
    }

    public void updateStacks(boolean immediate) {
        for (int i = 0; i < Game.MAZOS; i++) {
            mazosEl.get(i).setStack(game.pilonesAway().get(i), immediate);
            mazosYo.get(i).setStack(game.pilonesHome().get(i), immediate);
        }
    }

    @Override
    public void onBackPressed() {
        if (State.NOT_STARTED.equals(game.getState())) {
            super.onBackPressed();
            return;
        }
        new AlertDialog.Builder(this).setTitle("Abandon the game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        RoomManager.getInstance().disconnect();
        super.onDestroy();
        mp.stop();
    }

    private void playSound(@RawRes Integer sound) {
        if (sound == null) return;
        MediaPlayer mp = MediaPlayer.create(this, sound);
//        mp.start();
    }

}
