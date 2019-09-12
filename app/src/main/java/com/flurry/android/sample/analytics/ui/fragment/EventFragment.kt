package com.flurry.android.sample.analytics.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.*
import com.flurry.android.sample.analytics.R
import com.flurry.android.sample.analytics.ui.viewmodel.EventViewModel
import com.flurry.android.sample.analytics.util.Utils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.disposables.CompositeDisposable

class EventFragment : BaseFragment() {

    private lateinit var eventViewModel: EventViewModel

    @BindView(R.id.event_name_input_layout)
    lateinit var eventNameInputLayout: TextInputLayout

    @BindView(R.id.start_params_text_view)
    lateinit var startParamsTextView: TextView

    @BindView(R.id.start_params_list)
    lateinit var startParamsList: RecyclerView

    @BindView(R.id.delayed_input_layout)
    lateinit var delayedInputLayout: TextInputLayout

    @BindView(R.id.delayed_input_edit_text)
    lateinit var delayedInputEditText: TextInputEditText

    @BindView(R.id.end_params_text_view)
    lateinit var endParamsTextView: TextView

    @BindView(R.id.end_params_list)
    lateinit var endParamsList: RecyclerView

    private lateinit var startParamsAdapter: EventParamsAdapter

    private lateinit var endParamsAdapter: EventParamsAdapter

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_event, container, false)

        ButterKnife.bind(this, root)

        eventViewModel = ViewModelProviders.of(this, viewModelFactory).get(EventViewModel::class.java)

        eventViewModel.isEventNameValid.observe(this, Observer {
            Utils.handleErrorMessage(eventNameInputLayout, it)
        })

        eventViewModel.isTimedEvent.observe(this, Observer {
            toggleTimedEvent(it)
        })

        startParamsAdapter = EventParamsAdapter(eventViewModel, eventViewModel.startParams, this)

        startParamsAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                startParamsList.smoothScrollToPosition(positionStart)
            }
        })

        startParamsList.adapter = startParamsAdapter

        eventViewModel.isDelayTimeValid.observe(this, Observer {
            Utils.handleErrorMessage(delayedInputLayout, it)
        })

        endParamsAdapter = EventParamsAdapter(eventViewModel, eventViewModel.endParams, this)

        endParamsAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                endParamsList.smoothScrollToPosition(positionStart)
            }
        })

        endParamsList.adapter = endParamsAdapter

        eventViewModel.displayToast.observe(this, Observer {
            Toast.makeText(this.context, it, Toast.LENGTH_LONG).show()
        })

        return root
    }

    @OnTextChanged(value = [R.id.event_name_edit_text], callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    fun onEventNameChanged(s: Editable?) {
        eventViewModel.eventName = s?.toString()
    }

    @OnTextChanged(value = [R.id.delayed_input_edit_text], callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    fun onDelayedTimeChanged(s: Editable?) {
        val delayText = s?.toString()
        val delayed = if (!delayText.isNullOrEmpty()) delayText.toLong() else -1

        eventViewModel.delayTime = delayed
    }

    @OnCheckedChanged(R.id.timed_event_switch)
    fun onTimedEventChanged(isTimedEvent: Boolean) {
        eventViewModel.isTimedEvent.value = isTimedEvent
    }

    @OnClick(R.id.send_button)
    fun onSendButtonClicked(v: View) {
        eventViewModel.logEvent()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun toggleTimedEvent(isTimedEvent: Boolean) {
        if (isTimedEvent) {
            startParamsTextView.text = "Start Parameters"
            delayedInputLayout.visibility = View.VISIBLE
            delayedInputEditText.visibility = View.VISIBLE
            endParamsTextView.visibility = View.VISIBLE
            endParamsList.visibility = View.VISIBLE
        } else {
            startParamsTextView.text = "Parameters"
            delayedInputLayout.visibility = View.GONE
            delayedInputEditText.visibility = View.GONE
            endParamsTextView.visibility = View.GONE
            endParamsList.visibility = View.GONE
        }
    }

    class EventParamsAdapter(private val eventViewModel: EventViewModel,
                             private val params: MutableList<Pair<String, String>>,
                             private val fragment: EventFragment) : RecyclerView.Adapter<EventParamsAdapter.EventParamsViewHolder>() {
        private val paramsList: ArrayList<EventParam> = ArrayList()

        init {
            paramsList.add(EventParam())
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventParamsViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.params_list_item, parent, false)
            return EventParamsViewHolder(view)
        }

        override fun getItemCount(): Int = paramsList.size

        override fun onBindViewHolder(holder: EventParamsViewHolder, position: Int) {
            val param = paramsList[position]

            if (param.key.isNullOrEmpty() || param.value.isNullOrEmpty()) {
                holder.keyEditText.isFocusableInTouchMode = true
                holder.valueEditText.isFocusableInTouchMode = true
            }

            holder.keyEditText.text = param.key?.toEditable()
            holder.valueEditText.text = param.value?.toEditable()
            holder.addButton.setImageResource(if (param.isAdd) android.R.drawable.ic_input_add else android.R.drawable.ic_delete)

            holder.keyEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    eventViewModel.keyErrorMessage.cleanAndObserve(fragment, Observer {
                        Utils.handleErrorMessage(holder.keyInputLayout, it)
                    })
                }
            }

            holder.valueEditText.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    eventViewModel.valueErrorMessage.cleanAndObserve(fragment, Observer {
                        Utils.handleErrorMessage(holder.valueInputLayout, it)
                    })
                }
            }

            holder.keyEditText.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (param.isAdd && s.isNullOrEmpty()) {
                        eventViewModel.keyErrorMessage.setValue("Key could not be empty")
                    } else {
                        eventViewModel.keyErrorMessage.setValue(null)
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            holder.valueEditText.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (param.isAdd && s.isNullOrEmpty()) {
                        eventViewModel.valueErrorMessage.setValue("Value could not be empty")
                    } else {
                        eventViewModel.valueErrorMessage.setValue(null)
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            holder.addButton.setOnClickListener {
                if (param.isAdd) {
                    eventViewModel.keyErrorMessage.cleanAndObserve(fragment, Observer { errorMessage ->
                        Utils.handleErrorMessage(holder.keyInputLayout, errorMessage)
                    })

                    eventViewModel.valueErrorMessage.cleanAndObserve(fragment, Observer { errorMessage ->
                        Utils.handleErrorMessage(holder.valueInputLayout, errorMessage)
                    })

                    val key = holder.keyEditText.text.toString()
                    val value = holder.valueEditText.text.toString()

                    eventViewModel.paramCount.cleanAndObserve(fragment, Observer { count ->
                        eventViewModel.keyErrorMessage.removeObservers(fragment)
                        eventViewModel.valueErrorMessage.removeObservers(fragment)

                        if (count >= 10) {
                            Toast.makeText(it.context, "Could not add more than 10 parameters per event", Toast.LENGTH_LONG).show()
                            return@Observer
                        }

                        // avoid params to be modified
                        holder.keyEditText.isFocusableInTouchMode = false
                        holder.valueEditText.isFocusableInTouchMode = false
                        holder.keyEditText.clearFocus()
                        holder.valueEditText.clearFocus()

                        param.key = key
                        param.value = value
                        param.isAdd = false

                        holder.addButton.setImageResource(android.R.drawable.ic_delete)

                        params.add(Pair(key, value))

                        if (count < 9) {
                            paramsList.add(EventParam())
                        }

                        notifyItemInserted(paramsList.size)
                    })

                    eventViewModel.validateParam(key, value)
                } else {
                    paramsList.removeAt(position)
                    params.removeAt(position)

                    if (paramsList.isEmpty() || !paramsList[paramsList.size - 1].isAdd) {
                        paramsList.add(EventParam())
                    }

                    notifyDataSetChanged()
                }

                hideSoftKeyboard(it)
            }
        }

        private fun hideSoftKeyboard(view: View) {
            val inputMethodManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        class EventParamsViewHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem) {

            @BindView(R.id.key_input_layout)
            lateinit var keyInputLayout: TextInputLayout

            @BindView(R.id.value_input_layout)
            lateinit var valueInputLayout: TextInputLayout

            @BindView(R.id.key_edit_text)
            lateinit var keyEditText: TextInputEditText

            @BindView(R.id.value_edit_text)
            lateinit var valueEditText: TextInputEditText

            @BindView(R.id.add_button)
            lateinit var addButton: FloatingActionButton

            init {
                ButterKnife.bind(this, viewItem)
            }
        }

        data class EventParam(var key: String? = null, var value: String? = null, var isAdd: Boolean = true)

        private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
    }
}