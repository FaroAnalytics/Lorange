package com.faroanalytics.lorange;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class BirthdayActivity extends AppCompatActivity {
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday);

        String name = getIntent().getStringExtra("name");
        String picture = getIntent().getStringExtra("picture");
        phone = getIntent().getStringExtra("phone");


        ImageView imageBox = (ImageView)findViewById(R.id.ivProfilePicture);
        DownloadImage downloadImage = new DownloadImage(imageBox);
        downloadImage.execute(picture);

        TextView nameBox = (TextView)findViewById(R.id.tvBirthdayToday);
        nameBox.setText(name);

        ImageButton sendWhatsapp = (ImageButton)findViewById(R.id.bSendWhatsapp);
        sendWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("smsto:" + phone);
                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                i.setPackage("com.whatsapp");
                startActivity(Intent.createChooser(i, ""));
            }
        });
    }
}

