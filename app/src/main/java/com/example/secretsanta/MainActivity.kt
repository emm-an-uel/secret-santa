package com.example.secretsanta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.setMargins

class MainActivity : AppCompatActivity() {
    lateinit var linearLayout: LinearLayout
    lateinit var btnGenerate: Button
    lateinit var mapNames: MutableMap<Int, String>
    var numOfEditTexts = 2
    var users = 0

    lateinit var linearLayoutMap: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayout = findViewById(R.id.linearLayout)
        btnGenerate = findViewById(R.id.btnGenerate)
        mapNames = mutableMapOf()
        mapNames.apply {
            put(0, "")
            put(1, "")
        }

        linearLayoutMap = findViewById(R.id.linearLayoutMap)
        updateLinearLayoutMap()

        setTextWatchers()
    }

    private fun setTextWatchers() {
        for (n in 0 until numOfEditTexts) {
            val et: EditText = linearLayout[n] as EditText
            et.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    // do nothing
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    // do nothing
                }

                override fun afterTextChanged(p0: Editable?) {
                    mapNames[n] = p0.toString()
                    updateLinearLayoutMap()
                    checkForBlanks()
                }
            })
        }
    }

    private fun updateLinearLayoutMap() {
        linearLayoutMap.removeAllViews()
        for (k in mapNames.keys) {
            val v = mapNames[k]
            val tv = TextView(this)
            tv.text = "$k - $v"
            linearLayoutMap.addView(tv)
        }
    }

    private fun checkForBlanks() {
        if (!mapNames.containsValue("")) { // if none of the existing editTexts are blank
            val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(40, 3, 40, 0)

            val newEditText = EditText(this)
            newEditText.apply {
                setBackgroundColor(ContextCompat.getColor(this@MainActivity, com.google.android.material.R.color.mtrl_btn_transparent_bg_color))
                hint = "Enter person's name"
            }

            linearLayout.addView(newEditText, layoutParams)
            mapNames[numOfEditTexts] = ""
            updateLinearLayoutMap()
            numOfEditTexts++
            setTextWatchers()
        }
    }
}