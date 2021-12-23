package no.bekkchristmas.julegaveaapningsimulatoren

import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class MainActivity : AppCompatActivity() {

    private val GIFT_STATE = "GIFT_STATE"
    private val CLICK_COUNT = "CLICK_COUNT"
    private val CURRENT_GIFT = "CURRENT_GIFT"
    private val REQUIRED_CLICKS = "REQUIRED_CLICKS"

    private val INVISIBLE = "invisible"
    private val NOT_OPENED = "not_opened"
    private val OPENED = "opened"
    private val RESTART = "restart"

    private var giftState = "invisible"
    private var clickCount = -1
    private var currentGift = getRandomGift()
    private var requiredClicks = getRandomRequiredClicksToOpen()
    private var constraintLayout: ConstraintLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(savedInstanceState != null) {
            giftState = savedInstanceState.getString(GIFT_STATE, "invisible")
            clickCount = savedInstanceState.getInt(CLICK_COUNT, -1)
            currentGift = savedInstanceState.getInt(CURRENT_GIFT, getRandomGift() )
            requiredClicks = savedInstanceState.getInt(REQUIRED_CLICKS, getRandomRequiredClicksToOpen())
        }
        constraintLayout = findViewById(R.id.constraintLayout)
        constraintLayout!!.setOnClickListener {
            handleClick()
        }
        constraintLayout!!.setOnLongClickListener {
            showSnackBar()
        }
        setViewElements()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(GIFT_STATE, giftState)
        outState.putInt(CLICK_COUNT, clickCount)
        outState.putInt(CURRENT_GIFT, currentGift)
        outState.putInt(REQUIRED_CLICKS, requiredClicks)
        super.onSaveInstanceState(outState)
    }

    private fun handleClick() {
        when(giftState) {
            INVISIBLE -> {
                giftState = NOT_OPENED
                clickCount = 0
            }
            NOT_OPENED -> {
                if (clickCount < requiredClicks) clickCount++
                if (clickCount >= requiredClicks) giftState = OPENED
            }
            OPENED -> giftState = RESTART
            RESTART -> {
                giftState = INVISIBLE
                clickCount = -1
                currentGift = getRandomGift()
                requiredClicks = getRandomRequiredClicksToOpen()
            }
        }
        setViewElements()
    }

    private fun setViewElements() {
        val textView: TextView = findViewById(R.id.textView)
        val restartTextView: TextView = findViewById(R.id.restart_text)
        val imageView: ImageView = findViewById(R.id.imageView)
        val konfettiView: KonfettiView = findViewById(R.id.konfettiView)

        when(giftState) {
            INVISIBLE -> {
                imageView.setImageResource(R.mipmap.christmas_tree)
                restartTextView.visibility = View.INVISIBLE
                textView.text = getString(R.string.click_to_start)
                textView.textSize = 24F
                textView.setTypeface(null, Typeface.NORMAL)
            }
            NOT_OPENED -> {
                imageView.setImageResource(R.mipmap.gift_box)
                imageView.visibility = View.VISIBLE
                if(getClicksLeftToOpen() == 1) textView.text = getString(R.string.click_to_open)
                else textView.text = getString(R.string.clicks_to_open, getClicksLeftToOpen())
            }
            OPENED -> {
                textView.text = getString(R.string.gift_reveal, getGiftString(currentGift))
                textView.textSize = 32F
                imageView.setImageResource(getGiftResource(currentGift))
                konfettiBurst(konfettiView)
            }
            RESTART -> {
                imageView.setImageResource(R.mipmap.santa_claus)
                textView.text = getString(R.string.god_jul)
                textView.setTypeface(null, Typeface.BOLD)
                textView.textSize = 42F
                restartTextView.visibility = View.VISIBLE
                konfettiStream(konfettiView)
            }
        }
    }

    private fun showSnackBar(): Boolean {
        if(clickCount <= 0){
            return false
        }
        val clicksText = getString(R.string.keep_clicking)
        Snackbar.make(
            findViewById(R.id.constraintLayout),
            clicksText,
            Snackbar.LENGTH_SHORT
        ).show()
        return true
    }

    private fun konfettiBurst(konfettiView: KonfettiView) = konfettiView.build()
        .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
        .setDirection(0.0, 359.0)
        .setSpeed(1f, 8f)
        .setFadeOutEnabled(true)
        .setTimeToLive(5000L)
        .addShapes(Shape.Square, Shape.Circle)
        .addSizes(Size(12), Size(16), Size(20), Size(24))
        .setPosition(konfettiView.x + konfettiView.width / 2, konfettiView.y + konfettiView.height / 2)
        .burst(5000)

    private fun konfettiStream(konfettiView: KonfettiView) = konfettiView.build()
        .addColors(Color.RED, Color.GREEN, Color.MAGENTA)
        .setDirection(0.0, 359.0)
        .setSpeed(1f, 2f)
        .setFadeOutEnabled(true)
        .setTimeToLive(2500L)
        .addShapes(Shape.Square, Shape.Circle)
        .addSizes(Size(12), Size(16, 6f))
        .setPosition(-50f, konfettiView.width + 50f, -100f, -100f)
        .streamFor(300, 1000L)

    private fun getRandomGift(): Int = (1..6).random()

    private fun getRandomRequiredClicksToOpen(): Int = (1..100).random()

    private fun getClicksLeftToOpen(): Int = requiredClicks - clickCount

    private fun getGiftResource(giftId: Int): Int = when(giftId) {
        1 -> R.mipmap.gift_1
        2 -> R.mipmap.gift_2
        3 -> R.mipmap.gift_3
        4 -> R.mipmap.gift_4
        5 -> R.mipmap.gift_5
        6 -> R.mipmap.gift_6
        else -> -1
    }

    private fun getGiftString(giftId: Int): String = when(giftId) {
        1 -> getString(R.string.gift_1)
        2 -> getString(R.string.gift_2) + "\n<3"
        3 -> getString(R.string.gift_3) + "\nYES!"
        4 -> getString(R.string.gift_4)
        5 -> getString(R.string.gift_5)
        6 -> getString(R.string.gift_6)
        else -> ""
    }
}