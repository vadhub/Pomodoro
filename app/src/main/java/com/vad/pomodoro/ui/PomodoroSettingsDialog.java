package com.vad.pomodoro.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.vad.pomodoro.R;

public class PomodoroSettingsDialog extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_pomodoro_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NumberPicker numberPickerPomodoro = view.findViewById(R.id.numberPickerPomodoro);
        numberPickerPomodoro.setWrapSelectorWheel(false);
        numberPickerPomodoro.setMinValue(25);
        numberPickerPomodoro.setMaxValue(60);

        NumberPicker numberPickerShort = view.findViewById(R.id.numberPickerShort);
        numberPickerShort.setWrapSelectorWheel(false);
        numberPickerShort.setMinValue(5);
        numberPickerShort.setMaxValue(15);

        NumberPicker numberPickerLong = view.findViewById(R.id.numberPickerLong);
        numberPickerLong.setWrapSelectorWheel(false);
        numberPickerLong.setMinValue(15);
        numberPickerLong.setMaxValue(30);



    }
}
