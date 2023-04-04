package com.vad.pomodoro.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.vad.pomodoro.CheckOnService;
import com.vad.pomodoro.KeepScreen;
import com.vad.pomodoro.R;
import com.vad.pomodoro.model.SaveConfiguration;

public class OptionDialogFragment extends DialogFragment {

    private CheckOnService consumer;
    private KeepScreen keepScreen;

    private SaveConfiguration configuration;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        consumer = (CheckOnService) context;
        keepScreen = (KeepScreen) context;
        configuration = new SaveConfiguration(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_switch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch aSwitch = (Switch) view.findViewById(R.id.switchService);
        Switch aSwitchScreen = (Switch) view.findViewById(R.id.switchScreen);

        aSwitchScreen.setChecked(configuration.getKeepScreen());
        aSwitch.setChecked(configuration.getShowNotification());

        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            consumer.accept(isChecked);
            configuration.saveShowNotification(isChecked);
        });
        aSwitchScreen.setOnCheckedChangeListener((buttonView, isChecked) -> {
            keepScreen.keep(isChecked);
            configuration.saveKeepScreen(isChecked);
        });

        TextView textView = (TextView) view.findViewById(R.id.ok);
        textView.setOnClickListener(v -> dismiss());
    }
}
