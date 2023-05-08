package com.vad.pomodoro.ui;

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

public class PomodoroSettingsDialog extends DialogFragment implements NumberPicker.OnValueChangeListener {

    private SaveConfiguration configuration;
    private PomodoroUpdate pomodoroUpdate;
    private NumberPicker numberPickerPomodoro;
    private NumberPicker numberPickerShort;
    private NumberPicker numberPickerLong;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        configuration = new SaveConfiguration(context);
        pomodoroUpdate = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_pomodoro_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        numberPickerPomodoro = view.findViewById(R.id.numberPickerPomodoro);
        numberPickerPomodoro.setMinValue(25);
        numberPickerPomodoro.setMaxValue(60);
        Log.d("##ok", "onViewCreated: ");
        numberPickerPomodoro.setValue(configuration.getPomodoro());
        numberPickerPomodoro.setOnValueChangedListener(this);

        numberPickerShort = view.findViewById(R.id.numberPickerShort);
        numberPickerShort.setMinValue(5);
        numberPickerShort.setMaxValue(15);
        numberPickerShort.setValue(configuration.getShort());
        numberPickerShort.setOnValueChangedListener(this);

        numberPickerLong = view.findViewById(R.id.numberPickerLong);
        numberPickerLong.setMinValue(15);
        numberPickerLong.setMaxValue(30);
        numberPickerLong.setValue(configuration.getLong());
        numberPickerLong.setOnValueChangedListener(this);

        TextView ok = view.findViewById(R.id.doneSetting);
        ok.setOnClickListener(v -> {
            pomodoroUpdate.update();
            dismiss();
        });

    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (picker.equals(numberPickerPomodoro)) {
            configuration.savePomodoro(newVal);
        } else if (picker.equals(numberPickerShort)) {
            configuration.saveShort(newVal);
        } else if (picker.equals(numberPickerLong)) {
            configuration.saveLong(newVal);
        }
    }
}
