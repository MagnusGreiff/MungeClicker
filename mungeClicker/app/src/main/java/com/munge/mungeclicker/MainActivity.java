package com.munge.mungeclicker;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    /*
    Mungeclicker is a type of clicker game where the goal is to click on munge and get score.

    Things to add to the game: Shop, more music, more images and better layout.
     */


    //Buttons
    ToggleButton disableSound;
    Button shop, cheatButton, resetButton;
    int currentButtons = 0;

    //Score counter
    int count = 0;

    //Mediaplayer
    ArrayList<MediaPlayer> sounds = new ArrayList<MediaPlayer>();
    MediaPlayer backgroundMusic;

    //Debug string
    public static final String TAG = "Debug:";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Restricts the screen from turning to landscape mode.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        //Load in sound via mediaplayer
        /*
        *
        * Currently not in use. As the app only have 1 music file.
        *
        * */
        Field[] fields = R.raw.class.getFields();
        for (Field f : fields) {
            try {
                sounds.add(MediaPlayer.create(MainActivity.this, f.getInt(null)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        //Creates a mediaplayer with backgroundsound and then start the mediaplayer
        backgroundMusic = MediaPlayer.create(MainActivity.this, R.raw.munge);
        backgroundMusic.start();


        //Add buttons to click on ingame.
        addButtons(currentButtons, currentButtons + 10);

        /*
        *This is the scorecounter that keeps track of the score.
        *It reads from a textfile.
        * */
        count = Integer.parseInt(readFromFile());


        //Reset and disablesound button
        resetButton = (Button) findViewById(R.id.resetButton);
        disableSound = (ToggleButton) findViewById(R.id.disablesound);

        /*
        **Disablesound disables the backgroundmusic.
         */
        disableSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!disableSound.isChecked()) {
                    backgroundMusic.setVolume(0, 0);
                } else {
                    backgroundMusic.setVolume(1, 1);
                }
            }
        });

        /*
        **Resetbutton that resets the score.
         */
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                updateCount();
            }
        });
        updateCount();
    }


    public void updateCount() {
        TextView text = (TextView) findViewById(R.id.textView);
        text.setText("" + count);
    }

    @Override
    protected void onPause() {
        super.onPause();
        writeToFile(count + "");
    }


    /*
    Adds buttons
     */
    public void addButtons(int from, int to) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.buttonLayout);
        layout.removeAllViews();
        /*
        * Adds 10 buttons and sets the cost, pay, image, margin and textview to each button.
        * Cost = How much score you need to be able to press the button.
        * Pay = How much you get when you press the button.
        * Image = Located under R.drawable.
        * */
        for (int i = 0 + from; i < 10 + to; i++) {
            LinearLayout horizontalLayout = new LinearLayout(this);
            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            lp.setMargins(60, 0, 0, 0);

            // button
            ImageButton btn = new ImageButton(getApplicationContext());
            //How much it cost to click the button
            final int cost = (int) (Math.pow(2, i * 0.7) * (int) Math.sqrt(i * 4) + 6) * i;
            //How much you get when clicking the button.
            final int pay = (int) (10 * Math.sqrt(i * 1.12)) + 1;
            //Buttonimage
            btn.setImageResource(R.drawable.munge);

            /*
            When clicking on a button it checks if count => cost and if it is then add pay to count.
             */
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (count >= cost) {
                        count += pay;
                        updateCount();
                    }
                }
            });

            // textview
            TextView text = new TextView(this);
            text.setText("Unlocks @" + cost);

            // add button and textview
            horizontalLayout.addView(btn, lp);
            horizontalLayout.addView(text, lp);

            layout.addView(horizontalLayout);
        }
    }


    //Write score to textfile
    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("save.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }

    }

    //Read score from textfile.
    private String readFromFile() {
        String ret = "";

        try {
            InputStream inputStream = openFileInput("save.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
            try {
                FileOutputStream os = openFileOutput("save.txt", Context.MODE_PRIVATE);
                os.write("0".getBytes());
                os.close();
                ret = readFromFile();
            } catch (Exception e2) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }

        return ret;
    }
}