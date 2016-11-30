package com.jaaaelu.gzw.realmdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by admin on 2016/11/21.
 */

public class EditTextDialogFragment extends DialogFragment {

    private EditText mContent;
    private TextView mTitle;
    private TextView mConfirm;
    private TextView mCancel;
    private ChangeContentListener mListener;

    public static EditTextDialogFragment newInstance(String title, String content) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("content", content);
        EditTextDialogFragment fragment = new EditTextDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  Dialog全屏显示
//        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
    }

    public void setContentListener(ChangeContentListener listener) {
        mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmen_edittextt_dialog, container, false);

        initView(view);
        return view;
    }

    private void initView(View view) {
        mTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        mTitle.setText(getArguments().getString("title"));
        mContent = (EditText) view.findViewById(R.id.et_dialog_content);
        mContent.setText(getArguments().getString("content"));
        mContent.setSelection(getEditTextContent().length());
        mConfirm = (TextView) view.findViewById(R.id.tv_dialog_confirm);
        mCancel = (TextView) view.findViewById(R.id.tv_dialog_cancel);

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.changeContent(getEditTextContent());
                dismiss();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public String getEditTextContent() {
        return mContent.getText().toString();
    }

    public interface ChangeContentListener {
        void changeContent(String newContent);
    }

}
