package com.example.typing.view.game

import android.content.ContentValues.TAG
import android.content.DialogInterface
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
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.typing.R
import com.example.typing.databinding.ActivityGameBinding
import com.example.typing.view.rank.RankActivity
import com.example.typing.view.util.KoreanSeparator
import com.example.typing.view.util.ResourceLoader
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class GameActivity : AppCompatActivity(), Runnable {
    private var currentTime = 0
    private var start = 0L
    private var typeStart = -1L
    private var totalTPM = 0.0
    private var stage = 0
    private var clearedTextCount = 0
    private var execTime = 0L
    private var beforeLength = 0
    private var maxLimitTime = 60.0
    private var curLimitTime = maxLimitTime
    private val thread: Thread = Thread(this)
    private lateinit var binding : ActivityGameBinding
    private var typingTexts = ArrayList<String>();
    private var typingText = ""
    private var isTimeAttackMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game)

        ResourceLoader.loadResources(assets)
        typingTexts = ResourceLoader.getTypingTexts()
        typingText = typingTexts[abs(Random().nextInt()) % typingTexts.size]

        isTimeAttackMode = intent.getBooleanExtra("isTimeAttackMode", false)

        if(!isTimeAttackMode) {
            binding.timerIconIv.visibility = View.INVISIBLE
            binding.timeLimitTitle.visibility = View.GONE
            binding.timeLimitTv.visibility = View.GONE
        }
        else {
            binding.timerIconIv.visibility = View.VISIBLE
            binding.timeLimitTitle.visibility = View.VISIBLE
            binding.timeLimitTv.visibility = View.VISIBLE
            binding.timeLimitTv.text = String.format("%.1f초", maxLimitTime)
        }

        binding.typingTv.text = typingText;

        binding.averTpmTv.text = "0타"
        binding.curTpmTv.text = "0타"

        binding.stopGameBtn.setOnClickListener {
            if(stage <= 5) {
                showAlertDialog("그만두기", "5문장 이상 완료하지 못하면 랭킹에 등록되지 않습니다. 그만두시겠습니까?", {
                        _, _ ->
                    gotoResultActivity()
                    finish()
                }, {
                        _, _ ->
                })
            }
            else {
                gotoResultActivity()
                finish()
            }
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

    private fun showAlertDialog(title: String, msg: String,
                                positiveListener: DialogInterface.OnClickListener,
                                negativeListener: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(msg)
        builder.setPositiveButton("확인", positiveListener)
        builder.setNegativeButton("취소", negativeListener)
        builder.show()
    }

    private fun gotoResultActivity() {
        var correctLen = 0
        val typedLen = min(binding.typingEt.text.length, binding.typingTv.text.length)
        for (i in 0 until min(typedLen, typingText.length)) {
            if(binding.typingEt.text[i] == typingText[i]) correctLen++
            else break
        }
        val tpm = if(typeStart == -1L) 0.0 else KoreanSeparator.separate(typingText.substring(0 until correctLen)).length /
                (System.currentTimeMillis() - typeStart).toDouble() * 1000 * 60
        Log.d(TAG, "gotoResultActivity: $tpm")
        val i = Intent(this@GameActivity, ResultActivity::class.java)
        i.putExtra("averTPM", (totalTPM + tpm) / max(stage, 1))
        i.putExtra("stage", clearedTextCount)
        startActivity(i)
        finish()
    }

    override fun run() {
        try {
            while (true) {
                Thread.sleep(max(0L, 100 - execTime))
                if(typeStart > -1L && isTimeAttackMode) {
                    curLimitTime -= 0.1
                    runOnUiThread {
                        binding.timeLimitTv.text = String.format("%.1f초", curLimitTime)
                    }
                    if(curLimitTime <= 0) {
                        typeStart = -1L
                        gotoResultActivity()
                    }
                }
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
                        clearedTextCount++
                        totalTPM += tpm
                        typingText = typingTexts[abs(Random().nextInt()) % typingTexts.size]
                        maxLimitTime *= 0.93
                        curLimitTime = maxLimitTime
                        runOnUiThread {
                            binding.timeLimitTv.text = String.format("%.1f초", maxLimitTime)
                            binding.curTpmTv.text = "0타"
                            binding.typingEt.text.clear()
                            binding.typingTv.text = typingText
                        }
                        continue
                    }
                    runOnUiThread {
                        binding.curTpmTv.text = String.format("%.1f타", tpm)
                        binding.averTpmTv.text = String.format("%.1f타", (totalTPM + tpm) / stage)
                    }
                }
                execTime = System.currentTimeMillis() - start
            }
        }
        catch (ignored: InterruptedException) {}
    }
}