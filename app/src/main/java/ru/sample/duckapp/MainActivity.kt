package ru.sample.duckapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.ExperimentalAssetLoader
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.await
import ru.sample.duckapp.infra.Api.getDuckByCode
import ru.sample.duckapp.infra.Api.randomDucksApi

@ExperimentalAssetLoader
class MainActivity : AppCompatActivity() {
    private var loading = false
    private val animationView by lazy(LazyThreadSafetyMode.NONE) {
        findViewById<RiveAnimationView>(R.id.load)
    }
    private val inputView by lazy(LazyThreadSafetyMode.NONE) {
        findViewById<TextInputLayout>(R.id.input)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        animationView.setOnTouchListener { v, _ ->
            onClick(v)
            true
        }
    }

    private fun onClick(v: View) {
        when (v.id) {
            R.id.load -> {
                if (!loading) {
                    loading = true
                    if (inputView.editText?.text?.isEmpty() == true) {
                        loadRandomDuck()
                    } else {
                        loadDuckByCode(inputView.editText?.text.toString().toInt())
                    }

                    animationView.setNumberState("State machine 1", "Progress", 0f)
                    animationView.setBooleanState("State machine 1", "Downloading", true)
                }
            }
        }
    }

    private fun loadRandomDuck() = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = randomDucksApi.getRandomDuck().await().url
            withContext(Dispatchers.Main) {
                Picasso.get().load(response).into(findViewById<ImageView>(R.id.image))

                for (i in 0..100) {
                    delay(10)
                    animationView.setNumberState("State machine 1", "Progress", i.toFloat())
                }

                animationView.setNumberState("State machine 1", "Progress", 100f)
                delay(1000)
                animationView.setNumberState("State machine 1", "Progress", 0f)
                animationView.setBooleanState("State machine 1", "Downloading", false)

                loading = false
            }
        } catch (e: Exception) {
            Log.e("loadRandomDuck", e.toString())
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Ошибка загрузки", Toast.LENGTH_SHORT).show()
                animationView.setNumberState("State machine 1", "Progress", 0f)
                animationView.setBooleanState("State machine 1", "Downloading", false)

                loading = false
            }
        }
    }

    private fun loadDuckByCode(code: Int) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = getDuckByCode(code)
            withContext(Dispatchers.Main) {
                Picasso.get().load(response).into(findViewById<ImageView>(R.id.image))

                for (i in 0..100) {
                    delay(10)
                    animationView.setNumberState("State machine 1", "Progress", i.toFloat())
                }

                animationView.setNumberState("State machine 1", "Progress", 100f)
                delay(1000)
                animationView.setNumberState("State machine 1", "Progress", 0f)
                animationView.setBooleanState("State machine 1", "Downloading", false)

                loading = false
            }
        } catch (e: Exception) {
            Log.e("loadDuckByCode", e.toString())
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Ошибка загрузки", Toast.LENGTH_SHORT).show()
                animationView.setNumberState("State machine 1", "Progress", 0f)
                animationView.setBooleanState("State machine 1", "Downloading", false)

                loading = false
            }
        }
    }
}