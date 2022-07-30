package com.hackerivanovich.nonbanappmarket;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.protobuf.StringValue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppActivity extends AppCompatActivity {

    private Button install;
    private TextView name, company, like, dislike, downloads;
    private CircleImageView logo;
    private ImageView preview;
    private Button cancel;
    private Button open;
    private DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    StorageReference ref;

    String ex_uid, ex_id, ex_like, ex_dislike, ex_down, ex_pack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        reference = FirebaseDatabase.getInstance().getReference("Apps");

        init();
        Intent intent = getIntent();
        ex_uid = getIntent().getStringExtra("uid");
        String ex_name = intent.getStringExtra("name");
        String ex_company = intent.getStringExtra("company");
        ex_like = intent.getStringExtra("likes");
        ex_dislike = intent.getStringExtra("dislikes");
        String ex_logo = intent.getStringExtra("logo");
        String ex_link = intent.getStringExtra("link");
        String ex_preview = intent.getStringExtra("preview");
        ex_down = intent.getStringExtra("downloads");
        ex_pack = intent.getStringExtra("package");
        ex_id = intent.getStringExtra("id");

        name.setText(ex_name);
        company.setText(ex_company);
        like.setText(ex_like);
        dislike.setText(ex_dislike);
        downloads.setText(ex_down);

        Glide.with(this)
                .load(ex_logo)
                .into(logo);
        Glide.with(this)
                .load(ex_preview)
                .into(preview);

        install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    download(ex_link, ex_name);
                } else {
                    requestPermission();
                }
            }
        });
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApp(AppActivity.this, ex_pack);
            }
        });
        if (isPackageInstalled(ex_pack, this.getPackageManager())) {
            install.setVisibility(View.INVISIBLE);
            open.setVisibility(View.VISIBLE);
        }
    }

    private void download(String ex_link, String name) {
        storageReference = firebaseStorage.getInstance().getReference();
        ref = storageReference.child("apk/" + ex_link);

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                downloadFiles(AppActivity.this, url, ex_link);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void downloadFiles(final Activity activity, final String url, final String fileName) {
        Uri uri = Uri.parse(url);
        activity.registerReceiver(attachmentDownloadCompleteReceive, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(fileName);
        request.setDescription("Downloading file..");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        DownloadManager dm = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        Log.d("ap", "reqe");
        dm.enqueue(request);
    }

    BroadcastReceiver attachmentDownloadCompleteReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                long downloadId = intent.getLongExtra(
                        DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                Log.d("ap", "OOOOOOKKKKKKK");
                openDownloadedAttachment(context, downloadId);
            }
        }
    };

    private void openDownloadedAttachment(final Context context, final long downloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int downloadStatus = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
            String downloadLocalUri = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
            String downloadMimeType = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE));
            if ((downloadStatus == DownloadManager.STATUS_SUCCESSFUL) && downloadLocalUri != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    openDownloadedAttachment(context, Uri.parse(downloadLocalUri), downloadMimeType);
                }
            }
        }
        cursor.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void openDownloadedAttachment(final Context context, Uri attachmentUri, final String attachmentMimeType) {
        if(attachmentUri!=null) {
            // Get Content Uri.
            if (ContentResolver.SCHEME_FILE.equals(attachmentUri.getScheme())) {
                // FileUri - Convert it to contentUri.
                File file = new File(attachmentUri.getPath());
                attachmentUri = FileProvider.getUriForFile(context, "com.hackerivanovich.nonbanappmarket", file);;
            }

            if (checkAPKPermission()) {
                    startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            Uri.parse("package:com.hackerivanovich.nonbanappmarket")));
            }

            Intent intent = new Intent(context, AppActivity.class);

            Intent openAttachmentIntent = new Intent(Intent.ACTION_VIEW);
            openAttachmentIntent.setDataAndType(attachmentUri, attachmentMimeType);
            openAttachmentIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
            openAttachmentIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(openAttachmentIntent, 25);
        }
    }

    private void init() {
        install = findViewById(R.id.install_button);
        name = findViewById(R.id.name_app);
        company = findViewById(R.id.company_app);
        like = findViewById(R.id.like_app);
        dislike = findViewById(R.id.dislike_app);
        downloads = findViewById(R.id.download_app);
        logo = findViewById(R.id.logo_app);
        preview = findViewById(R.id.preview_app);
        cancel = findViewById(R.id.cancel_button2);
        open = findViewById(R.id.open);

    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(AppActivity.this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

        } else {
            ActivityCompat.requestPermissions(AppActivity.this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(AppActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean checkAPKPermission() {
        int result = ContextCompat.checkSelfPermission(AppActivity.this, Manifest.permission.REQUEST_INSTALL_PACKAGES);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        try {
            Intent i = manager.getLaunchIntentForPackage(packageName);
            if (i == null) {
                return false;
                //throw new ActivityNotFoundException();
            }
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            context.startActivity(i);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 25) {
            PackageManager pm = getApplicationContext().getPackageManager();
            if (isPackageInstalled(ex_pack, pm)) {
                open.setVisibility(View.VISIBLE);
                install.setVisibility(View.INVISIBLE);
                reference = FirebaseDatabase.getInstance().getReference();
            }
        }
    }
}