package com.jaaaelu.gzw.realmdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements EditTextDialogFragment.ChangeContentListener {
    public RealmResults<TestModel> mResults;       //  查询返回的结果集
    public boolean isEditList = false;             //  是否正在编辑
    private ListView mDBList;                       //  用于展示数据的列表
    private View mEditBarView;                      //  删除条目时显示的编辑界面
    private ImageView mChooseDone;                  //  删除条目时显示的对勾
    public TextView mChooseCount;                  //  删除条目时显示的选中条目
    private ImageView mDeleteChoose;                //  删除条目时显示的删除按钮
    private MyAdapter mAdapter;                     //  ListView的适配器
    private TextView mHint;                         //  提示  可无视
    public List<Integer> mDeleteList;              //  选择要删除的条目的下标
    private LinearLayout mEmptyView;                //  空View
    private Realm mRealm;                           //  Realm实例
    private String mCurrEditId;                     //  当前编辑着的条目的id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
        initListener();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mRealm = Realm.getDefaultInstance();
        queryResultAll();
        mDeleteList = new ArrayList<>();
    }

    /**
     * 初始化侦听
     */
    private void initListener() {
        //  点击编辑界面的对勾返回之前界面
        mChooseDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setEditVisibility(false);
                setEditList(false);
            }
        });

        //  删除逻辑
        mDeleteChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Collections.sort(mDeleteList);
                for (int i = mDeleteList.size() - 1; i >= 0; i--) {
                    final int index = mDeleteList.get(i);
                    deleteItemData(index);
                }
                setEditList(false);
                setEditVisibility(false);
                mDeleteList.clear();
                mChooseCount.setText("已选择" + mDeleteList.size() + "个");
            }
        });

        //  为result设置数据改变侦听
        mResults.addChangeListener(new RealmChangeListener<RealmResults<TestModel>>() {
            @Override
            public void onChange(RealmResults<TestModel> element) {
                mResults = element;
                mAdapter.notifyDataSetChanged();
            }
        });

        //  点击条目编辑Title
        mDBList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EditTextDialogFragment fragment = EditTextDialogFragment.newInstance("正在编辑条目Title",
                        mResults.get(i).getTestTitle());
                fragment.setContentListener(MainActivity.this);
                fragment.show(getSupportFragmentManager(), "EditTextDialogFragment");
                mCurrEditId = mResults.get(i).get_id();
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHint = (TextView) findViewById(R.id.tv_hint);
        mEmptyView = (LinearLayout) findViewById(R.id.ll_empty_view);

        mDBList = (ListView) findViewById(R.id.lv_db_list);
        mAdapter = new MyAdapter();
        mDBList.setAdapter(mAdapter);
        mDBList.setEmptyView(mEmptyView);

        mEditBarView = findViewById(R.id.in_edit_bar);
        mChooseDone = (ImageView) mEditBarView.findViewById(R.id.iv_choose_done);
        mChooseCount = (TextView) mEditBarView.findViewById(R.id.tv_choose_count);
        mDeleteChoose = (ImageView) mEditBarView.findViewById(R.id.iv_delete_choose);
    }

    //  是否可见编辑界面，用于编辑界面非编辑界面的切换
    public void setEditVisibility(boolean visibility) {
        mEditBarView.setVisibility(visibility ? View.VISIBLE : View.GONE);
        mHint.setVisibility(visibility ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_db_operation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //  单个menu的点击事件
        switch (item.getItemId()) {
            case R.id.menu_add:
                createItemData();
                return true;
            case R.id.menu_delete:
                setEditVisibility(true);
                setEditList(true);
                return true;
            case R.id.menu_queue:
                EditTextDialogFragment fragment = EditTextDialogFragment.newInstance("请输入要查询的Title",
                        "");
                fragment.setContentListener(MainActivity.this);
                fragment.show(getSupportFragmentManager(), "EditTextDialogFragment");
                mCurrEditId = "";
                return true;
        }
        return false;
    }

    /**
     * 插入假数据 在后台线程进行
     */
    private void createItemData() {
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UUID uuid = UUID.randomUUID();
                TestModel testModel = realm.createObject(TestModel.class, uuid.toString());
                testModel.setTestTitle("点击编辑标题");
                testModel.setUpdateTime(getCurrTime());
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
//                queryResultAll();
                mAdapter.notifyDataSetChanged();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("Realm", "保存失败" + error.getMessage());
            }
        });
    }

    /**
     * 删除数据 在后台线程进行
     */
    private void deleteItemData(final int index) {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mResults.deleteFromRealm(index);
            }
        });
    }

    /**
     * 更新数据 在后台线程进行
     */
    private void updateItemData(final String title) {
        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                TestModel testModel = realm.where(TestModel.class).equalTo("_id", mCurrEditId).findFirst();
                testModel.setUpdateTime(getCurrTime());
                testModel.setTestTitle(title);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                mAdapter.notifyDataSetChanged();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.e("Realm", "保存失败" + error.getMessage());
            }
        });
    }

    /**
     * 根据标题查询数据 在后台线程进行
     */
    private void queryResultByTitle(String title) {
        mResults = mRealm.where(TestModel.class)
                .equalTo("testTitle", title)
                .findAll();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 查询所有数据 在后台线程进行
     */
    private void queryResultAll() {
        mResults = mRealm.where(TestModel.class).findAllAsync();
    }


    /**
     * 获取当前时间
     *
     * @return 格式化之后的时间
     */
    private String getCurrTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    @Override
    public void changeContent(String newContent) {
        if ("".equals(mCurrEditId)) {
            if ("".equals(newContent)) {
                queryResultAll();
            } else {
                queryResultByTitle(newContent);
            }
        } else {
            updateItemData(newContent);
        }
    }


    public void setEditList(boolean isEditList) {
        this.isEditList = isEditList;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (isEditList) {
            setEditVisibility(false);
            setEditList(false);
        } else {
            super.onBackPressed();
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mResults.size();
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
            ItemHolder itemHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test_model, parent, false);
                itemHolder = new ItemHolder(convertView, position);
                convertView.setTag(itemHolder);
            } else {
                itemHolder = (ItemHolder) convertView.getTag();
            }
            itemHolder.isShowCheckBox();
            itemHolder.setData(position);
            return convertView;
        }


        /**
         * Holder
         */
        class ItemHolder {
            private CheckBox delete;
            private TextView title;
            private TextView currNum;
            private TextView updateTime;

            public ItemHolder(View itemView, final int position) {
                delete = (CheckBox) itemView.findViewById(R.id.cb_delete_checkbox);
                title = (TextView) itemView.findViewById(R.id.tv_test_title);
                currNum = (TextView) itemView.findViewById(R.id.tv_curr_num);
                updateTime = (TextView) itemView.findViewById(R.id.tv_test_update_time);
                delete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (isEditList) {
                            if (mDeleteList.contains(position)) {
                                mDeleteList.remove((Integer)position);
                            } else {
                                mDeleteList.add(position);
                            }
                            mChooseCount.setText("已选择" + mDeleteList.size() + "个");
                        }
                    }
                });
            }

            /**
             * 设置数据
             */
            public void setData(int position) {
                currNum.setText(position + 1 + "");
                title.setText(mResults.get(position).getTestTitle());
                updateTime.setText("最后更新时间:  " + mResults.get(position).getUpdateTime());
                delete.setChecked(false);
            }

            /**
             * 是否被选中
             */
            public void isShowCheckBox() {
                delete.setVisibility(isEditList ? View.VISIBLE : View.GONE);
                currNum.setVisibility(isEditList ? View.INVISIBLE : View.VISIBLE);
            }
        }
    }

}