package cg.skytic.contentprovidertuto1;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button2 = (Button)findViewById(R.id.button2);
        Button button = (Button)findViewById(R.id.button);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddName();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRetrieveStudents();
            }
        });
    }


    private void onClickAddName(){
        ContentValues contentValues = new ContentValues();

        contentValues.put(StudentsProvider.NAME,((EditText)findViewById(R.id.editText2)).getText().toString());

        contentValues.put(StudentsProvider.GRADE,
                ((EditText)findViewById(R.id.editText3)).getText().toString());


        Uri uri = getContentResolver().insert(StudentsProvider.CONTENT_URI,contentValues);
        Toast.makeText(getBaseContext(),
                uri.toString(), Toast.LENGTH_LONG).show();
    }

    private void onClickRetrieveStudents(){
        String URL ="content://"+StudentsProvider.PROVIDER_NAME;

        Uri uriStudent = Uri.parse(URL);
        Cursor c = managedQuery(uriStudent,null,null,null,"name");


        if (c.moveToFirst()) {
            StringBuilder stringBuilder = new StringBuilder();
            do{
                stringBuilder.append(
                       c.getString(c.getColumnIndex(StudentsProvider.ID)) +
                                "\n " +  c.getString(c.getColumnIndex( StudentsProvider.NAME)) +
                                "\n " + c.getString(c.getColumnIndex( StudentsProvider.GRADE)
                               ) +"\n\n");

            } while (c.moveToNext());

            Toast.makeText(this, ""+stringBuilder.toString(), Toast.LENGTH_SHORT).show();
        }
    }

}
