package com.neoress.drawcanvas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    Button btnLoadImage;
    TextView textSource;
    EditText editTextCaption;
    Button btnProcessing;
    ImageView imageResult;

    final int RQS_IMAGE = 1;

    Uri source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLoadImage    = findViewById(R.id.btn_loadimage);
        textSource      = findViewById(R.id.tv_sourceuri);
        editTextCaption = findViewById(R.id.et_caption);
        btnProcessing   = findViewById(R.id.btn_processing);
        imageResult     = findViewById(R.id.img_result);

        // listen event load an image
        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentPickImage = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentPickImage, RQS_IMAGE);
            }
        });

        btnProcessing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(source != null) {
                    Bitmap processingBitmap = ProcessingBitmap();
                    if(processingBitmap != null) {
                        imageResult.setImageBitmap(processingBitmap);
                        Toast.makeText(getApplicationContext(),
                                "Done",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Something wrong in processing",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("ONRESULT", "vao day roi ne. resultCode: " + resultCode);

        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case RQS_IMAGE:
                    source = data.getData();
                    textSource.setText(source.toString());

                    Log.i("ONRESULT", "vao day roi ne 2. source: " + source.toString());
                    break;
            }
        }
    }

    private Bitmap ProcessingBitmap(){
        Bitmap bm;
        Bitmap newBitmap = null;

        try {
            bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(source));

            Bitmap.Config config = bm.getConfig();
            if(config == null){
                config = Bitmap.Config.ARGB_8888;
            }

            newBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), config);
            Canvas newCanvas = new Canvas(newBitmap);

            newCanvas.drawBitmap(bm, 0, 0, null);

            String captionString = editTextCaption.getText().toString();
            if(captionString != null){

                Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintText.setColor(Color.parseColor("#FFFFFF"));
                paintText.setTextSize(50);
                paintText.setStyle(Paint.Style.FILL);
                paintText.setShadowLayer(10f, 10f, 10f, Color.BLACK);

                Rect rectText = new Rect();
                paintText.getTextBounds(captionString, 0, captionString.length(), rectText);

                newCanvas.drawText(captionString,
                        0, rectText.height(), paintText);

                Toast.makeText(getApplicationContext(),
                        "drawText: " + captionString,
                        Toast.LENGTH_LONG).show();

            }else{
                Toast.makeText(getApplicationContext(),
                        "caption empty!",
                        Toast.LENGTH_LONG).show();
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return newBitmap;
    }
}
