package com.example.sizzle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.net.URLEncoder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
        System.out.println(recognizedText);
        // DO WHATEVER YOU WANT WITH THIS TEXT!!
        String[] output;
        try {
            output = getUrlAndCovidProcedures(recognizedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // USE OUTPUT FOR APP
    }

    private String recognizeText(InputImage image) {

        TextRecognizer recognizer = TextRecognition.getClient();
        final String[] recognizedText = {"No text detected :("};

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


    public static String[] getUrlAndCovidProcedures(String text) throws Exception {

        final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.89 Safari/537.36";
        String[] output = {"",""}; // TO FILL WITH [0] URL and [1] FULL PROCEDURES
        String query = text + "near me yelp";
        final Document page = Jsoup.connect("https://www.google.com/search?q=" + URLEncoder.encode(query, "UTF-8")).userAgent(USER_AGENT).get();
        //Traverse the results
        boolean yelpFinished = false;
        for (Element result : page.select("div > div > div > div > a")){


            final String url = result.attr("href");
            if(!url.contains("google") && !url.contains("search") && !url.contains("#")) {
                //System.out.println(url);
            }
            if(url.contains("yelp.ca") && yelpFinished == false) {
                yelpFinished = true;
                System.out.println(url);
                output[0] = url;
                final Document yelpPage = Jsoup.connect(url).userAgent(USER_AGENT).get();

                Element result2 = yelpPage.select("section").first();

                for (Element result3 : result2.select("div > div > div")) {
                    //System.out.println(result3);
                    String fullProcedures = "";
                    for (Element procedures : result3.select("div > div > span")) {

                        fullProcedures += procedures.text() + " ";

                    }

                    fullProcedures = fullProcedures.replaceAll("\n[ \t]*\n", "\n");
                    System.out.print(fullProcedures);
                    output[1]=fullProcedures;


                }

            }


        }
        return output;
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


