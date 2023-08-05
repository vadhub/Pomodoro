package com.vad.pomodoro.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vad.pomodoro.R;

public class IndicatorRound {

    private final Context context;

    private final ImageView oneRound;
    private final ImageView twoRound;
    private final ImageView threeRound;
    private final ImageView fourRound;
    private final TextView roundTextView;
    private final ProgressBar progressBar;

    public IndicatorRound(Context context, ImageView oneRound, ImageView twoRound, ImageView threeRound, ImageView fourRound, TextView roundTextView, ProgressBar progressBar) {
        this.context = context;
        this.oneRound = oneRound;
        this.twoRound = twoRound;
        this.threeRound = threeRound;
        this.fourRound = fourRound;
        this.roundTextView = roundTextView;
        this.progressBar = progressBar;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setRelax() {
        progressBar.setProgressDrawable(context.getDrawable(R.drawable.pie_progress_relax));
    }

    public void setWork() {
        progressBar.setProgressDrawable(context.getDrawable(R.drawable.pie_progress_work));
    }

    public void changeRound(int round) {
        switch (round) {
            case 1:
                roundTextView.setText(context.getString(R.string.round1));
                setWork();
                oneRound.setImageDrawable(context.getDrawable(R.drawable.indicator_current));
                fourRound.setImageDrawable(context.getDrawable(R.drawable.indicator_empty));
                threeRound.setImageDrawable(context.getDrawable(R.drawable.indicator_empty));
                twoRound.setImageDrawable(context.getDrawable(R.drawable.indicator_empty));
                break;

            case 2:
                roundTextView.setText(context.getString(R.string.relax));
                setRelax();
                oneRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                twoRound.setImageDrawable(context.getDrawable(R.drawable.indicator_empty));
                threeRound.setImageDrawable(context.getDrawable(R.drawable.indicator_empty));
                fourRound.setImageDrawable(context.getDrawable(R.drawable.indicator_empty));
                break;

            case 3:
                roundTextView.setText(context.getString(R.string.round2));
                setWork();
                oneRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                twoRound.setImageDrawable(context.getDrawable(R.drawable.indicator_current));
                threeRound.setImageDrawable(context.getDrawable(R.drawable.indicator_empty));
                fourRound.setImageDrawable(context.getDrawable(R.drawable.indicator_empty));
                break;

            case 4:
                roundTextView.setText(context.getString(R.string.relax));
                setRelax();
                oneRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                twoRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                threeRound.setImageDrawable(context.getDrawable(R.drawable.indicator_empty));
                fourRound.setImageDrawable(context.getDrawable(R.drawable.indicator_empty));
                break;

            case 5:
                roundTextView.setText(context.getString(R.string.round3));
                setWork();
                oneRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                twoRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                threeRound.setImageDrawable(context.getDrawable(R.drawable.indicator_current));
                fourRound.setImageDrawable(context.getDrawable(R.drawable.indicator_empty));
                break;

            case 6:
                roundTextView.setText(context.getString(R.string.relax));
                setRelax();
                oneRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                twoRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                threeRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                fourRound.setImageDrawable(context.getDrawable(R.drawable.indicator_empty));
                break;

            case 7:
                roundTextView.setText(context.getString(R.string.round4));
                setWork();
                oneRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                twoRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                threeRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                fourRound.setImageDrawable(context.getDrawable(R.drawable.indicator_current));
                break;

            case 0:
                roundTextView.setText(context.getString(R.string.relax));
                setRelax();
                oneRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                twoRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                threeRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                fourRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                break;

        }
    }
}
