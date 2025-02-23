package com.example.iptt;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import com.google.firebase.database.*;
import com.google.firebase.FirebaseApp;
import java.io.IOException;

public class HomePageActivity extends AppCompatActivity {

    private static final String TAG = "HomePageActivity"; // Debugging Tag

    // Declare views
    private Button centerButton;
    private ImageView soundIcon;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Log.d(TAG, "✅ HomePageActivity started");

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Initialize views
        centerButton = findViewById(R.id.playAudioButton);
        soundIcon = findViewById(R.id.sound_icon);

        // ✅ Play audio ONLY when the button is clicked
        centerButton.setOnClickListener(v -> {
            Log.d(TAG, "🎵 Center Button Clicked - Fetching audio URL");
            fetchAndPlayAudio();
        });

        // Set up other click listeners
        setClickListeners();
    }

    private void setClickListeners() {
        soundIcon.setOnClickListener(v -> Log.d(TAG, "🔊 Sound Icon Clicked"));
    }

    // ✅ Fetch Audio URL from Firebase and Play Audio
    private void fetchAndPlayAudio() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("audioFiles/file1/url");
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String audioUrl = dataSnapshot.getValue(String.class);
                    Log.d(TAG, "🎧 Audio URL received: " + audioUrl);
                    playAudio(audioUrl);
                } else {
                    Log.e(TAG, "⚠ No audio URL found in Firebase.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "❌ Failed to load URL", databaseError.toException());
            }
        });
    }

    // ✅ Play Audio from URL
    private void playAudio(String url) {
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release previous MediaPlayer to prevent memory leaks
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                Log.d(TAG, "🎧 Playing Audio");
                mp.start();
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                Log.d(TAG, "✅ Audio Completed");
                mp.release(); // Release MediaPlayer when done
                mediaPlayer = null;
            });

        } catch (IOException e) {
            Log.e(TAG, "❌ Error playing audio", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
