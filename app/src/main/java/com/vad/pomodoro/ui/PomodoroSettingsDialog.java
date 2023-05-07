package com.vad.pomodoro.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.vad.pomodoro.R;
import com.vad.pomodoro.model.SaveConfiguration;

public class PomodoroSettingsDialog extends DialogFragment implements NumberPicker.OnValueChangeListener {

    private SaveConfiguration configuration;
    private NumberPicker numberPickerPomodoro;
    private NumberPicker numberPickerShort;
    private NumberPicker numberPickerLong;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        configuration = new SaveConfiguration(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_pomodoro_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        numberPickerPomodoro = view.findViewById(R.id.numberPickerPomodoro);
        numberPickerPomodoro.setWrapSelectorWheel(false);
        numberPickerPomodoro.setMinValue(25);
        numberPickerPomodoro.setMaxValue(60);
        numberPickerPomodoro.setOnValueChangedListener(this);

        numberPickerShort = view.findViewById(R.id.numberPickerShort);
        numberPickerShort.setWrapSelectorWheel(false);
        numberPickerShort.setMinValue(5);
        numberPickerShort.setMaxValue(15);
        numberPickerShort.setOnValueChangedListener(this);

        numberPickerLong = view.findViewById(R.id.numberPickerLong);
        numberPickerLong.setWrapSelectorWheel(false);
        numberPickerLong.setMinValue(15);
        numberPickerLong.setMaxValue(30);
        numberPickerLong.setOnValueChangedListener(this);

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
