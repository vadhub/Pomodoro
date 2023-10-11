package com.vad.pomodoro.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.vad.pomodoro.CheckOnService;
import com.vad.pomodoro.KeepScreen;
import com.vad.pomodoro.R;
import com.vad.pomodoro.TikTakHandler;
import com.vad.pomodoro.model.SaveConfiguration;

public class OptionDialogFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    private CheckOnService consumer;
    private KeepScreen keepScreen;
    private TikTakHandler tikTakHandler;

    private SaveConfiguration configuration;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch aSwitchService;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch aSwitchScreen;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch aSwitchTik;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        consumer = (CheckOnService) context;
        keepScreen = (KeepScreen) context;
        tikTakHandler = (TikTakHandler) context;
        configuration = new SaveConfiguration(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_switch, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        CardView pomodoro = view.findViewById(R.id.pomodoroCard);
        CardView shortBreak = view.findViewById(R.id.shortBreak);
        CardView longBreak = view.findViewById(R.id.longBreak);

        pomodoro.setOnClickListener(v -> openDialog(getResources().getString(R.string.pomodoro)));
        shortBreak.setOnClickListener(v -> openDialog(getResources().getString(R.string.short_break)));
        longBreak.setOnClickListener(v -> openDialog(getResources().getString(R.string.long_break)));

        aSwitchService = view.findViewById(R.id.switchService);
        aSwitchScreen = view.findViewById(R.id.switchScreen);
        aSwitchTik = view.findViewById(R.id.switchTikTak);

        aSwitchScreen.setChecked(configuration.getKeepScreen());
        aSwitchService.setChecked(configuration.getShowNotification());
        aSwitchTik.setChecked(configuration.geSoundTikTak());

        aSwitchService.setOnCheckedChangeListener(this);
        aSwitchScreen.setOnCheckedChangeListener(this);
        aSwitchTik.setOnCheckedChangeListener(this);

    }

    private void openDialog(String currentSetting) {
        Bundle bundle = new Bundle();
        bundle.putString("current_setting", currentSetting);

        PomodoroSettingsDialog dialogFragment = new PomodoroSettingsDialog();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "settings_pomodoro_fragment");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (aSwitchService.equals(buttonView)) {
            consumer.accept(isChecked);
            configuration.saveShowNotification(isChecked);
        } else if (aSwitchScreen.equals(buttonView)) {
            keepScreen.keep(isChecked);
            configuration.saveKeepScreen(isChecked);
        } else if (aSwitchTik.equals(buttonView)) {
            tikTakHandler.onSwitch(isChecked);
            configuration.saveSoundTikTak(isChecked);
        }
    }
}
