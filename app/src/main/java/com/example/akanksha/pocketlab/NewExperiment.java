package com.example.akanksha.pocketlab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;


public class NewExperiment extends Activity {
    Button temperatureButton;
    //Button humidityButton;
    Button colorButton;
    Button savecsvButton;
    //Button accelButton;
    Button ambiButton;
    Button homeButton;
    Button variButton;
    TextView titleTextView;

    Button viewdataButton;

    Activity newExpSelf = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_experiment);

        temperatureButton = (Button) findViewById(R.id.temperature_button);
        //humidityButton = (Button) findViewById(R.id.humid_button);
        colorButton = (Button) findViewById(R.id.color_button);
        savecsvButton = (Button) findViewById(R.id.csv_button);
        //accelButton = (Button) findViewById(R.id.accel_button);
        ambiButton = (Button) findViewById(R.id.ambient_button);
        homeButton = (Button) findViewById(R.id.home_button);
        viewdataButton = (Button) findViewById(R.id.view_data_button);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        variButton = (Button) findViewById(R.id.variable_sensor);

        titleTextView.setText(HomeScreen.experimentName);

        temperatureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(newExpSelf, TemperatureSensor.class);
               // Intent intent = new Intent(newExpSelf, TemperatureSensorBoard.class);
                startActivity(intent);
            }
        });
        /*humidityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(newExpSelf, HumiditySensor.class);
                startActivity(intent);
            }
        });*/
        colorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(newExpSelf, ColorSensor.class);
                startActivity(intent);
            }
        });
        savecsvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(newExpSelf);

                alert.setTitle("Are you sure you want to save the following lab?");

                // Set an EditText view to get user input
                TextView expinfo = new TextView(newExpSelf);

                AllInfoSQL s = new AllInfoSQL(MainActivity.exptime);
                ArrayList<String> labdata = new ArrayList<String>();
                try {
                    s.execute(MainActivity.currentUser);
                    labdata = s.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                SpannableString displaystring = displayspan(labdata);

                final ArrayList<String> labdata2 = labdata;

                expinfo.setText(displaystring);
                alert.setView(expinfo);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        //return;
                        //String mycsvstring = csvstring(labdata2);
                        //savetocsv(mycsvstring);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                // from http://stackoverflow.com/questions/2620444/how-to-prevent-a-dialog-from-closing-when-a-button-is-clicked
                final AlertDialog dialog = alert.create();
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                     //put something here

                        String mycsvstring = csvstring(labdata2);
                        savetocsv(mycsvstring);
                        dialog.dismiss();
                        return;
                    }
                });
            }
        });

        variButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(newExpSelf, VariableSensorAct.class);
                startActivity(intent);
            }
        });

        ambiButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(newExpSelf, AmbientSensor.class);
                startActivity(intent);
            }
        });

        homeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(newExpSelf, HomeScreen.class);
                startActivity(intent);
            }
        });

        viewdataButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent(newExpSelf, DisplayExpData.class);
                startActivity(intent);

            }
        });


    }

    public void savetocsv(String dataval){ //saves a string to a csv file
        //get file path
        File path = this.getFilesDir();
        Log.d("DEBUG", path.getAbsolutePath());

        //get time for the name of the csv
        long currenttime = System.currentTimeMillis();
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy-HH-mm-ss");
        Date expdate = new Date(currenttime);
        String reportDate = df.format(expdate);

        //save file name as user plus the saving time
        String filename = MainActivity.currentUser + reportDate + ".csv";
        //File myfile = new File(path,filename);

        /*try {
            FileOutputStream outputstream = openFileOutput("hello", Context.MODE_WORLD_READABLE);
            outputstream.write("hi".getBytes());
            outputstream.close();
            Log.d("DEBUG", "Saving2");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("DEBUG", "not saving");
        }*/

        String imageFileName = "PocketLab_" + reportDate + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

        try {
            File myfile  = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".csv",         /* suffix */
                    storageDir      /* directory */

            );
            FileWriter writer = new FileWriter(myfile);
            writer.write(dataval);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*Toast.makeText(context.getApplicationContext(),
                "Report successfully saved to: " + outputFile.getAbsolutePath(),
                Toast.LENGTH_LONG).show();*/

        /*String ret = "";

        try {
            InputStream inputStream = openFileInput("hello");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("DEBUG", "File not found: " + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("DEBUG", ret);*/


    }

    public String csvstring(ArrayList<String> datafromsql){ //get the string to save to a csv
        //Basic version with just a columnname and data
        String datastring = "";

        for (int i = 0; i < datafromsql.size(); i+=2){
            datastring += datafromsql.get(i) + ",";
        }

        datastring += "\n";

        for (int i = 1; i < datafromsql.size(); i+=2){
            datastring += datafromsql.get(i) + ",";
        }

        return datastring;
    }

    public SpannableString displayspan(ArrayList<String> datafromsql){ //get the string to display

        SpannableString displaystring = new SpannableString("");

        for (int i = 0; i < datafromsql.size(); i++ ){
        //int i=0;

            String tempstr = datafromsql.get(i) + "\n";
            SpannableString tempspan = new SpannableString(tempstr);

            if (i % 2 == 0){ //even, or titles of sections
                tempspan.setSpan(new RelativeSizeSpan(1.25f), 0,tempstr.length() - 1, 0);
                tempspan.setSpan(new ForegroundColorSpan(Color.BLUE), 0, tempstr.length() - 1, 0);
            }

            displaystring = new SpannableString(TextUtils.concat(displaystring,tempspan));

        }

        return displaystring;
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(newExpSelf, HomeScreen.class);
        startActivity(intent);
    }
}
