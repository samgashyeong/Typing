package com.example.typing.view.game

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.typing.R
import com.example.typing.databinding.ActivityGameBinding
import com.example.typing.view.MainActivity
import com.example.typing.view.util.KoreanSeparator
import com.example.typing.view.util.ResourceLoader
import java.lang.Long.max
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.min

class GameActivity : AppCompatActivity(), Runnable {
    private var currentTime = 0
    private var start = 0L
    private var typeStart = -1L
    private var totalTPM = 0.0
    private var stage = 0
    private var execTime = 0L
    private var beforeLength = 0
    private val thread: Thread = Thread(this)
    private lateinit var binding : ActivityGameBinding
    private var typingTexts = ArrayList<String>();
    private var typingText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game)

        ResourceLoader.loadResources(assets)
        typingTexts = ResourceLoader.getTypingTexts()
        typingText = typingTexts[abs(Random().nextInt()) % typingTexts.size]

        val isTimeAttackMode = intent.extras?.getBoolean("isTimeAttackMode", false) == true

        if(!isTimeAttackMode) {
            binding.timerIconIv.visibility = View.INVISIBLE
            binding.timeLimitTitle.visibility = View.INVISIBLE
            binding.timeLimitTv.visibility = View.INVISIBLE
        }

        binding.typingTv.text = typingText;

        binding.averTpmTv.text = "0타"
        binding.curTpmTv.text = "0타"

        binding.stopGameBtn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.typingEt.requestFocus()
        binding.typingEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0 == null) return
                if(typeStart < 0L && p0.isNotEmpty()) {
                    stage++
                    typeStart = System.currentTimeMillis()
                }
                if(p0.length - beforeLength >= 2) {
                    binding.typingEt.text.clear()
                }
                beforeLength = p0.length
                if(binding.typingEt.text.isNotEmpty()) {
                    val lastIdx = binding.typingEt.text.length - 1
                    val lastTypedChar = binding.typingEt.text[lastIdx]
                    val typedLen = min(
                        binding.typingEt.text.length
                                - (if ((lastTypedChar in '가'..'힣' || lastTypedChar in 'ㄱ'..'ㅎ')
                            && lastIdx < typingText.length && lastTypedChar != typingText[lastIdx]) 1 else 0),
                        binding.typingTv.text.length
                    )

                    var correctLen = 0
                    for (i in 0 until min(typedLen, typingText.length)) {
                        if (typingText[i] == binding.typingEt.text[i]) correctLen++
                        else break
                    }
                    changeColorPartially(binding.typingTv, 0, correctLen, R.color.main_color)
                    changeColorPartially(binding.typingTv, correctLen, typedLen, R.color.light_red)
                    changeColorPartially(
                        binding.typingTv,
                        typedLen,
                        binding.typingTv.text.length,
                        R.color.black
                    )
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    override fun onStart() {
        super.onStart()
        thread.start()
    }

    override fun onStop() {
        super.onStop()
        thread.interrupt()
    }

    fun changeColorPartially(textView: TextView, start: Int, end: Int, colorId: Int) {
        val builder = SpannableStringBuilder(textView.text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setSpan(ForegroundColorSpan(resources.getColor(colorId, null)),
                start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }
        textView.text = builder;
    }

    override fun run() {
        try {
            while (true) {
                Thread.sleep(max(0L, 100 - execTime))
                start = System.currentTimeMillis()
                currentTime++
                if(typeStart > -1L && currentTime % 10 == 0) {
                    var correctLen = 0
                    val typedLen = min(binding.typingEt.text.length, binding.typingTv.text.length)
                    for (i in 0 until min(typedLen, typingText.length)) {
                        if(binding.typingEt.text[i] == typingText[i]) correctLen++
                        else break
                    }
                    val tpm = KoreanSeparator.separate(typingText.substring(0 until correctLen)).length /
                            (System.currentTimeMillis() - typeStart).toDouble() * 1000 * 60
                    if(correctLen >= typingText.length) {
                        typeStart = -1L
                        totalTPM += tpm
                        typingText = typingTexts[abs(Random().nextInt()) % typingTexts.size]
                        runOnUiThread {
                            binding.curTpmTv.text = "0타"
                            binding.typingEt.text.clear()
                            binding.typingTv.text = typingText
                        }
                        continue
                    }
                    runOnUiThread {
                        binding.curTpmTv.text = String.format(
                            "%.1f타",
                            tpm
                        )
                        binding.averTpmTv.text = String.format(
                            "%.1f타",
                            (totalTPM + tpm) / stage
                        )
                    }
                }
                execTime = System.currentTimeMillis() - start
            }
        }
        catch (ignored: InterruptedException) {}
    }
}