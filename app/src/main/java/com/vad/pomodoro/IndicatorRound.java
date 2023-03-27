package com.vad.pomodoro;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

public class IndicatorRound {

    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setRound(ImageView oneRound, ImageView twoRound, ImageView threeRound, ImageView fourRound, TextView roundTextView, int round) {
        switch (round) {
            case 1:
                roundTextView.setText(context.getString(R.string.round1));
                oneRound.setImageDrawable(context.getDrawable(R.drawable.indicator_current));
                fourRound.setImageDrawable(context.getDrawable(R.drawable.indicator_empty));
                threeRound.setImageDrawable(context.getDrawable(R.drawable.indicator_empty));
                twoRound.setImageDrawable(context.getDrawable(R.drawable.indicator_empty));
                break;

            case 2:
                roundTextView.setText(context.getString(R.string.round2));
                twoRound.setImageDrawable(context.getDrawable(R.drawable.indicator_current));
                oneRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                break;

            case 3:
                roundTextView.setText(context.getString(R.string.round3));
                threeRound.setImageDrawable(context.getDrawable(R.drawable.indicator_current));
                twoRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                oneRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                break;

            case 4:
                roundTextView.setText(context.getString(R.string.round4));
                fourRound.setImageDrawable(context.getDrawable(R.drawable.indicator_current));
                threeRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                twoRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                oneRound.setImageDrawable(context.getDrawable(R.drawable.indicator_full));
                break;

        }
    }
}
