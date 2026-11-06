package com.sudarsanankr.indialivetv

import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import android.text.TextWatcher
import android.text.Editable
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : AppCompatActivity() {

    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var channelList: LinearLayout

    private val channels = listOf(
        "DD News" to "News",
        "DD India" to "News",
        "Sansad TV" to "News",
        "DD Sports" to "Sports",
        "DD National" to "Entertainment",
        "DD Malayalam" to "Malayalam",
        "DD Tamil" to "Tamil",
        "DD Bharati" to "Entertainment"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(12, 14, 24)
        window.navigationBarColor = Color.rgb(12, 14, 24)

        player = ExoPlayer.Builder(this).build()

        val main = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 20, 18, 12)
            setBackgroundColor(Color.rgb(12, 14, 24))
        }

        val title = TextView(this).apply {
            text = "📺 INDIA LIVE TV"
            textSize = 25f
            setTextColor(Color.WHITE)
            setTypeface(null, 1)
        }
        main.addView(title)

        val subtitle = TextView(this).apply {
            text = "Live Television • India"
            textSize = 14f
            setTextColor(Color.LTGRAY)
        }
        main.addView(subtitle)

        playerView = PlayerView(this).apply {
            player = this@MainActivity.player
            useController = true
        }

        main.addView(playerView,
            LinearLayout.LayoutParams(-1, 220).apply {
                topMargin = 20
            }
        )

        val search = EditText(this).apply {
            hint = "Search TV channels..."
            setHintTextColor(Color.GRAY)
            setTextColor(Color.WHITE)
            textSize = 15f
            setSingleLine(true)
        }

        main.addView(search)

        val categories = TextView(this).apply {
            text = "NEWS     SPORTS     MUSIC     MOVIES"
            textSize = 15f
            setTextColor(Color.CYAN)
            setPadding(0, 18, 0, 18)
        }
        main.addView(categories)

        val scroll = ScrollView(this)
        channelList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        scroll.addView(channelList)
        main.addView(scroll,
            LinearLayout.LayoutParams(-1, 0, 1f)
        )

        setContentView(main)

        showChannels(channels)

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int,
                count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?, start: Int,
                before: Int, count: Int
            ) {
                val filtered = channels.filter {
                    it.first.contains(
                        s.toString(),
                        ignoreCase = true
                    )
                }
                showChannels(filtered)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showChannels(items: List<Pair<String, String>>) {
        channelList.removeAllViews()

        items.forEach { channel ->

            val button = Button(this).apply {
                text = "▶  ${channel.first}   •   ${channel.second}"
                textSize = 14f
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.rgb(35, 40, 60))

                setOnClickListener {
                    Toast.makeText(
                        this@MainActivity,
                        "${channel.first}: Official stream URL needed",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            channelList.addView(button,
                LinearLayout.LayoutParams(-1, 65).apply {
                    bottomMargin = 8
                }
            )
        }
    }

    override fun onStop() {
        super.onStop()
        player.pause()
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }
}