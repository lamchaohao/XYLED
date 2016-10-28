package cn.com.hotled.xyled.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.TypefaceListAdapter;

public class TypefaceListActivity extends AppCompatActivity {

    private ListView typeFaceListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.typeface_list);
        initView();
    }

    private void initView() {
        File file =new File("/system/fonts");
        if (file.exists()){
            Toast.makeText(this, "exists", Toast.LENGTH_SHORT).show();
        }
        File[] files = file.listFiles();
        for (int i=0;i<files.length;i++){
            Log.i("textFragm",files[i].getName().toString());
        }

        typeFaceListView = (ListView) findViewById(R.id.typeFaceListView);
        typeFaceListView.setAdapter(new TypefaceListAdapter(files,this));
    }
}
