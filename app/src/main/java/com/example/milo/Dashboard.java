package com.example.milo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import io.alterac.blurkit.BlurLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

public class Dashboard extends AppCompatActivity{
    public String token, username;
    String change = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        //contextOfApplication = getApplicationContext();
        locationEnabled();
        SharedPreferences sharedPreferences=getSharedPreferences(SignInActivity.PREFS_NAME, 0);
        username = sharedPreferences.getString("username", "");
        String email = sharedPreferences.getString("email", "");
        String firstname = sharedPreferences.getString("firstname", "");
        String lastname = sharedPreferences.getString("lastname" ,"");
        token = sharedPreferences.getString("token", "");
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        SettingsFragment fragobj = new SettingsFragment();
        fragobj.setArguments(bundle);
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode &
                            Configuration.UI_MODE_NIGHT_MASK;
                    switch (nightModeFlags) {
                        case Configuration.UI_MODE_NIGHT_YES:
                            URL url = new URL("https://source.unsplash.com/user/anuja_2512/likes");
                            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            Dashboard.this.runOnUiThread(new Runnable() {
                                @SuppressLint("Range")
                                @Override
                                public void run() {
                                    ImageView relativeLayout = findViewById(R.id.bgimage);
                                    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                                    relativeLayout.setImageDrawable(new BitmapDrawable(image));
                                    relativeLayout.setVisibility(View.VISIBLE);

                                    //relativeLayout.getBackground().setAlpha(150);
                                    //profiledp.setImageBitmap(image);
                                }
                            });
                            break;

                        case Configuration.UI_MODE_NIGHT_NO:
                            url = new URL("https://source.unsplash.com/user/anuja_2512/likes");
                            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            Dashboard.this.runOnUiThread(new Runnable() {
                                @SuppressLint("Range")
                                @Override
                                public void run() {

                                    ImageView relativeLayout = findViewById(R.id.bgimage);
                                    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                                    relativeLayout.setImageDrawable(new BitmapDrawable(image));
                                    relativeLayout.setVisibility(View.VISIBLE);

                                    //relativeLayout.getBackground().setAlpha(150);
                                    //profiledp.setImageBitmap(image);
                                }
                            });

                            break;

                        case Configuration.UI_MODE_NIGHT_UNDEFINED:
                            ConstraintLayout layoutd = (ConstraintLayout)findViewById(R.id.layout);
                            // Resources layout =getResources();
                            //  layout.(R.drawable.backgrounddark);

                            break;
                    }


                } catch(IOException e) {
                    System.out.println(e);
                }
            }
        });
        thread1.start();

        MeowBottomNavigation bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.add(new MeowBottomNavigation.Model(1, R.drawable.ic_icon_ionic_ios_home));
        bottomNav.add(new MeowBottomNavigation.Model(2, R.drawable.ic_icon_awesome_slack_hash));
        bottomNav.add(new MeowBottomNavigation.Model(3, R.drawable.ic_icon_simple_eventbrite));
        bottomNav.add(new MeowBottomNavigation.Model(4, R.drawable.ic_icon_material_near_me));
        bottomNav.add(new MeowBottomNavigation.Model(5, R.drawable.ic_icon_metro_user));
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1,new HomeFragment()).commit();
        Fragment homefragment = new HomeFragment();
        Fragment hashtagsfragment = new HashtagsFragment();
        Fragment eventsfragment = new EventsFragment();
        Fragment settingsfragment = new SettingsFragment();
        Fragment profilefragment = new ProfileFragment();
        Fragment promoFragment = new PromotionFragment();

        try {
            getSupportFragmentManager().popBackStackImmediate();
        } catch (IllegalStateException ignored) {
            // There's no way to avoid getting this if saveInstanceState has already been called.
        }
        bottomNav.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                switch (item.getId()){
                    case 1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, homefragment).commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, hashtagsfragment).commit();
                        break;
                    case 3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, eventsfragment).commit();
                        break;
                    case 4:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, promoFragment).commit();
                        break;
                    case 5:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, profilefragment).commit();
                        break;

                }
            }
        });


        bottomNav.show(1,true);
        bottomNav.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                //Toast.makeText(Dashboard.this, item.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        bottomNav.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                switch (item.getId()){
                    case 1:

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, homefragment).commitAllowingStateLoss();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, hashtagsfragment).commitAllowingStateLoss();
                        break;
                    case 3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, eventsfragment).commitAllowingStateLoss();
                        break;
                    case 4:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, promoFragment).commitAllowingStateLoss();
                        break;
                    case 5:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, profilefragment).commitAllowingStateLoss();
                        break;

                }

            }
        });



    }


    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setMessage("Do you wish to Logout or Exit?")
                .setCancelable(true)
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory( Intent.CATEGORY_HOME );
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                    }
                })
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences=getSharedPreferences(SignInActivity.PREFS_NAME, 0);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putBoolean("hasLoggedIn", false);
                        editor.commit();
                        finish();
                        startActivity(new Intent(Dashboard.this, SignInActivity.class));
                    }
                })
                .show();
    }

    public String getToken() {
        return token;
    }
    public String getUsername() {
        return username;
    }

    private void locationEnabled () {
        LocationManager lm = (LocationManager)
                getSystemService(Context. LOCATION_SERVICE ) ;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager. NETWORK_PROVIDER ) ;
        } catch (Exception e) {
            e.printStackTrace() ;
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(Dashboard.this )
                    .setMessage( "GPS Enable" )
                    .setCancelable(false)
                    .setPositiveButton( "Settings" , new
                            DialogInterface.OnClickListener() {
                                @Override
                                public void onClick (DialogInterface paramDialogInterface , int paramInt) {
                                    startActivity( new Intent(Settings. ACTION_LOCATION_SOURCE_SETTINGS )) ;
                                }
                            })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(Dashboard.this, "Please enable location to see posts!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show() ;
        }
        else return;
    }
}