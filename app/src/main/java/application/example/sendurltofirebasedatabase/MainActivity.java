package application.example.sendurltofirebasedatabase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FILE = 1;
    ArrayList<Uri> FileList=new ArrayList<Uri>();
    ProgressDialog progressDialog;
    Button choose,send;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Processing Please Wait.....");
        choose=findViewById(R.id.choose);
        send=findViewById(R.id.upload);

    }

    public void ChooseFiles(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_FILE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PICK_FILE){

            if (resultCode==RESULT_OK){

               if (data.getClipData()!=null){

                   int count =data.getClipData().getItemCount();

                   int i=0;

                   while (i<count){

                       Uri File=data.getClipData().getItemAt(i).getUri();
                       FileList.add(File);
                       i++;


                   }
                   Toast.makeText(this,"You have selected "+FileList.size()+" Files",Toast.LENGTH_LONG).show();
               }
            }
        }
    }


    public void UploadFiles(View view) {
        progressDialog.show();
        Toast.makeText(this,"if it takes time , you can press the button again.",Toast.LENGTH_SHORT).show();
        for (int j=0;j<FileList.size();j++){
            Uri PerFile= FileList.get(j);
            StorageReference folder= FirebaseStorage.getInstance().getReference().child("Files");
            final StorageReference filename=folder.child("file"+PerFile.getLastPathSegment());
            filename.putFile(PerFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(getApplicationContext(),"upload success",Toast.LENGTH_SHORT).show();

                     filename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                         @Override
                         public void onSuccess(Uri uri) {
                            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("user");

                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put("link",String.valueOf(uri));
                             databaseReference.push().setValue(hashMap);
                            progressDialog.dismiss();

                            FileList.clear();


                         }
                     });

                }
            });

        }
    }
}