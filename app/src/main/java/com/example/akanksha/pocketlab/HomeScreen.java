package com.example.akanksha.pocketlab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.concurrent.ExecutionException;
import java.util.ArrayList;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class HomeScreen extends ActionBarActivity {
    Button newExpButton;
    Button existingExpButton;
    Activity mSelf = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        newExpButton = (Button) findViewById(R.id.new_experiment_button);
        existingExpButton = (Button) findViewById(R.id.exisiting_experiment_button);

        /*newExpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homeScreenSelf, NewExperiment.class);
                startActivity(intent);
            }
        });*/
        newExpButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // from http://www.androidsnippets.com/prompt-user-input-with-an-alertdialog
                final AlertDialog.Builder alert = new AlertDialog.Builder(mSelf);

                //alert.setTitle("Title");
                alert.setTitle("Enter a name for your new experiment");
                alert.setMessage("");

                // Set an EditText view to get user input
                final EditText input = new EditText(mSelf);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton)
                    {

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
                        String exp = input.getText().toString();
                        boolean success = false;

                        if(exp.length() >= 40) {
                            dialog.setMessage("Too long, must not exceed 40 characters");
                        }
                        if (exp.equals("")) {
                            dialog.setMessage("Experiment must have a name");
                        }
                        else{
                            NewExperimentSQL s = new NewExperimentSQL();
                            s.execute(MainActivity.currentUser, exp);
                            String result = null;
                            try {
                                result = s.get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }

                            if (result.equals("Works")) {
                                dialog.dismiss();
                                AlertDialog.Builder dialog2 = new AlertDialog.Builder(mSelf);
                                //alert.setTitle("Title");
                                dialog2.setTitle("Made new experiment \"" + exp + "\"!");

                                dialog2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Intent intent = new Intent(mSelf, NewExperiment.class);
                                        startActivity(intent);
                                    }
                                });

                                dialog2.show();
                            }
                        }
                    }
                });
            }
        });

        existingExpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CharSequence colors[] = new CharSequence[] {"red", "green", "blue", "black","a","b","c","d","e","f","g","h","i","j","k"};
                ExistingExpSQL s = new ExistingExpSQL();
                s.execute("SELECT * from expdata WHERE username=\'" + MainActivity.currentUser + "\'");
                ArrayList<String> experiments = new ArrayList<String>();
                try {
                    experiments = s.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                ArrayList<String> expnames = new ArrayList<String>();
                ArrayList<String> exptimes = new ArrayList<String>();
                ArrayList<String> clickablenames = new ArrayList<String>();
                for (int i = 0; i < experiments.size(); i++){
                    if (i%2 == 0){
                        expnames.add(experiments.get(i));
                    }
                    else{
                        exptimes.add(experiments.get(i));
                    }
                }
                for (int i = 0; i < expnames.size(); i++){
                    long templong = Long.parseLong(exptimes.get(i));
                    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    Date expdate = new Date(templong);
                    String reportDate = df.format(expdate);
                    clickablenames.add(expnames.get(i)+"     "+reportDate);
                }
                final ArrayList<String> exptimes2 = exptimes;
                CharSequence[] charclick = clickablenames.toArray(new CharSequence[clickablenames.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(mSelf);
                builder.setTitle("Choose an existing experiment");
                builder.setItems(charclick, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        MainActivity.exptime = Long.parseLong(exptimes2.get(which));
                        Intent intent = new Intent(mSelf, NewExperiment.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
