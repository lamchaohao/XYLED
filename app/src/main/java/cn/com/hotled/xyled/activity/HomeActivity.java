package cn.com.hotled.xyled.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.adapter.TabFragmentAdapter;
import cn.com.hotled.xyled.fragment.AdvanceTextFragment;
import cn.com.hotled.xyled.fragment.BaseFragment;
import cn.com.hotled.xyled.fragment.ImageFragment;
import cn.com.hotled.xyled.fragment.TextFragment;
import cn.com.hotled.xyled.util.MoveTextUtil;
import cn.com.hotled.xyled.util.TcpSend;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    TabLayout tabLayout;
    ViewPager viewPager;
    private ArrayList<Fragment> fragmentList;
    private TextFragment textFragment;
    private ImageFragment imageFragment;
    private List<String> titleList;
    private ImageView mIv_shareRemote;
    private TabFragmentAdapter mAdapter;
    private long firstTime;
    private long lastTime;
    private boolean backFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();

    }



    /**
     * initialize toolbar,Drawer and Navigation
     */
    private void initView() {
        //Toolbar----------------------------------------------
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mIv_shareRemote = (ImageView) toolbar.findViewById(R.id.iv_appbar_home_shareRemote);
        mIv_shareRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseFragment fragment = (BaseFragment) mAdapter.getItem(viewPager.getCurrentItem());
                // TODO: 2016/11/29 临时改为16高
                MoveTextUtil moveUtil =new MoveTextUtil(HomeActivity.this,fragment.getBitmap(),64,32,fragment.getFrameTime(),fragment.getStayTime());
                moveUtil.startGenFile();
            }
        });
        findViewById(R.id.iv_itemManage_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,ItemManageActivity.class));
            }
        });
        //DrawerLayout-------------------------------------------------------------------
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //NavigationView------------------------------------------------------------
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //ViewPager------------------------------------------------
        textFragment = new TextFragment();
        imageFragment = new ImageFragment();
        AdvanceTextFragment atextFragment= new AdvanceTextFragment();
        fragmentList = new ArrayList<>();
        fragmentList.add(atextFragment);
        fragmentList.add(textFragment);
        fragmentList.add(imageFragment);

        titleList = new ArrayList();
        titleList.add("高级文字");
        titleList.add("文字");
        titleList.add("图片");
        viewPager= (ViewPager) findViewById(R.id.vp_home);
        mAdapter = new TabFragmentAdapter(getSupportFragmentManager(),fragmentList,titleList);
        viewPager.setAdapter(mAdapter);
        //Tablayout-------------------------------------------
        tabLayout= (TabLayout) findViewById(R.id.tl_home);
        tabLayout.setupWithViewPager(viewPager,true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if (!backFlag){//第一次点击
            Toast.makeText(this, "再按一次返回退出", Toast.LENGTH_SHORT).show();
            firstTime = System.currentTimeMillis();
            backFlag=true;
        }else{
            //第二次点击
            lastTime = System.currentTimeMillis();
            long gapTime=lastTime-firstTime;

            if (gapTime<2000){
               super.onBackPressed();
            }else{
                //防止时间过长再次点击没反应
                Toast.makeText(this, "再按一次返回退出", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(this,SocketActivity.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            sendFile();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendFile() {
        final File file = new File(Environment.getExternalStorageDirectory()+"/amap/COLOR_01.PRG");
        View view = LayoutInflater.from(this).inflate(R.layout.tcp_send, null);
        final EditText et_tcpIp = (EditText) view.findViewById(R.id.et_tcpIp);
        final EditText et_tcpPort = (EditText) view.findViewById(R.id.et_tcpPort);
        et_tcpIp.setText("192.168.1.101");
        et_tcpPort.setText("10010");
        new AlertDialog.Builder(this)
                .setTitle("设置服务器IP与端口")
                .setView(view)
                .setPositiveButton("发送文件", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tcpIP = et_tcpIp.getText().toString();
                        String tcpPort = et_tcpPort.getText().toString();
                        TcpSend tcpSend=new TcpSend(HomeActivity.this,tcpIP,Integer.parseInt(tcpPort),file);
                        tcpSend.send();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancle",null)
                .show();

    }



}
