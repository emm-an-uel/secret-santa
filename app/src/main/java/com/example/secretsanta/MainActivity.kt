package com.example.secretsanta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    lateinit var linearLayoutNames: LinearLayout
    lateinit var btnGenerate: Button
    lateinit var btnReset: Button
    lateinit var mapNames: MutableMap<Int, String>
    lateinit var mapSecretSanta: MutableMap<String, String>
    var numOfUsers = 3
    var selfPaired = false

    lateinit var linearLayoutResults: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayoutNames = findViewById(R.id.linearLayoutNames)
        btnGenerate = findViewById(R.id.btnGenerate)
        btnReset = findViewById(R.id.btnReset)
        mapSecretSanta = mutableMapOf()
        mapNames = mutableMapOf()
        mapNames.apply {
            put(0, "")
            put(1, "")
            put(2, "")
        }

        linearLayoutResults = findViewById(R.id.linearLayoutResults)

        setTextWatchers()
        btnGenerate.setOnClickListener {
            if (mapNames.size == 3 && mapNames.containsValue("")) { // min. 3 names, if map only contains 3 and any of them are empty, not valid
                Snackbar.make(btnGenerate, "Input at least 3 names!", Snackbar.LENGTH_SHORT).show()
            } else {
                generatePairings()
            }
        }

        btnReset.setOnClickListener {
            Snackbar.make(btnReset, "Names cleared", Snackbar.LENGTH_SHORT).show()
            mapNames.clear()
            mapSecretSanta.clear()
            linearLayoutNames.removeAllViews()
            linearLayoutResults.removeAllViews()
            numOfUsers = 3
            for (n in 0 until numOfUsers) {
                val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutParams.setMargins(40, 3, 40, 0)

                val newEditText = EditText(this)
                newEditText.apply {
                    setBackgroundColor(ContextCompat.getColor(this@MainActivity, com.google.android.material.R.color.mtrl_btn_transparent_bg_color))
                    hint = "Enter person's name"
                }

                linearLayoutNames.addView(newEditText, layoutParams)
                mapNames[n] = ""
            }
            setTextWatchers()
        }
    }

    private fun generatePairings() {
        // THE LOGIC: code runs through every person in listOfGiving and pairs them up with someone in listOfReceiving
        // once the person in listOfReceiving has a pair, their number will be removed from listOfReceiving so they don't get double gifts
        // if the number in listOfGiving == listOfReceiving (ie person is paired with themselves), code will be re-run so nobody is paired with themselves
        mapSecretSanta = mutableMapOf()
        val listOfReceiving = arrayListOf<Int>()
        val listOfGiving = arrayListOf<Int>()
        for (k in mapNames.keys) {
            if (mapNames[k] != "") {
                listOfReceiving.add(k)
                listOfGiving.add(k)
            }
        }

        for (n in listOfGiving) {
            val personGiving = mapNames[n]!!
            val i = (0 until listOfReceiving.size).random()
            val indexReceiving = listOfReceiving[i]
            val personReceiving = mapNames[indexReceiving]!!

            if (personGiving == personReceiving) {
                selfPaired = true
            }
            listOfReceiving.remove(indexReceiving)

            mapSecretSanta[personGiving] = personReceiving
        }

        displayPairings()
    }

    private fun displayPairings() {
        // restart whole process if personGiving == personReceiving
        // restarting everything is necessary otherwise it could be that the first 3 are ok, but the last person is self-paired. this would cause infinite loop
        if (!selfPaired) {
            linearLayoutResults.removeAllViews()
            val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(10, 0, 10, 0)

            for (k in mapSecretSanta.keys) {
                val v = mapSecretSanta[k]
                val tv = TextView(this)
                tv.text = "$k: $v"
                linearLayoutResults.addView(tv, layoutParams)
            }
        } else {
            selfPaired = false
            generatePairings()
        }
    }

    private fun setTextWatchers() {
        for (n in 0 until numOfUsers) {
            val et: EditText = linearLayoutNames[n] as EditText
            et.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    // do nothing
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    // do nothing
                }

                override fun afterTextChanged(p0: Editable?) {
                    mapNames[n] = p0.toString()
                    checkForBlanks()
                }
            })
        }
    }

    private fun checkForBlanks() {
        if (!mapNames.containsValue("") && numOfUsers < 10) { // if none of the existing editTexts are blank && max 10 people
            val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            layoutParams.setMargins(40, 3, 40, 0)

            val newEditText = EditText(this)
            newEditText.apply {
                setBackgroundColor(ContextCompat.getColor(this@MainActivity, com.google.android.material.R.color.mtrl_btn_transparent_bg_color))
                hint = "Enter person's name"
            }

            linearLayoutNames.addView(newEditText, layoutParams)
            mapNames[numOfUsers] = ""
            numOfUsers++
            setTextWatchers()
        }
    }
}