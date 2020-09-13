package com.example.sizzle;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.graphics.Bitmap;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    private static final int pic_id = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCamera = (Button)findViewById(R.id.btnCamera);
        imageView = (ImageView)findViewById(R.id.imageView);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,pic_id);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap photo = (Bitmap)data.getExtras().get("data");
        // not sure what to do with rotation ??
        InputImage image = InputImage.fromBitmap(photo, 0); // Only 0, 90, 180, 270 are supported
        imageView.setImageBitmap(photo);
        String recognizedText = recognizeText(image);
        // DO WHATEVER YOU WANT WITH THIS TEXT!!
    }

    private String recognizeText(InputImage image) {

        TextRecognizer recognizer = TextRecognition.getClient();
        final String[] recognizedText = {""};

        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                // MORE TEXT PROCESSING IF NEEDED
//                                for (Text.TextBlock block : visionText.getTextBlocks()) {
//                                    Rect boundingBox = block.getBoundingBox();
//                                    Point[] cornerPoints = block.getCornerPoints();
//                                    String text = block.getText();
//
//                                    for (Text.Line line: block.getLines()) {
//                                        // ...
//                                        for (Text.Element element: line.getElements()) {
//                                            // ...
//                                        }
//                                    }
//                                }
                                recognizedText[0] = visionText.getText();
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // THROW??
                                    }
                                });
        return recognizedText[0];
    }

    // For string processing in case you feel as if the total text needs to be processed more !
    private void processTextBlock(Text result) {
        String resultText = result.getText();
        for (Text.TextBlock block : result.getTextBlocks()) {
            String blockText = block.getText();
            Point[] blockCornerPoints = block.getCornerPoints();
            Rect blockFrame = block.getBoundingBox();
            for (Text.Line line : block.getLines()) {
                String lineText = line.getText();
                Point[] lineCornerPoints = line.getCornerPoints();
                Rect lineFrame = line.getBoundingBox();
                for (Text.Element element : line.getElements()) {
                    String elementText = element.getText();
                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                }
            }
        }
    }

    // not sure what this function is used for?
    private TextRecognizer getTextRecognizer() {
        TextRecognizer detector = TextRecognition.getClient();
        return detector;
    }

}


