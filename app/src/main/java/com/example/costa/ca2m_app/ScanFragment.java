package com.example.costa.ca2m_app;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Costa on 06/06/2017.
 */

/**
  * PJDCC - Summary for class responsabilities.
  *
  * @author Costantino Mele <costantino9612@gmail.com>
  * @since 1.0
  * @version 1.0 Initial Version
  */
public class ScanFragment extends Fragment implements View.OnClickListener, TextToSpeech.OnInitListener {
    private TextToSpeech txts;
    private static final String TAG = "TextToSpeechDemo";
    private static final int MY_DATA_CHECK_CODE = 1234;

    //Creazione di una nuova istanza di ScanFragment
    public static ScanFragment newInstance() {
        ScanFragment fragment = new ScanFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan, container, false);

        ImageButton btnAdd = (ImageButton) view.findViewById(R.id.speak);
        btnAdd.setOnClickListener(this);

        // Fire off an intent to check if a TTS engine is installed
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE)
        {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
            {
                // success, create the TTS instance
                txts = new TextToSpeech(getActivity(), this);
                txts.setLanguage(Locale.ITALIAN);
            }
            else
            {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (txts != null)
        {
            txts.stop();
            txts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        TextView txt = (TextView) getView().findViewById(R.id.descr_descrizione);
        ImageButton btnAdd = (ImageButton) getView().findViewById(R.id.speak);

        if (txts.isSpeaking()) {
            btnAdd.setImageResource(R.drawable.ic_silenzio);
            txts.stop();
        }
        else {
            btnAdd.setImageResource(R.drawable.ic_audio);
            txts.speak(txt.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            txts.setLanguage(Locale.ITALIAN);
        } else {
            Log.e(TAG, "Could not initialize TextToSpeech.");
        }
    }

}
