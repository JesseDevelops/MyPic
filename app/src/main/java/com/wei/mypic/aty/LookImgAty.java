package com.wei.mypic.aty;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wei.mypic.R;
import com.wei.mypic.adapter.LookImgAdapter;
import com.wei.mypic.bean.LocalPicBean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * 图片预览页
 */
public class LookImgAty extends AppCompatActivity {

    private int selectedPos;
    private ArrayList<LocalPicBean> localPicBeans;
    private TextView tv_title;
    private TextView tv_finish;
    private TextView tv_old_pic_size;
    private TextView tv_selected;
    private ViewPager viewPager;
    private boolean isShowSize = false;

    //选择图片的总数
    int selectImgNumber;

    /**
     * @param context
     * @param selectedPos     viewpager 当前要显示的位置
     * @param localPicBeans   需要预览的imgList
     * @param selectImgNumber 需要选择的图片的总数
     * @return
     */
    public static Intent getStartIntent(Context context, int selectedPos, int selectImgNumber,
                                        ArrayList<LocalPicBean> localPicBeans) {
        Intent intent = new Intent(context, LookImgAty.class);
        intent.putExtra("selectedPos", selectedPos);
        intent.putExtra("selectImgNumber", selectImgNumber);
        intent.putParcelableArrayListExtra("localPicBeans", localPicBeans);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_look_img);

        selectedPos = getIntent().getIntExtra("selectedPos", 0);
        localPicBeans = getIntent().getParcelableArrayListExtra("localPicBeans");
        selectImgNumber = getIntent().getIntExtra("selectImgNumber", 0);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new LookImgAdapter(this, localPicBeans));
        viewPager.setCurrentItem(selectedPos);
        //从开始显示位置1
        selectedPos = selectedPos + 1;

        tv_title = findViewById(R.id.tv_title);
        tv_title.setText("(" + selectedPos + "/" + localPicBeans.size() + ")");

        //初始是imgList的size 因为进来的都是选中的
        tv_finish = findViewById(R.id.tv_finish);
        handleSelectedSize();
        tv_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<LocalPicBean> lookLocalPicBeans = new ArrayList<>();

                for (int i = 0; i < localPicBeans.size(); i++) {
                    if (localPicBeans.get(i).isSelected()) {
                        lookLocalPicBeans.add(localPicBeans.get(i));
                    }
                }

                if (lookLocalPicBeans.size() > 0) {
                    Log.v("Lin2", "选择图片的大小：" + lookLocalPicBeans.size());
                    Intent intent = new Intent();
                    intent.putParcelableArrayListExtra("list", lookLocalPicBeans);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Log.v("Lin2", "选择图片的大小：为0");
                    Log.v("Lin2", "选择图片的大小：" + lookLocalPicBeans.size());
                    finish();
                }
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                position = position + 1;
                tv_title.setText("(" + position + "/" + localPicBeans.size() + ")");

                showSize();
                handleSelected();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tv_old_pic_size = findViewById(R.id.tv_old_pic_size);
        tv_old_pic_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowSize = !isShowSize;
                showSize();
            }
        });
        showSize();
        tv_selected = findViewById(R.id.tv_selected);
        tv_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (localPicBeans.get(viewPager.getCurrentItem()).isSelected()) {
                    localPicBeans.get(viewPager.getCurrentItem()).setSelected(false);
                    tv_selected.setText("未选中");
                } else {
                    localPicBeans.get(viewPager.getCurrentItem()).setSelected(true);
                    tv_selected.setText("选中了");
                }
                handleSelectedSize();

            }
        });
        handleSelected();

    }

    /**
     * 显示图片的尺寸
     */
    private void showSize() {
        if (isShowSize) {
            String size = new BigDecimal(localPicBeans.get(viewPager.getCurrentItem()).getSize())
                    .divide(new BigDecimal(1048576), 2, RoundingMode.HALF_UP).toString() + "M";
            tv_old_pic_size.setText("原图：" + size);
        } else {
            tv_old_pic_size.setText("原图");
        }
    }

    //处理下方的按钮
    private void handleSelected() {
        if (localPicBeans.get(viewPager.getCurrentItem()).isSelected()) {
            tv_selected.setText("选中了");
        } else {
            tv_selected.setText("未选中");
        }
    }

    //处理已选择的大小
    private void handleSelectedSize() {
        int size = 0;
        for (int i = 0; i < localPicBeans.size(); i++) {
            if (localPicBeans.get(i).isSelected()) {
                size = size + 1;
            }
        }
        tv_finish.setText("完成(" + size + "/" + selectImgNumber + ")");
    }
}
