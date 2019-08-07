package com.beiing.gifmaker.gifmake;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beiing.baseframe.adapter.for_recyclerview.support.OnItemClickListener;
import com.beiing.baseframe.supports.OnClickListener;
import com.beiing.gifmaker.R;
import com.beiing.gifmaker.bean.GifImageFrame;
import com.beiing.gifmaker.constant.Constant;
import com.beiing.gifmaker.image_selector.MultiImageSelector;
import com.beiing.gifmaker.utils.DialogUtil;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GifMakeActivity extends AppCompatActivity implements IGifMakeView {

    public static final String TAG = "GifMakeActivity";
    public static final int START_ALBUM_CODE = 0x21;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.grid_view)
    RecyclerView gridView;
    @BindView(R.id.tv_generate)
    TextView generate;
    @BindView(R.id.clear)
    TextView clear;
    @BindView(R.id.et_fps)
    EditText etFps;
    @BindView(R.id.et_width)
    EditText etWidth;
    @BindView(R.id.et_height)
    EditText etHeight;
    @BindView(R.id.img)
    ImageView img;

    private ImageAdapter adapter;

    private GifMakePresenter presenter;
    private int picWidth = 720;
    private int picHeight = 1280;
    private int picFps = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gifmake);
        ButterKnife.bind(this);
        initData();
        initEvent();
    }


    protected void initData() {
        toolbar.setTitle(R.string.gif_make);
        presenter = new GifMakePresenter(this);
        gridView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new ImageAdapter(this, presenter.getGifImages());
        gridView.setAdapter(adapter);
    }

    protected void initEvent() {
        adapter.setOnItemClickListener(new OnItemClickListener<GifImageFrame>() {
            @Override
            public void onItemClick(@NonNull ViewGroup parent, @NonNull View view, GifImageFrame gifImage, int position) {
                if (gifImage.getType() == GifImageFrame.TYPE_ICON) {
                    MultiImageSelector.create()
                            .showCamera(true) // show camera or not. true by default
                            .count(9) // max select image size, 9 by default. used width #.multi()
                            .multi() // multi mode, default mode;
                            .start(GifMakeActivity.this, Constant.REQUEST_CODE_SELECT_IMAGE);
                }
            }

            @Override
            public boolean onItemLongClick(@NonNull ViewGroup parent, @NonNull View view, GifImageFrame gifImage, int position) {
                if (adapter.getMode() == ImageAdapter.MODE_COMMON) {
                    adapter.setMode(ImageAdapter.MODE_DELETE);
                } else if (adapter.getMode() == ImageAdapter.MODE_DELETE) {
                    adapter.setMode(ImageAdapter.MODE_COMMON);
                }
                return false;
            }
        });

        adapter.setClickListener(new OnClickListener<GifImageFrame>() {
            @Override
            public void onClick(int position, int id, GifImageFrame gifImage) {
                if (id == R.id.iv_delete) {
                    presenter.getGifImages().remove(position);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(value = {R.id.tv_generate, R.id.clear, R.id.tv_preview})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_generate://生成gif图
                int size = presenter.getGifImages().size();
                if (size > 1) {
                    picFps = Integer.parseInt(etFps.getText().toString());
                    picWidth = Integer.parseInt(etWidth.getText().toString());
                    picHeight = Integer.parseInt(etHeight.getText().toString());
                    Toast.makeText(GifMakeActivity.this, "开始生成Gif图", Toast.LENGTH_SHORT).show();
                    presenter.createGif(picFps, picWidth, picHeight);
                    DialogUtil.showLoading(this);
                } else {
                    Toast.makeText(GifMakeActivity.this, "请添加图片", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.clear:
                presenter.clear();
                adapter.notifyDataSetChanged();
                break;

            case R.id.tv_preview:
                if (presenter.isHasPreview()) {
//                    View contentView = LayoutInflater.from(this).inflate(R.layout.layout_gif_preview, null);
//                    GifImageView gifView = (GifImageView) contentView.findViewById(R.id.gif_view);
                    Glide.with(this).load(presenter.getPreViewFile())
                            .into(img);
//                    byte[] fileBytes = FileUtil.getFileBytes(presenter.getPreViewFile());
//                    if (fileBytes != null) {
//
//                        gifView.setBytes(fileBytes);
//                        gifView.startAnimation();
//                    }
//
//                    MaterialDialog mMaterialDialog = new MaterialDialog(this)
//                            .setView(contentView)
//                            .setCanceledOnTouchOutside(true);
//                    mMaterialDialog.show();
                } else {
                    Toast.makeText(GifMakeActivity.this, "没有预览图", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_CODE_SELECT_IMAGE) {
            if (resultCode == RESULT_OK) {
                List<String> paths = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                presenter.solveImages(paths);
            }
        }
    }

    @Override
    public void finishPaths() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void finishCreate(boolean b) {
        DialogUtil.dimiss();
        if (b) {
            Toast.makeText(this, "生成成功", Toast.LENGTH_SHORT).show();
            Glide.with(this).load(presenter.getPreViewFile()).into(img);
        } else {
            Toast.makeText(this, "生成失败", Toast.LENGTH_SHORT).show();
        }
    }
}
