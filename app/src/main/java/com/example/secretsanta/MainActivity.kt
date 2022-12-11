package com.example.secretsanta

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.get
import com.gkemon.XMLtoPDF.PdfGenerator
import com.gkemon.XMLtoPDF.PdfGeneratorListener
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    lateinit var linearLayoutNames: LinearLayout
    lateinit var btnGenerate: Button
    lateinit var btnReset: Button
    lateinit var mapNames: MutableMap<Int, String>
    lateinit var mapSecretSanta: MutableMap<String, String>
    lateinit var listSecretSanta: ArrayList<PersonPair>
    var numOfUsers = 3
    var selfPaired = false

    lateinit var listOfCodes: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listOfCodes = arrayListOf()
        listOfCodes.apply {
            add("Horse")
            add("Monkey")
            add("Tiger")
            add("Cat")
            add("Dog")
            add("Ant")
            add("Beetle")
            add("Dolphin")
            add("Whale")
            add("Fish")
        }

        linearLayoutNames = findViewById(R.id.linearLayoutNames)
        btnGenerate = findViewById(R.id.btnGenerate)
        btnReset = findViewById(R.id.btnReset)
        mapSecretSanta = mutableMapOf()
        listSecretSanta = arrayListOf()
        mapNames = mutableMapOf()
        mapNames.apply {
            put(0, "")
            put(1, "")
            put(2, "")
        }

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

            val receiverCode = listOfCodes[n]

            mapSecretSanta[personGiving] = personReceiving
            listSecretSanta.add(PersonPair(personGiving, personReceiving, receiverCode))
        }

        checkForSelfPaired()
    }

    private fun checkForSelfPaired() {
        if (selfPaired) { // re-generate pairings
            selfPaired = false
            generatePairings()
        } else {
            generatePdf()
        }
    }

    private fun generatePdf() {
        // generate pdf for each giver
        for (person in listSecretSanta) {
            // inflate the layout
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.secret_santa_pdf, null)

            // populate layout with giver and receiver names
            val btnName = view.findViewById<Button>(R.id.btnName)
            val name = person.receiver
            btnName.text = "name: $name"
            val btnCode = view.findViewById<Button>(R.id.btnCode)
            val code = person.receiverCode
            btnCode.text = "code: $code"

            // using Gkemon's xml to pdf generator
            PdfGenerator.getBuilder()
                .setContext(this)
                .fromViewSource()
                .fromView(view)
                .setFileName(person.giver) // file name is giver's name
                .setFolderNameOrPath("PDF-folder")
                .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.NONE)
                .build(object : PdfGeneratorListener() {
                    override fun onStartPDFGeneration() {
                    }

                    override fun onFinishPDFGeneration() {
                    }
                })
        }

        // generate code list pdf
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.code_list_pdf, null)
        val btn1 = view.findViewById<Button>(R.id.btn1)
        val btn2 = view.findViewById<Button>(R.id.btn2)
        val btn3 = view.findViewById<Button>(R.id.btn3)
        val btn4 = view.findViewById<Button>(R.id.btn4)

        val p1 = listSecretSanta[0].receiver
        val c1 = listSecretSanta[0].receiverCode
        val p2 = listSecretSanta[1].receiver
        val c2 = listSecretSanta[1].receiverCode
        val p3 = listSecretSanta[2].receiver
        val c3 = listSecretSanta[2].receiverCode
        val p4 = listSecretSanta[3].receiver
        val c4 = listSecretSanta[3].receiverCode

        btn1.text = "$c1: $p1"
        btn2.text = "$c2: $p2"
        btn3.text = "$c3: $p3"
        btn4.text = "$c4: $p4"

        // using Gkemon's xml to pdf generator
        PdfGenerator.getBuilder()
            .setContext(this)
            .fromViewSource()
            .fromView(view)
            .setFileName("CodeList")
            .setFolderNameOrPath("PDF-folder")
            .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.NONE)
            .build(object : PdfGeneratorListener() {
                override fun onStartPDFGeneration() {
                }

                override fun onFinishPDFGeneration() {
                }
            })

        Snackbar.make(btnGenerate, "PDFs generated", Snackbar.LENGTH_SHORT).show()
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