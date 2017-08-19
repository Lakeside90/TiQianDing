package com.xkhouse.fang.house.view.baike;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.xkhouse.fang.R;

/**
 * 购房百科--落户（请原谅我名字取得这么low）
 * Created by wujian on 2016/3/1.
 */
public class LuoHuView implements View.OnClickListener{

    private Context mContext;

    private View rootView;

    private TextView luohu_txt;

    private String luohu = "1 → 申请人常住户口所在地户籍证明或户口簿、身份证(原件和复印件)<br/>" +
            "2 → 随迁配偶、未成年子女的，应当提供《结婚证》(原件和复印件)，" +
            "配偶、未成年子女常住户口所在地户籍证明或户口簿、身份证(原件和复印件)，" +
            " 当地计划生育部门出具的符合计划生育政策证明(或处罚证明)，《出生证》（原件和复印件）<br/>" +
            "3 → 落户申请报告以及《房屋所有权证》(原件和复印件)<br/>" +
            "4 → 购房合同原件原件及复印件（未办房产证者提供）和商品房备案证明（附在购房合同内）<br/>" +
            "5 → 房管部门审核批准的《外地人购买住宅确认表》(第一联申请人收执联)<br/>" +
            "6 → 申请人高中以上文化程度学历证书(原件和复印件)<br/>" +
            "7 → 市级以上医院出具的无传染疾病证明<br/>" +
            "8 → 常住户口所在地公安机关出具的无违法犯罪证明<br/>" +
            "9 → 填写《入户申请表》";



    public View getView() {
        return rootView;
    }

    public LuoHuView(Context context){
        this.mContext = context;
        initView();
        setListener();
    }

    private void initView() {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_luo_hu, null);

        luohu_txt = (TextView) rootView.findViewById(R.id.luohu_txt);
        luohu_txt.setText(Html.fromHtml(luohu));
    }

    private void setListener(){

    }


    @Override
    public void onClick(View v) {

    }



}
