package com.jinin4.journalog.applock

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import com.jinin4.journalog.databinding.ActivityLockScreenBinding
import com.jinin4.journalog.utils.PasswordUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// 반정현 작성 - 24.01.28
class ScreenLockActivity : Activity(){
    val context: Context = this
    private lateinit var binding:ActivityLockScreenBinding
    protected var filters: Array<InputFilter?>? = null
    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ScreenLockActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            context.startActivity(intent)
        }
    }

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockScreenBinding.inflate(layoutInflater)
        binding.tvMessage.text="비밀번호를 입력해주세요"
        setContentView(binding.root)

        setupFilters(numberFilter)

        setupEditText(binding.passcode1)
        setupEditText(binding.passcode2)
        setupEditText(binding.passcode3)
        setupEditText(binding.passcode4)


        val buttons = listOf(
            binding.button0,
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6,
            binding.button7,
            binding.button8,
            binding.button9
        )

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener { view ->
                val currentValueString = index.toString()
                when {
                    binding.passcode1.isFocused -> {
                        binding.passcode1.transformationMethod = PasswordTransformationMethod.getInstance()
                        binding.passcode1.setText(currentValueString)
                        binding.passcode2.requestFocus()
                        binding.passcode2.text.clear()
                    }
                    binding.passcode2.isFocused -> {
                        binding.passcode2.transformationMethod = PasswordTransformationMethod.getInstance()
                        binding.passcode2.setText(currentValueString)
                        binding.passcode3.requestFocus()
                        binding.passcode3.text.clear()
                    }
                    binding.passcode3.isFocused -> {
                        binding.passcode3.transformationMethod = PasswordTransformationMethod.getInstance()
                        binding.passcode3.setText(currentValueString)
                        binding.passcode4.requestFocus()
                        binding.passcode4.text.clear()
                    }
                    binding.passcode4.isFocused -> {
                        binding.passcode4.transformationMethod = PasswordTransformationMethod.getInstance()
                        binding.passcode4.setText(currentValueString)
                    }
                }

                if (binding.passcode1.text.isNotEmpty() && binding.passcode2.text.isNotEmpty() && binding.passcode3.text.isNotEmpty() && binding.passcode4.text.isNotEmpty()) {
                    onPasscodeInputed()
                }
            }
        }



        binding.buttonClear
            .setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    clearFields()
                }
            })

        binding.buttonErase
            .setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    onDeleteKey()
                }
            })

        val resourceId = resources.getIdentifier("slide_up", "anim", packageName)
        val resourceIdNothing = resources.getIdentifier("nothing", "anim", packageName)
        overridePendingTransition(resourceId, resourceIdNothing)



    }

    fun setupFilters(numberFilter: InputFilter) {
        filters = arrayOfNulls<InputFilter>(2)
        filters?.set(0, InputFilter.LengthFilter(1))
        filters?.set(1, numberFilter)
    }

    private fun onPasscodeInputed() {
        val enteredPassword = "${binding.passcode1?.text}${binding.passcode2?.text}${binding.passcode3?.text}${binding.passcode4?.text}"

        GlobalScope.launch(Dispatchers.Main) {
            val isPasswordCorrect = PasswordUtils.checkPassword(context, enteredPassword)
            if (isPasswordCorrect) {
                setResult(RESULT_OK);
                finish()
            } else {
                binding.tvMessage.text="비밀번호를 다시 입력해주세요"
                onPasscodeError()
            }
        }
    }

    override fun onBackPressed(){
        moveTaskToBack(true)
    }

    protected fun setupEditText(editText: EditText) {
        editText.inputType = InputType.TYPE_NULL
        editText.filters = filters
        editText.setOnTouchListener(touchListener)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_DEL) {
            onDeleteKey()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun onDeleteKey() {
        when {
            binding.passcode1.isFocused -> {
                // Do nothing
            }
            binding.passcode2.isFocused -> {
                binding.passcode1?.requestFocus()
                binding.passcode1?.setText("")
            }
            binding.passcode3.isFocused -> {
                binding.passcode2?.requestFocus()
                binding.passcode2?.setText("")
            }
            binding.passcode4?.isFocused == true-> {
                binding.passcode3?.requestFocus()
                binding.passcode3?.setText("")
            }
        }
    }

    protected fun onPasscodeError() {
        val thread = Thread {
            val resourceIdShake= resources.getIdentifier("shake", "anim", packageName)
            val animation = AnimationUtils.loadAnimation(this, resourceIdShake)
            binding.llApplock.startAnimation(animation)
            binding.passcode1.setText("")
            binding.passcode2.setText("")
            binding.passcode3.setText("")
            binding.passcode4.setText("")
            binding.passcode1.requestFocus()
        }
        runOnUiThread(thread)
    }

    private val numberFilter = InputFilter { source, start, end, dest, dstart, dend ->
        when {
            source.length > 1 -> ""
            source.isEmpty() -> null
            else -> {
                try {
                    val number = Integer.parseInt(source.toString())
                    if (number in 0..9) number.toString() else ""
                } catch (e: NumberFormatException) {
                    ""
                }
            }
        }
    }

    private val touchListener = View.OnTouchListener { v, event ->
        v.performClick()
        clearFields()
        false
    }

    private fun clearFields() {
        binding.passcode1.setText("")
        binding.passcode2.setText("")
        binding.passcode3.setText("")
        binding.passcode4.setText("")

        binding.passcode1.postDelayed({ binding.passcode1.requestFocus() }, 200)
    }

}