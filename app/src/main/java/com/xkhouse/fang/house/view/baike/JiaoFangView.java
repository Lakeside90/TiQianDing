package com.xkhouse.fang.house.view.baike;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.app.activity.JJFreeServiceActivity;
import com.xkhouse.fang.app.util.DisplayUtil;
import com.xkhouse.frame.log.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 购房百科--交房（请原谅我名字取得这么low）
 * Created by wujian on 2016/3/1.
 */
public class JiaoFangView implements View.OnClickListener{

    private Context mContext;

    private View rootView;

    //验房
    private ViewPager yanfang_viewpager;
    private LinearLayout yanfang_point_lay;
    private TextView yanfang_txt;
    private List<ImageView> pointViews_yf;

    private String[] yanfang_titile = {
            "验房程序",
            "验房流程及工具",
            "验房细节"};

    private String[] yanfang_content = {
            "一、查看两书一表：《住宅质量保证书》、《住宅使用说明书》、《竣工验收备案表》；面积实测数据对照购房合同上的面积。<br/>" +
                    "二、不交费、不签字、先验房：发现问题后更不能交费、签字。应先修房，同时取证、为日后举证奠定基础，" +
                    "特别保存好验房问题备案单。核查房屋总面积超出或减少百分之三以内的情况很普遍；套内面积减少、公摊面积增加的情况更多。<br/>" +
                    "三、房屋完好：签业主公约、前期物业管理合同时，看好条款，不能放弃自己的权利。<br/>" +
                    "四、交合理费用：（包括：物业管理费应有物价局批文、装修押金、垃圾清运费)",
            "一、先看外部：外立面、外墙瓷砖和涂料、单元门、楼道。<br/>" +
                    "二、再查内部：入户门、门、窗、天棚、墙面、地面、墙阴阳角、墙砖、地砖、上下水、防水存水、" +
                    "暖气、煤气、通风、采光、排烟 、强弱电配电箱、强电: 排气插座、开关、照明灯；" +
                    "弱电:可视对讲、呼叫报警、电话、宽带、有线电视。<br/>" +
                    "三、后测相邻：闭存水试验、水表空转等问题必须和楼上楼下邻居配合。<br/>" +
                    "四、验房工具：量具： 盒尺、 直角尺、 丁字尺、直尺；电钳工具：插排、万用表、摇表、" +
                    "多用螺丝刀、测电笔、手锤、灯；辅助工具：镜子、手电、纸笔；验房专用工具：JZC-2 型垂直检测尺、" +
                    "多功能内外直角检测尺、对角检测尺、水电检测锤、活动响鼓锤、钢针小锤等。",
            "一、空气检测：首先，业主必须确定开发商购买的材料符合国家标准，有害物质不超标；其次，根据国家标准，" +
                    "工程竣工以后，开发商要请权威机构进行室内环境检测，并且必须向购房者出具检测报告。<br/>" +
                    "二、电路：购房者可以向所在小区的物业管理公司或开发商索要基本的水、电等隐蔽工程布局的竣工图纸。" +
                    "还要注意观察配电箱的漏电保护开关是否有照明；普通插座、大功率插座等有明确的分路。<br/>" +
                    "三、吊顶：普通业主从吊顶的观察口可以肉眼判断，按照相关规定，" +
                    "隐蔽工程吊顶必须使用轻钢龙骨，其性能非常稳定，使用寿命也相对较长。<br/>" +
                    "四、防水：在卫生间倒清水试验一下，查验下水是否通畅。如最低点的地漏不能有积水，各下水处应该流水通畅。"};


    //税费
    private ViewPager shuifei_viewpager;
    private LinearLayout shuifei_point_lay;
    private TextView shuifei_txt;
    private List<ImageView> pointViews_sf;

    private String[] shuifei_titile = {
            "契税",
            "维修基金",
            "其他税费"};

    private String[] shuifei_content = {
            "试用范围：商品住房以房地产主管部门契约签证时间为准，存量住房以房地产主管部门契约登记受理时间为准。<br/>" +
            "证明开具：家庭唯一住房证明到行政服务中心契税窗口开具；外地非农户口的还应该持有户口所在地房管部门出具的无房证明。<br/>" +
            "征收标准：<br/>" +
            "首套房：普通住宅90㎡以下1%，90㎡以上1.5%<br>" +
            "二套房：普通住宅90㎡以下1%，90㎡以上2%<br/>",
            "指住宅物业的业主为了本物业区域内公共部位和共用设施、设备的维修养护事项而缴纳一定标准的钱款至专项账户" +
                    "，并授权业主委员会统一管理和使用基金。只有购买新建住宅物业才需要缴纳，二手房不需要交。<br/>" +
            "征收标准：<br/>" +
            "多层1%；高层、小高层2%<br/>",
            "1、印花税：住宅免征，只对商业性质房源收取，税率为万分之五。<br/>" +
             "2、交易过户手续费和登记工本费：对二手房交易征收。<br/>"+
             "3、营业税:２年内成交价×5%；２年以上普通住宅不征收。<br/>"+
             "4、城建税：营业税的7%"+"5.教育费附加税:营业税的3%"};




    private TextView zhuyi_txt;
    private String zhuyi = "注：以房子交付为准，交付完就可缴契税，拿着契税单子即可去办房产证；" +
            "如果手头没钱也可以先缓一缓，但是，拖延缴纳契税存在2大风险。<br/>" +
            "1、如果再买房，名下两套房都会按照二套房的标准来征收契税<br/>" +
            "2、未来卖房时，营业税及个税２年期限以契税完税时间开始计算<br/>"+
            "3、具体征收标准以当地规定为准，以上仅供参考";

    private TextView submit_txt;

    public View getView() {
        return rootView;
    }

    public JiaoFangView(Context context){
        this.mContext = context;
        initView();
        setListener();

        fillYanFangData();
        fillShuiFeiData();
        yanfang_txt.setText(yanfang_titile[0]);
        shuifei_txt.setText(shuifei_titile[0]);
    }

    private void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_jiao_fang, null);

        yanfang_viewpager = (ViewPager) rootView.findViewById(R.id.yanfang_viewpager);
        yanfang_point_lay = (LinearLayout) rootView.findViewById(R.id.yanfang_point_lay);
        yanfang_txt = (TextView) rootView.findViewById(R.id.yanfang_txt);

        shuifei_viewpager = (ViewPager) rootView.findViewById(R.id.shuifei_viewpager);
        shuifei_point_lay = (LinearLayout) rootView.findViewById(R.id.shuifei_point_lay);
        shuifei_txt = (TextView) rootView.findViewById(R.id.shuifei_txt);

        zhuyi_txt = (TextView) rootView.findViewById(R.id.zhuyi_txt);
        submit_txt = (TextView) rootView.findViewById(R.id.submit_txt);

        zhuyi_txt.setText(Html.fromHtml(zhuyi));
    }

    private void setListener(){
        submit_txt.setOnClickListener(this);

        yanfang_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

                for (int i = 0; i < yanfang_titile.length; i++) {
                    if (arg0 == i) {
                        pointViews_yf.get(i).setImageResource(R.drawable.cricle_blue_bg);
                    } else {
                        pointViews_yf.get(i).setImageResource(R.drawable.cricle_dark_bg);
                    }
                }
                yanfang_txt.setText(yanfang_titile[arg0]);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        shuifei_viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

                for (int i = 0; i < shuifei_titile.length; i++) {
                    if (arg0 == i) {
                        pointViews_sf.get(i).setImageResource(R.drawable.cricle_blue_bg);
                    } else {
                        pointViews_sf.get(i).setImageResource(R.drawable.cricle_dark_bg);
                    }
                }
                shuifei_txt.setText(shuifei_titile[arg0]);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }


    private void fillYanFangData(){

        pointViews_yf = new ArrayList<ImageView>();
        yanfang_point_lay.removeAllViews();

        LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(DisplayUtil.dip2px(mContext, 6),
                DisplayUtil.dip2px(mContext, 6));
        lps.leftMargin = DisplayUtil.dip2px(mContext, 3);
        for(int i=0; i < yanfang_titile.length; i++){
            ImageView imageView = new ImageView(mContext);
            imageView.setImageResource(R.drawable.cricle_dark_bg);
            yanfang_point_lay.addView(imageView, lps);
            pointViews_yf.add(imageView);
        }
        if(pointViews_yf.size() > 1){
            pointViews_yf.get(0).setImageResource(R.drawable.cricle_blue_bg);
        }

        List<View> views = new ArrayList<View>();
        for (int i = 0; i < yanfang_titile.length; i++) {

            TextView textView = new TextView(mContext);
            textView.setTextSize(14);
            textView.setTextColor(mContext.getResources().getColor(R.color.c_666666));
            textView.setText(Html.fromHtml(yanfang_content[i]));
            views.add(textView);
        }
        yanfang_viewpager.setAdapter(new ADPagerAdapter(views));
    }


    private void fillShuiFeiData(){

        pointViews_sf = new ArrayList<ImageView>();
        shuifei_point_lay.removeAllViews();

        LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(DisplayUtil.dip2px(mContext, 6),
                DisplayUtil.dip2px(mContext, 6));
        lps.leftMargin = DisplayUtil.dip2px(mContext, 3);
        for(int i=0; i < shuifei_titile.length; i++){
            ImageView imageView = new ImageView(mContext);
            imageView.setImageResource(R.drawable.cricle_dark_bg);
            shuifei_point_lay.addView(imageView, lps);
            pointViews_sf.add(imageView);
        }
        if(pointViews_sf.size() > 1){
            pointViews_sf.get(0).setImageResource(R.drawable.cricle_blue_bg);
        }

        List<View> views = new ArrayList<View>();
        for (int i = 0; i < shuifei_titile.length; i++) {

            TextView textView = new TextView(mContext);
            textView.setTextSize(14);
            textView.setTextColor(mContext.getResources().getColor(R.color.c_666666));
            textView.setText(Html.fromHtml(shuifei_content[i]));
            views.add(textView);
        }
        shuifei_viewpager.setAdapter(new ADPagerAdapter(views));
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_txt:
                mContext.startActivity(new Intent(mContext, JJFreeServiceActivity.class));
                break;
        }
    }





    class ADPagerAdapter extends PagerAdapter {

        private List<View> listViews;// content

        private int size;// 页数

        public ADPagerAdapter(List<View> views) {// 构造函数
            // 初始化viewpager的时候给的一个页面
            this.listViews = views;
            size = views == null ? 0 : views.size();
        }

        public void setData(List<View> views){
            this.listViews = views;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {// 返回数量
            return size;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {// 销毁view对象
            ((ViewPager) arg0).removeView(listViews.get(arg1 % size));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {// 返回view对象
            try {
                ((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);
            } catch (Exception e) {
                Logger.e("", "exception：" + e.getMessage());
            }
            return listViews.get(arg1 % size);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager)container).removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

}
