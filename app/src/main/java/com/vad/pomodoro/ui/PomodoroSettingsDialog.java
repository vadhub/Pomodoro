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
    private NumberPicker numberPicker;

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
        numberPicker = view.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(25);
        numberPicker.setMaxValue(60);
        Log.d("##ok", "onViewCreated: ");
        numberPicker.setValue(configuration.getPomodoro());
        numberPicker.setOnValueChangedListener(this);

        TextView ok = view.findViewById(R.id.doneSetting);
        ok.setOnClickListener(v -> {
            pomodoroUpdate.update();
            dismiss();
        });

    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }
}
