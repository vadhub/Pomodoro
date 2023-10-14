package com.vad.pomodoro.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.vad.pomodoro.PomodoroUpdate;
import com.vad.pomodoro.R;
import com.vad.pomodoro.model.SaveConfiguration;

public class PomodoroSettingsDialog extends DialogFragment {

    private SaveConfiguration configuration;
    private PomodoroUpdate pomodoroUpdate;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        configuration = new SaveConfiguration(context);
        pomodoroUpdate = (TimerFragment) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = getLayoutInflater().inflate(R.layout.dialog_pomodoro_settings, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        Bundle args = getArguments();

        String currentSetting = args.getString("current_setting", getContext().getResources().getString(R.string.pomodoro));
        int minValue = args.getInt("min_value", 25);
        int maxValue = args.getInt("max_value", 60);
        int state = args.getInt("state", 0);

        TextView currentSet = view.findViewById(R.id.textState);
        currentSet.setText(currentSetting);

        NumberPicker numberPicker = view.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        Log.d("##ok", "onViewCreated: ");
        numberPicker.setValue(configuration.getPomodoro());

        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (state == 0) {
                configuration.savePomodoro(newVal);
            } else if (state == 1) {
                configuration.saveShort(newVal);
            } else if (state == 2) {
                configuration.saveLong(newVal);
            }
        });

        builder.setView(view)
                .setPositiveButton(view.getContext().getResources().getString(R.string.ok), (dialog, which) -> {
                    pomodoroUpdate.update();
                    dismiss();
                }).setNegativeButton(view.getContext().getResources().getString(R.string.cancel), ((dialog, which) -> {
                    dismiss();
                }));

        return builder.create();
    }
}
