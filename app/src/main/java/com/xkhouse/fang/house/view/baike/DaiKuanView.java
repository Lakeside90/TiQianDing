package com.xkhouse.fang.house.view.baike;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xkhouse.fang.R;
import com.xkhouse.fang.money.activity.XKLoanIndexActivity;

/**
 * 购房百科--贷款（请原谅我名字取得这么low）
 * Created by wujian on 2016/3/1.
 */
public class DaiKuanView implements View.OnClickListener{

    private Context mContext;

    private View rootView;

    private TextView shangye_txt;
    private TextView gongjijin_txt;
    private TextView zuhe_txt;

    private ImageView liucheng_bg;

    private TextView cailiao_txt;
    private TextView zhengming_txt;
    private TextView submit_txt;

    private String cailiao_sy = "贷款预批材料：<br/>" +
            "1、合法的购买住房合同、协议及相关批准文件；<br/>" +
            "2、贷款申请人身份证，户口簿，婚姻证明（验原件收复印件）；<br/>" +
            "3、买方及配偶的收入证明原件；<br/>" +
            "4、卖方的房屋所有权证和国有土地使用权证（验原件收复印件）；<br/>" +
            "5、经办银行认可的有关部门 出具的借款人稳定经济收入证明或其他偿债能力证明资料；<br/>" +
            "6、其他资料（根据各贷款银行的要求提供）。<br/><br/><br/>" +
            "抵押登记材料：<br/>" +
            "1、买方的房屋所有权证；<br/>" +
            "2、买方的国有土地使用权证；<br/>" +
            "3、抵押物或质押权利清单及权属证明文件，有处分权人 出具的同意抵押或质押的证明；<br/>" +
            "4、保证人出具的同意提供担保的书面承诺及保证人的资信证明。";

    private String zhengming_sy = "1、资金监管协议；<br/>" +
            "2、房产开发商营业执照、资质证书、法人代表身份证；<br/>" +
            "3、房产开发商或代理商的收款帐号。";


    private String cailiao_gjj = "贷款预批材料：<br/>" +
            "1、填报《住房公积金贷款申请表》；<br/>" +
            "2、提供申请人及其配偶或房屋产权共有人的身份证、户口簿、婚姻证明；<br/>" +
            "3、买方及配偶的收入证明原件、家庭房产查档证明；4、相应购房证明材料。<br/><br/><br/>" +
            "抵押登记材料：<br/>" +
            "1、买方的房屋所有权证；<br/>" +
            "2、买方的国有土地使用权证；<br/>" +
            "3、 抵押物或质押权利清单及权属证明文件，有处分权人出具的同意抵押或质押 的证明；<br/>" +
            "4、保证人出具的同意提供担保的书面承诺及保证人的资信证明。";

    private String zhengming_gjj = "1、资金监管协议；<br/>" +
            "2、房产开发商营业执照、资质证书、法人代表身份证；<br/>" +
            "3、房产开发商或代理商的收款帐号。";

    private String cailiao_zh = "贷款预批材料：<br/>" +
            "1、合法的购买住房的合同、协议及批准文件；<br/>" +
            "2、提供申请人及其配偶或房屋产权共有人的身份证、户口簿、婚姻证明；<br/>" +
            "3、买方及配偶的收入证明原件家庭房产查档证明；<br/>" +
            "4、借款人用于购买住房的自筹资金的有关证明；<br/>" +
            "5、卖方的房屋所有权证和国有土地使用权证（验原件收复印件）；<br/>" +
            "6、公积金管理部门和贷款行规定的其他文件和资料。<br/><br/><br/>" +
            "抵押登记材料：<br/>" +
            "1、买方的房屋所有权证；<br/>" +
            "2、买方的国有土地使用权证；<br/>" +
            "3、抵押物或质押权利清单及权属证明文件，有处分权人出具的同意抵押或质押的证明；<br/>" +
            "4、保证人出具的同意提供担保的书面承诺及保证人的资信证明。";

    private String zhengming_zh = "1、资金监管协议；<br/>" +
            "2、房产开发商营业执照、资质证书、法人代表身份证；<br/>" +
            "3、房产开发商或代理商的收款帐号。";


    public View getView() {
        return rootView;
    }

    public DaiKuanView(Context context){
        this.mContext = context;
        initView();
        setListener();

        changeView(0);
    }

    private void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_dai_kuan, null);

        shangye_txt = (TextView) rootView.findViewById(R.id.shangye_txt);
        gongjijin_txt = (TextView) rootView.findViewById(R.id.gongjijin_txt);
        zuhe_txt = (TextView) rootView.findViewById(R.id.zuhe_txt);

        liucheng_bg = (ImageView) rootView.findViewById(R.id.liucheng_bg);

        cailiao_txt = (TextView) rootView.findViewById(R.id.cailiao_txt);
        zhengming_txt = (TextView) rootView.findViewById(R.id.zhengming_txt);
        submit_txt = (TextView) rootView.findViewById(R.id.submit_txt);

    }

    private void setListener(){
        shangye_txt.setOnClickListener(this);
        gongjijin_txt.setOnClickListener(this);
        zuhe_txt.setOnClickListener(this);
        submit_txt.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.shangye_txt:
                changeView(0);
                break;

            case R.id.gongjijin_txt:
                changeView(1);
                break;

            case R.id.zuhe_txt:
                changeView(2);
                break;

            case R.id.submit_txt:
                mContext.startActivity(new Intent(mContext, XKLoanIndexActivity.class));
                break;
        }
    }

    private void changeView(int index){
        switch (index){
            case 0:
                shangye_txt.setBackgroundResource(R.drawable.blue_corner_btn_selector);
                shangye_txt.setTextColor(mContext.getResources().getColor(R.color.white));
                gongjijin_txt.setBackgroundResource(R.drawable.blue_border_cricle_btn_bg);
                gongjijin_txt.setTextColor(mContext.getResources().getColor(R.color.common_red));
                zuhe_txt.setBackgroundResource(R.drawable.blue_border_cricle_btn_bg);
                zuhe_txt.setTextColor(mContext.getResources().getColor(R.color.common_red));

                liucheng_bg.setImageResource(R.drawable.daikuan_liuchen);

                cailiao_txt.setText(Html.fromHtml(cailiao_sy));
                zhengming_txt.setText(Html.fromHtml(zhengming_sy));

                break;

            case 1:
                shangye_txt.setBackgroundResource(R.drawable.blue_border_cricle_btn_bg);
                shangye_txt.setTextColor(mContext.getResources().getColor(R.color.common_red));
                gongjijin_txt.setBackgroundResource(R.drawable.blue_corner_btn_selector);
                gongjijin_txt.setTextColor(mContext.getResources().getColor(R.color.white));
                zuhe_txt.setBackgroundResource(R.drawable.blue_border_cricle_btn_bg);
                zuhe_txt.setTextColor(mContext.getResources().getColor(R.color.common_red));

                liucheng_bg.setImageResource(R.drawable.daikuan_liuchen_gjj);

                cailiao_txt.setText(Html.fromHtml(cailiao_gjj));
                zhengming_txt.setText(Html.fromHtml(zhengming_gjj));

                break;

            case 2:
                shangye_txt.setBackgroundResource(R.drawable.blue_border_cricle_btn_bg);
                shangye_txt.setTextColor(mContext.getResources().getColor(R.color.common_red));
                gongjijin_txt.setBackgroundResource(R.drawable.blue_border_cricle_btn_bg);
                gongjijin_txt.setTextColor(mContext.getResources().getColor(R.color.common_red));
                zuhe_txt.setBackgroundResource(R.drawable.blue_corner_btn_selector);
                zuhe_txt.setTextColor(mContext.getResources().getColor(R.color.white));

                liucheng_bg.setImageResource(R.drawable.daikuan_liuchen_zh);

                cailiao_txt.setText(Html.fromHtml(cailiao_zh));
                zhengming_txt.setText(Html.fromHtml(zhengming_zh));
                break;
        }
    }
}
