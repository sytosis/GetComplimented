package com.example.getcomplimented;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class EnterBuddyName extends Fragment {
    EditText buddyName;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.enter_buddy_name, container, false);

    }
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        buddyName = view.findViewById(R.id.buddy_name);
        Button enterButton = view.findViewById(R.id.button_enter);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if a name is set (first launch)
                SharedPreferences sharedPreferences = Objects.requireNonNull(getActivity()).getSharedPreferences("alarm", MODE_PRIVATE);
                SharedPreferences.Editor buddyNameEditor = sharedPreferences.edit();
                buddyNameEditor.clear();
                buddyNameEditor.putString("name", buddyName.getText().toString());
                buddyNameEditor.apply();

                System.out.println(sharedPreferences.getString("name", null));

                NavHostFragment.findNavController(EnterBuddyName.this)
                        .navigate(R.id.action_EnterBuddyName_to_FirstFragment);

            }
        });


    }


}
