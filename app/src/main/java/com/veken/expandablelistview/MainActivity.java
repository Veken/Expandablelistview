package com.veken.expandablelistview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Veken
 */
public class MainActivity extends AppCompatActivity {
    private ExpandableListView expandablelistview;
    private TimePickerDialog timePickerDialog;
    private MyExpandableListViewAdapter adapter;

    private Map<String, List<String>> dataset = new HashMap<>();
    private String[] parentList;
    private List<String> childrenList = new ArrayList<>();
    public String TAG = this.getClass().getSimpleName();

    private int currentMonth;
    private int currentYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expandablelistview = (ExpandableListView) findViewById(R.id.expandablelistview);
        initData();
        initView();
    }

    private void initData() {
        Calendar cal = Calendar.getInstance();
        currentMonth = cal.get(Calendar.MONTH) + 1;
        currentYear = cal.get(Calendar.YEAR);
        parentList = new String[currentMonth];
        for (int i = 0; i < currentMonth; i++) {
            parentList[i] = currentYear + "年" + (currentMonth - i) + "月";
        }
        for (int i = 0; i < 6; i++) {
            childrenList.add(i + "");
        }
        for (int i = 0; i < parentList.length; i++) {
            dataset.put(parentList[i], childrenList);
        }
    }

    private void initView() {
        adapter = new MyExpandableListViewAdapter();
        expandablelistview.setAdapter(adapter);
        //数据的展开
        for (int i = 0; i < dataset.size(); i++) {
            expandablelistview.expandGroup(i);
        }
        expandablelistview.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
    }


    private void showTimeDialog() {
        timePickerDialog = new TimePickerDialog();
        timePickerDialog.setOnDialogClickListener(new TimePickerDialog.OnDialogClickListener() {
            @Override
            public void onBtnClick(String year, String month, String data) {
                childrenList.clear();
                dataset.clear();
                if (year.equals("最近三个月")) {
                    parentList = new String[3];
                    for (int i = 0; i < parentList.length; i++) {
                        parentList[i] = currentYear + "年" + (currentMonth - i) + "月";
                    }
                    for (int i = 0; i < 6; i++) {
                        childrenList.add(i + "");
                    }
                    for (int i = 0; i < parentList.length; i++) {
                        dataset.put(parentList[i], childrenList);
                    }
                } else {
                    parentList = new String[1];
                    parentList[0] = year + "年" + month + "月";
                    if (year.equals((currentYear - 1) + "")) {
                        for (int i = 0; i < parentList.length; i++) {
                            dataset.put(parentList[i], null);
                            Toast.makeText(MainActivity.this, "该月没有数据", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        for (int i = 0; i < 6; i++) {
                            childrenList.add(i + "");
                        }
                        for (int i = 0; i < parentList.length; i++) {
                            dataset.put(parentList[i], childrenList);
                        }
                    }
                }
                for (int i = 0; i < parentList.length; i++) {
                    adapter.refresh(expandablelistview, i);
                }
                Log.d("dataset", dataset.toString());
                timePickerDialog.dismiss();
            }
        });
        timePickerDialog.show(getSupportFragmentManager(), TAG);
    }

    private class MyExpandableListViewAdapter extends BaseExpandableListAdapter {
        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //更新数据
                notifyDataSetChanged();
                super.handleMessage(msg);
            }
        };


        /*供外界更新数据的方法*/
        public void refresh(ExpandableListView expandableListView, int groupPosition) {
            handler.sendMessage(new Message());
            //必须重新伸缩之后才能更新数据
            expandableListView.collapseGroup(groupPosition);
            expandableListView.expandGroup(groupPosition);
        }

        //  获得某个父项的某个子项
        @Override
        public Object getChild(int parentPos, int childPos) {
            return dataset.get(parentList[parentPos]).get(childPos);
        }

        //  获得父项的数量
        @Override
        public int getGroupCount() {
            if (dataset == null) {
                return 0;
            }
            return dataset.size();
        }

        //  获得某个父项的子项数目
        @Override
        public int getChildrenCount(int parentPos) {
            if (dataset.get(parentList[parentPos]) == null) {
                return 0;
            }
            return dataset.get(parentList[parentPos]).size();
        }

        //  获得某个父项
        @Override
        public Object getGroup(int parentPos) {
            return dataset.get(parentList[parentPos]);

        }

        //  获得某个父项的id
        @Override
        public long getGroupId(int parentPos) {
            return parentPos;
        }

        //  获得某个父项的某个子项的id
        @Override
        public long getChildId(int parentPos, int childPos) {
            return childPos;
        }

        //  按函数的名字来理解应该是是否具有稳定的id，这个函数目前一直都是返回false，没有去改动过
        @Override
        public boolean hasStableIds() {
            return false;
        }

        //  获得父项显示的view
        @Override
        public View getGroupView(int parentPos, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) MainActivity
                        .this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.parent_item, null);
            }
            view.setTag(R.layout.parent_item, parentPos);
            view.setTag(R.layout.child_item, -1);
            TextView tv_time = (TextView) view.findViewById(R.id.tv_detail_item_time);
            //当parenList.length==1的时候，要提防parentList[parentPos]数组下标越界，
            // parentPos滑动的时候，有可能为1，所以当只有一条数据的时候，直接得到下标为0的值
            if (parentList.length == 1) {
                tv_time.setText(parentList[0]);
            } else {
                tv_time.setText(parentList[parentPos]);
            }
//            Log.d("tv_time",parentList[parentPos]);
            //是否添加item之间的空格
//            View view1 = view.findViewById(R.id.view_bg);
            TextView tx_choice = (TextView) view.findViewById(R.id.tv_choice);
            if (parentPos == 0) {
//                view1.setVisibility(View.GONE);
                tx_choice.setVisibility(View.VISIBLE);
                tx_choice.setText("筛选");
                //筛选按钮时间弹出框
                tx_choice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showTimeDialog();
                    }
                });
            } else {
                tx_choice.setVisibility(View.GONE);
//                view1.setVisibility(View.VISIBLE);
            }
            return view;
        }

        //  获得子项显示的view
        @Override
        public View getChildView(int parentPos, int childPos, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) MainActivity
                        .this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.child_item, null);
            }
            view.setTag(R.layout.parent_item, parentPos);
            view.setTag(R.layout.child_item, childPos);
            TextView text = (TextView) view.findViewById(R.id.tv_child);
            //当parenList.length==1的时候，要提防parentList[parentPos]数组下标越界，
            // parentPos滑动的时候，有可能为1，所以当只有一条数据的时候，直接得到下标为0的值
            if (parentList.length == 1) {
                text.setText(parentList[0] + dataset.get(parentList[0]).get(childPos) + "");
            } else {
                text.setText(parentList[parentPos] + dataset.get(parentList[parentPos]).get(childPos) + "");
            }

            return view;
        }

        //  子项是否可选中，如果需要设置子项的点击事件，需要返回true
        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
    }

}
