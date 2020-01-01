package com.bright.course.config

import android.app.DialogFragment
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.bright.course.R
import com.classroom.activity.ScanQRCodeActivity
import kotlinx.android.synthetic.main.activity_add_license.*


/**
 * Created by kim on 2018/8/27.
 *
 */
class AddLicenseDialog : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)

        return inflater.inflate(R.layout.activity_add_license, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etLicense.addTextChangedListener(mTextWatcher)

        btnScanQrCode.setOnClickListener {
            activity?.let { ScanQRCodeActivity.start(it) }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnRegister.setOnClickListener {
            dismiss()
        }
    }

    private val mTextWatcher = object : TextWatcher {
        var beforeLength: Int = 0

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            beforeLength = s.length
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        //一般我们都是在这个里面进行我们文本框的输入的判断，上面两个方法用到的很少
        override fun afterTextChanged(s: Editable) {
            editLengthChange(beforeLength, s)
        }
    }

    /**
     * 每隔四位插入一个“-”
     */
    private fun editLengthChange(beforeLength: Int, s: Editable) {
        val length = s.toString().length
        val b = s.toString().endsWith("-")
        if (beforeLength < length) {//判断输入状态
            if (length == 4 || length == 9 || length == 14) {
                etLicense.setText(StringBuffer(s).insert(length, "-").toString())
            } else if (length == 5 || length == 10 || length == 15) { //另一种情况，手动删除空格再次输入后
                if (!b) etLicense.setText(StringBuffer(s).insert(length - 1, "-").toString())
            }
        } else { //删除状态
            if (b) etLicense.setText(StringBuffer(s).delete(length - 1, length).toString())
        }
        //设置指针选中位置
        etLicense.setSelection(etLicense.text.toString().length)
    }
}