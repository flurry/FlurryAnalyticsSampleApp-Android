package com.flurry.android.sample.analytics.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.flurry.android.sample.analytics.R
import com.flurry.android.sample.analytics.ui.viewmodel.CrashViewModel
import com.flurry.android.sample.analytics.util.Utils
import com.google.android.material.textfield.TextInputLayout

class CrashFragment : BaseFragment() {

    private lateinit var crashViewModel: CrashViewModel

    @BindView(R.id.error_id_input_layout)
    lateinit var idInputLayout: TextInputLayout

    @BindView(R.id.error_id_edit_text)
    lateinit var idEditText: EditText

    @BindView(R.id.error_msg_input_layout)
    lateinit var messageInputLayout: TextInputLayout

    @BindView(R.id.error_msg_edit_text)
    lateinit var messageEditText: EditText

    @BindView(R.id.error_class_input_layout)
    lateinit var classInputLayout: TextInputLayout

    @BindView(R.id.error_class_edit_text)
    lateinit var classEditText: EditText

    @BindView(R.id.exception_input_layout)
    lateinit var exceptionInputLayout: TextInputLayout

    @BindView(R.id.exception_edit_text)
    lateinit var exceptionEditText: EditText

    @BindView(R.id.breadcrumb_input_layout)
    lateinit var breadcrumbInputLayout: TextInputLayout

    @BindView(R.id.breadcrumb_edit_text)
    lateinit var breadcrumbEditText: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_crash, container, false)

        ButterKnife.bind(this, root)

        crashViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(CrashViewModel::class.java)

        crashViewModel.idErrorMessage.observe(this, Observer {
            Utils.handleErrorMessage(idInputLayout, it)
        })

        crashViewModel.messageErrorMessage.observe(this, Observer {
            Utils.handleErrorMessage(messageInputLayout, it)
        })

        crashViewModel.classErrorMessage.observe(this, Observer {
            Utils.handleErrorMessage(classInputLayout, it)
        })

        crashViewModel.exceptionErrorMessage.observe(this, Observer {
            Utils.handleErrorMessage(exceptionInputLayout, it)
        })

        crashViewModel.breadcrumbErrorMessage.observe(this, Observer {
            Utils.handleErrorMessage(breadcrumbInputLayout, it)
        })

        crashViewModel.displayToast.observe(this, Observer {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })

        return root
    }

    @OnClick(R.id.bt_log_error_class)
    fun onLogErrorClassClicked(v: View) {
        val errorId = idEditText.text.toString()
        val errorMessage = messageEditText.text.toString()
        val errorClass = classEditText.text.toString()

        crashViewModel.validateThenLogErrorClass(errorId, errorMessage, errorClass)
        hideSoftKeyboard(v)
    }

    @OnClick(R.id.bt_log_exception)
    fun onLogExceptionClicked(v: View) {
        val errorId = idEditText.text.toString()
        val errorMessage = messageEditText.text.toString()
        val exceptionMessage = exceptionEditText.text.toString()

        crashViewModel.validateThenLogException(errorId, errorMessage, exceptionMessage)
        hideSoftKeyboard(v)
    }

    @OnClick(R.id.bt_log_breadcrumb)
    fun onLogBreadcrumbClicked(v: View) {
        val breadcrumb = breadcrumbEditText.text.toString()

        crashViewModel.validateThenLogBreadcrumb(breadcrumb)
        hideSoftKeyboard(v)
    }

    @OnClick(R.id.bt_force_crash)
    fun onForceCrashClicked(v: View) {
        hideSoftKeyboard(v)

        throw RuntimeException("Force Crash")
    }

    private fun hideSoftKeyboard(view: View) {
        clearFocus()

        val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun clearFocus() {
        idEditText.clearFocus()
        messageEditText.clearFocus()
        classEditText.clearFocus()
        exceptionEditText.clearFocus()
        breadcrumbEditText.clearFocus()
    }
}