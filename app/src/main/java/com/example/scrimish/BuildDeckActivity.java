package com.example.scrimish;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roommanager.RoomManager;
import com.example.scrimish.dialog.ChangeHeroDialog;
import com.example.scrimish.model.Carta;
import com.example.scrimish.model.Human;
import com.example.scrimish.view.MazoView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class BuildDeckActivity extends AppCompatActivity {

    private int[] mazos_yo = {R.id.mazo_1_yo, R.id.mazo_2_yo, R.id.mazo_3_yo, R.id.mazo_4_yo, R.id.mazo_5_yo};
    private int[] mazos_el = {R.id.deck_build_layout_mazo_1, R.id.deck_build_layout_mazo_2, R.id.deck_build_layout_mazo_3,
            R.id.deck_build_layout_mazo_4, R.id.deck_build_layout_mazo_5, R.id.deck_build_layout_mazo_6,
            R.id.deck_build_layout_mazo_a, R.id.deck_build_layout_mazo_s, R.id.deck_build_layout_mazo_k};

    private ArrayList<MazoView> mazosYo = new ArrayList<>();
    private ArrayList<MazoView> mazosEl = new ArrayList<>();

    private BuildDeckState game = new BuildDeckState(new Human());
    private TextView mTextStatus;
    private MazoView.King mOpponentKing;
    private boolean mJumping;
    private MazoView.King mKing = MazoView.King.EDER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextStatus = findViewById(R.id.waiting_for_opponent);
        if (getIntent().getBooleanExtra("AI", false)) {
            ArrayList<MazoView.King> kings = new ArrayList<>(Arrays.asList(MazoView.King.EDER, MazoView.King.MAIA, MazoView.King.PALLEGINA));
//            ArrayList<MazoView.King> kings = new ArrayList<>(Arrays.asList(MazoView.King.values()));
            Collections.shuffle(kings);
            mOpponentKing = kings.get(0);
            mTextStatus.setText("");
        }
        game.player1();
        setListeners();
        RoomManager.getInstance().setMembershipListener(new RoomManager.MembershipListener() {
            @Override
            public void onMembers(int size) {
                if (size == 2) {
                    readyPlayer1();
                }
            }

            @Override
            public void onMemberLeave() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTextStatus.setText("Opponent disconnected");
                    }
                });
            }
        });
        findViewById(R.id.mazos_el).setVisibility(View.GONE);
        updateStacks();

        mazosEl.get(0).setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                return false;
            }
        });
    }

    public void readyPlayer1() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (game.getState() == State.WAITING_FOR_AWAY_PLAYER) {
                    onDoneClicked();
                } else {
                    mTextStatus.setText("");
                }
            }
        });
        RoomManager.getInstance().registerCallback(new RoomManager.Callback() {
            @Override
            public void onMessageReceived(String s, long timestamp) {
                if (s.startsWith("Finished")) {
                    mOpponentKing = MazoView.King.valueOf(s.split(" ")[1]);
                    if (game.getState() == State.WAITING_FOR_AWAY_PLAYER) {
                        if (RoomManager.getInstance().mLastSentTimestamp < timestamp) {
                            startActualGame(true);
                        } else {
                            startActualGame(false);
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTextStatus.setText("Opponent already finished");
                            }
                        });
                    }
                }
            }
        });
    }

    private void onDoneClicked() {
        ((View) findViewById(R.id.button_confirm).getParent()).setVisibility(View.INVISIBLE);
        RoomManager.getInstance().sendMessage("Finished " + mKing.toString());
        game.waiting();
        if (mOpponentKing != null) {
            if (getIntent().getBooleanExtra("AI", false)) {
                startActualGame(new Random().nextBoolean());
            } else {
                startActualGame(false);
            }
        } else {
            mTextStatus.setText("Waiting for opponent to finish...");
        }
    }

    private void startActualGame(boolean iGoFirst) {
        Intent intent = new Intent(BuildDeckActivity.this, MainActivity.class);
        intent.putExtra("First", iGoFirst);
        intent.putExtra("Cartas", game.cartas());
        intent.putExtra("King1", mKing);
        intent.putExtra("King2", mOpponentKing);
        intent.putExtra("AI", getIntent().getBooleanExtra("AI", false));
        mJumping = true;
        finish();
        startActivity(intent);
    }

    public void setListeners() {
        for (int mazo = 0; mazo < mazos_yo.length; mazo++) {
            mazosYo.add((MazoView) findViewById(mazos_yo[mazo]));
            mazosYo.get(mazo).setKing(mKing);
            final int mazo2 = mazo;
            mazosYo.get(mazo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mazoClickeadoYo(mazo2);
                }
            });
        }

        for (int mazo = 0; mazo < mazos_el.length; mazo++) {
            mazosEl.add((MazoView) findViewById(mazos_el[mazo]));
            mazosEl.get(mazo).setKing(mKing);

            final int mazo2 = mazo;

            ArrayList<Carta> pilon = new ArrayList<>();
            for (int i = 0; i < game.pilonesAway().get(mazo); i++) {
                pilon.add(Carta.FromIndex(mazo));
            }
            mazosEl.get(mazo).setCartas(pilon);
            mazosEl.get(mazo).showFrontOfAll(true);
            mazosEl.get(mazo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mazoClickeadoEl(mazo2);
                }
            });
        }

        findViewById(R.id.button_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (game.getState() == State.NOT_STARTED) {
                    onDoneClicked();
                } else {
                    game.randomize();
                    for (int i = 0; i < mazosYo.size(); i++) {
                        mazosYo.get(i).setCartas(game.cartas().get(i));
                    }
                    updateStacks();
                    ((Button) findViewById(R.id.button_confirm)).setText("DONE");
                }
            }
        });
        for (final MazoView mazoView : mazosYo) {
            mazoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    game.player1();
                    mazoView.expand(!mazoView.isExpanded());
                    mazoView.showFrontOfAll(mazoView.isExpanded());
                    return true;
                }

            });
        }
        findViewById(R.id.button_change_hero).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeHeroDialog dialog = new ChangeHeroDialog(BuildDeckActivity.this);
                dialog.setOnFinishListener(new ChangeHeroDialog.OnFinishListener() {
                    @Override
                    public void onFinish(MazoView.King k) {
                        mKing = k;
                        for (MazoView m : mazosYo) m.setKing(k);
                        for (MazoView m : mazosEl) m.setKing(k);
                    }
                });
                dialog.show();
            }
        });
        findViewById(R.id.help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BuildDeckActivity.this, HowToActivity.class);
                startActivity(intent);
            }
        });
    }

    public void mazoClickeadoEl(int mazo) {
        if (game.getState() != State.NADA_CLICKEADO && game.getState() != State.CARTA_MIA_CLICKEADA) return;
        clearMazos(mazos_el);
        if (game.pilonesAway().get(mazo) == 0) return;
        game.setMazoClickeado(mazo);
        setSelected(mazos_el, mazo);
    }

    public void setSelected(int[] mazos, int mazo) {
        clearMazos(mazos);
        findViewById(mazos[mazo]).setSelected(true);
    }

    private void clearMazos(int[] mazos) {
        for (int mazo : mazos) {
            findViewById(mazo).setSelected(false);
        }
    }

    public void mazoClickeadoYo(final int mazo) {
        if (game.getState() == State.CARTA_MIA_CLICKEADA) {
            if (game.pilonesHome().get(mazo) == 5) return;
            Carta cartaEl = game.top;

            game.onCardAdded(game.mazoClickeado, mazo, cartaEl);
            mazosYo.get(mazo).addCard(cartaEl);
            updateStacks();
            setSelected(mazos_yo, mazo);
            if (game.getState() == State.NOT_STARTED)
                ((Button) findViewById(R.id.button_confirm)).setText("DONE");
        }
        else if (game.getState() == State.NADA_CLICKEADO || game.getState() == State.NOT_STARTED) {
            if (game.pilonesHome().get(mazo) == 0) return;
            game.onCardRemoved(mazo);
            ((MazoView) findViewById(mazos_yo[mazo])).removeTop();
            updateStacks();
            if (game.pilonesHome().get(mazo) > 0) {
                setSelected(mazos_yo, mazo);
            }
            ((Button) findViewById(R.id.button_confirm)).setText("Random");
        }

    }

    public void updateStacks() {
        for (int i = 0; i < mazos_yo.length; i++) {
            mazosYo.get(i).setStack(game.pilonesHome().get(i), true);
        }
        for (int i = 0; i < mazos_el.length; i++) {
            mazosEl.get(i).setStack(game.pilonesAway().get(i), true);
        }
    }

    @Override
    protected void onDestroy() {
        Log.e("Activity lifecycle", "destroy");
        if (!mJumping) {
            RoomManager.getInstance().disconnect();
        }
//        RoomManager.getInstance().stopRoomkeeperThread();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.e("Activity lifecycle", "stop");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.e("Activity lifecycle", "pause");
        super.onPause();
    }

}
