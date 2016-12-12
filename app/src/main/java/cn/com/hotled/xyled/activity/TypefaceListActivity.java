package cn.com.hotled.xyled.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.TypefaceAdapter;
import cn.com.hotled.xyled.bean.TypefaceFile;
import cn.com.hotled.xyled.decoration.WifiItemDecoration;

public class TypefaceListActivity extends BaseActivity {

    private ListView typeFaceListView;
    private int oldPosition;
    private TypefaceAdapter typefaceAdapter;
    private RecyclerView typefaceRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.typeface_list);
        initView();
    }

    private void initView() {
        File file =new File("/system/fonts");
        File[] files = file.listFiles();
        final List<TypefaceFile> fileList=new ArrayList<>();
        for (int i=0;i<files.length;i++){
            String name = files[i].getName();
            if(name.contains("-Regular")&&!name.contains("MiuiEx")){
                fileList.add(new TypefaceFile(files[i],false));
            }
        }
        oldPosition = -1;


        typefaceRecycler = (RecyclerView) findViewById(R.id.typeFaceListView);
        typefaceAdapter = new TypefaceAdapter(fileList, this);
        typefaceRecycler.setLayoutManager(new LinearLayoutManager(this));
        Log.i("TypefaceListActivity","fileList.size:"+fileList.size());

        typefaceRecycler.setAdapter(typefaceAdapter);
        typefaceRecycler.addItemDecoration(new WifiItemDecoration(this,WifiItemDecoration.VERTICAL_LIST));
        typefaceAdapter.setOnItemClickListener(new TypefaceAdapter.OnItemOnClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (oldPosition!=-1){
                    boolean selected = fileList.get(oldPosition).isSelected();
                    fileList.get(oldPosition).setSelected(!selected);//取反操作
                    if (oldPosition!=position){
                        //两次点击的是不同的两个item
                        oldPosition =position;
                        fileList.get(position).setSelected(true);
                        typefaceAdapter.notifyItemChanged(oldPosition);
                        typefaceAdapter.notifyItemChanged(position);
                    }else {
                        typefaceAdapter.notifyItemChanged(oldPosition);
                        return;
                    }
                }
//                typefaceAdapter.notifyDataSetChanged();
            }
        });
    }
}
