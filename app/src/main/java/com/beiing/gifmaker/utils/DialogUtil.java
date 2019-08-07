package com.beiing.gifmaker.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.beiing.baseframe.supports.ThemeSelectListener;
import com.beiing.gifmaker.R;

import java.util.Arrays;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by chenliu on 2016/7/6.<br/>
 * 描述：
 * </br>
 */
public class DialogUtil {

    private static MaterialDialog mDialog;

    public static void showLoading(Context context){
        mDialog = new MaterialDialog(context);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.progressbar_item,
                        null);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setView(view).show();
    }

    public static void dimiss(){
        if(mDialog != null){
            mDialog.dismiss();
        }
    }
}
