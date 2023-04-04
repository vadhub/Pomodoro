package com.vad.pomodoro.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.vad.pomodoro.Consumer;
import com.vad.pomodoro.R;

public class OptionDialogFragment extends DialogFragment {

    private Consumer consumer;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        consumer = (Consumer) context;
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
        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            consumer.accept(isChecked);
        });
        TextView textView = (TextView) view.findViewById(R.id.ok);
        textView.setOnClickListener(v -> dismiss());
    }
}
