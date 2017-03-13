package cn.com.hotled.xyled.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.com.hotled.xyled.R;
import cn.com.hotled.xyled.util.communicate.RemoteSwitchUtil;

import static cn.com.hotled.xyled.global.Global.READ_FAILE;
import static cn.com.hotled.xyled.global.Global.READ_SUCCESS;
import static cn.com.hotled.xyled.global.Global.WIFI_ERRO;


public class RemoteActivity extends BaseActivity {

    @BindView(R.id.iv_remote_previous)
    ImageView mIvRemotePrevious;
    @BindView(R.id.tv_remote_current)
    TextView mTvRemoteCurrent;
    @BindView(R.id.iv_remote_next)
    ImageView mIvRemoteNext;
    @BindView(R.id.gv_programButton)
    GridView mGvProgramButton;
    private String[] mButtonNames;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WIFI_ERRO:
                    Snackbar.make(mGvProgramButton,"未连接屏幕，请查屏",Snackbar.LENGTH_SHORT).show();
                    break;
                case READ_SUCCESS:
                    Snackbar.make(mGvProgramButton,"切换成功",Snackbar.LENGTH_SHORT).show();
                    break;
                case READ_FAILE:
                    Snackbar.make(mGvProgramButton,"屏幕无响应，请重试",Snackbar.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private RemoteSwitchUtil mReadUtil;
    private int mCurrentProgram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mReadUtil = new RemoteSwitchUtil(this,mHandler);
        mButtonNames = new String[20];
        for (int i = 0; i < mButtonNames.length; i++) {
            mButtonNames[i] = i + 1 + "";
        }
        GridView gridView = (GridView) findViewById(R.id.gv_programButton);
        gridView.setAdapter(new ButtonAdapter());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentProgram = position;
                mReadUtil.startWriteData(mCurrentProgram);
                mTvRemoteCurrent.setText(mCurrentProgram+1+"");
            }
        });
    }

    @OnClick({R.id.iv_remote_previous, R.id.iv_remote_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_remote_previous:

                if (mCurrentProgram>0) {
                    mCurrentProgram -= 1;
                }else {
                    mCurrentProgram = 0;
                }
                mReadUtil.startWriteData(mCurrentProgram);
                mTvRemoteCurrent.setText(mCurrentProgram+1+"");
                break;
            case R.id.iv_remote_next:
                if (mCurrentProgram<19) {
                    mCurrentProgram += 1;
                }else {
                    mCurrentProgram = 0;
                }
                mReadUtil.startWriteData(mCurrentProgram);
                mTvRemoteCurrent.setText(mCurrentProgram+1+"");
                break;
        }
    }

    class ButtonAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mButtonNames.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View gridView = null;
            ViewHolder viewHolder = null;
            if (convertView == null) {
                gridView = inflater.from(RemoteActivity.this).inflate(R.layout.content_remote_gridview, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.programTextView = (TextView) gridView.findViewById(R.id.textview);
                gridView.setTag(viewHolder);
            } else {
                gridView = convertView;
                viewHolder = (ViewHolder) gridView.getTag();
            }

            viewHolder.programTextView.setText(mButtonNames[position]);

            return gridView;
        }
    }

    class ViewHolder {
        TextView programTextView;
    }

}
