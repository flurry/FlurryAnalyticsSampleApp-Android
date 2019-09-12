package com.flurry.android.sample.analytics.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.*
import com.flurry.android.sample.analytics.R
import com.flurry.android.sample.analytics.ui.viewmodel.OtherFeaturesViewModel
import com.flurry.android.sample.analytics.util.Utils
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class OtherFeaturesFragment : BaseFragment() {

    @BindView(R.id.features_spinner)
    lateinit var featureSpinner: Spinner

    @BindView(R.id.text_input_layout_1)
    lateinit var textInputLayout1: TextInputLayout

    @BindView(R.id.edit_text_1)
    lateinit var editText1: TextInputEditText

    @BindView(R.id.text_input_layout_2)
    lateinit var textInputLayout2: TextInputLayout

    @BindView(R.id.edit_text_2)
    lateinit var editText2: TextInputEditText

    @BindView(R.id.text_input_layout_3)
    lateinit var textInputLayout3: TextInputLayout

    @BindView(R.id.edit_text_3)
    lateinit var editText3: TextInputEditText

    @BindView(R.id.answer_spinner)
    lateinit var answerSpinner: Spinner

    @BindView(R.id.result_textview)
    lateinit var resultTextView: TextView

    private lateinit var otherFeaturesViewModel: OtherFeaturesViewModel

    private lateinit var genderAdapter: ArrayAdapter<CharSequence>
    private lateinit var boolAdapter: ArrayAdapter<CharSequence>
    private lateinit var consentAdapter: ArrayAdapter<CharSequence>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_other_features, container, false)

        ButterKnife.bind(this, root)

        otherFeaturesViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(OtherFeaturesViewModel::class.java)

        featureSpinner.adapter = createArrayAdapter(context!!, R.array.features_spinner)

        genderAdapter = createArrayAdapter(context!!, R.array.gender_spinner)
        boolAdapter = createArrayAdapter(context!!, R.array.bool_spinner)
        consentAdapter = createArrayAdapter(context!!, R.array.consent_spinner)

        otherFeaturesViewModel.errorMessage1.observe(this, Observer {
            Utils.handleErrorMessage(textInputLayout1, it)
        })

        otherFeaturesViewModel.errorMessage2.observe(this, Observer {
            Utils.handleErrorMessage(textInputLayout2, it)
        })

        otherFeaturesViewModel.errorMessage3.observe(this, Observer {
            Utils.handleErrorMessage(textInputLayout3, it)
        })

        otherFeaturesViewModel.displayToast.observe(this, Observer {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })

        otherFeaturesViewModel.hint1.observe(this, Observer {
            textInputLayout1.visibility = View.VISIBLE
            textInputLayout1.hint = it.first
            editText1.inputType = it.second
        })

        otherFeaturesViewModel.hint2.observe(this, Observer {
            textInputLayout2.visibility = View.VISIBLE
            textInputLayout2.hint = it.first
            editText2.inputType = it.second
        })

        otherFeaturesViewModel.hint3.observe(this, Observer {
            textInputLayout3.visibility = View.VISIBLE
            textInputLayout3.hint = it.first
            editText3.inputType = it.second
        })

        otherFeaturesViewModel.answerSpinnerType.observe(this, Observer {
            answerSpinner.visibility = View.VISIBLE

            when(it!!) {
                OtherFeaturesViewModel.SpinnerType.BOOL -> answerSpinner.adapter = boolAdapter
                OtherFeaturesViewModel.SpinnerType.GENDER -> answerSpinner.adapter = genderAdapter
                OtherFeaturesViewModel.SpinnerType.CONSENT -> answerSpinner.adapter = consentAdapter
            }
        })

        otherFeaturesViewModel.resultText.observe(this, Observer {
            resultTextView.visibility = View.VISIBLE
            resultTextView.text = it
        })

        return root
    }

    @OnItemSelected(R.id.features_spinner)
    fun onFeaturesSpinnerSelected(pos: Int) {
        reset()
        otherFeaturesViewModel.construct(pos)
    }

    @OnTouch(R.id.features_spinner)
    fun onFeaturesSpinnerTouched(v: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            clearFocus()
            hideSoftKeyboard(v)
        }

        return false
    }

    @OnClick(R.id.run_button)
    fun onRunButtonClicked() {
        clearFocus()

        val input1 = editText1.text.toString()
        val input2 = editText2.text.toString()
        val input3 = editText3.text.toString()
        val featurePos = featureSpinner.selectedItemPosition
        val answerPos = answerSpinner.selectedItemPosition

        otherFeaturesViewModel.validateThenRun(featurePos, input1, input2, input3, answerPos)
    }

    private fun createArrayAdapter(context: Context, id: Int) =
        ArrayAdapter.createFromResource(context, id, android.R.layout.simple_spinner_item).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

    private fun reset() {
        textInputLayout1.visibility = View.GONE
        textInputLayout2.visibility = View.GONE
        textInputLayout3.visibility = View.GONE
        answerSpinner.visibility = View.GONE
        resultTextView.visibility = View.GONE
        editText1.setText("")
        editText2.setText("")
        editText3.setText("")
        otherFeaturesViewModel.errorMessage1.postValue(null)
        otherFeaturesViewModel.errorMessage2.postValue(null)
        otherFeaturesViewModel.errorMessage3.postValue(null)
    }

    private fun clearFocus() {
        editText1.clearFocus()
        editText2.clearFocus()
        editText3.clearFocus()
    }

    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}