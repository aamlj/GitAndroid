package com.mjones.flag_quiz55;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class playerName extends MainActivity {

    private TextView resultText;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // components from main.xml
        Button button = (Button) findViewById(R.id.button);
        resultText = (TextView) findViewById(R.id.result);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                showInputDialog();
            }
        });
    }

    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(playerName.this);
        View promptView = layoutInflater.inflate(R.layout.inputdialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(playerName.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        /* setup a dialog window */
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resultText.setText("Hello, " + editText.getText());
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
