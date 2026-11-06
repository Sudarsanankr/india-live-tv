package com.sudarsanankr.indialivetv

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

data class Channel(val name: String, val language: String, val category: String, val url: String)

class MainActivity : AppCompatActivity() {
    private val bg = Color.rgb(12, 14, 24)
    private val panel = Color.rgb(26, 29, 45)
    private val accent = Color.rgb(54, 190, 255)
    private lateinit var list: LinearLayout
    private lateinit var playerView: PlayerView
    private lateinit var player: ExoPlayer
    private lateinit var status: TextView
    private var selectedCategory = "All"
    private var query = ""
    private var current: Channel? = null

    // Add only official/public streams that you are authorized to use.
    private val channels = listOf(
        Channel("DD News", "Hindi", "News", ""),
        Channel("DD India", "English", "News", ""),
        Channel("Sansad TV", "Hindi", "News", ""),
        Channel("DD Malayalam", "Malayalam", "Entertainment", ""),
        Channel("DD Sports", "Hindi", "Sports", ""),
        Channel("DD Bharati", "Hindi", "Entertainment", ""),
        Channel("DD Tamil", "Tamil", "Entertainment", ""),
        Channel("DD National", "Hindi", "Entertainment", "")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        player = ExoPlayer.Builder(this).build()
        buildUi()
        renderChannels()
    }

    private fun rounded(color: Int, radius: Float = 22f): GradientDrawable =
        GradientDrawable().apply { setColor(color); cornerRadius = radius }

    private fun text(value: String, size: Float, color: Int = Color.WHITE, bold: Boolean = false): TextView =
        TextView(this).apply {
            this.text = value; textSize = size; setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
        }

    private fun buildUi() {
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(18, 18, 18, 12)
            setBackgroundColor(bg)
        }
        val title = text("INDIA LIVE TV", 24f, Color.WHITE, true)
        page.addView(title)
        page.addView(text("Live television • News • Sports • Entertainment", 12f, 0xFF9EA7BD.toInt()))
        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL; setPadding(12, 12, 12, 12)
            background = rounded(Color.BLACK, 20f)
        }
        playerView = PlayerView(this).apply {
            player = this@MainActivity.player
            useController = true
            layoutParams = LinearLayout.LayoutParams(-1, dp(205))
        }
        hero.addView(playerView)
        status = text("Choose a channel to start watching", 13f, 0xFFB7C0D5.toInt())
        status.setPadding(4, 10, 4, 4)
        hero.addView(status)
        page.addView(hero, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(18) })

        val search = EditText(this).apply {
            hint = "Search channels..."
            setHintTextColor(0xFF8791A8.toInt()); setTextColor(Color.WHITE)
            setSingleLine(true); setPadding(16, 8, 16, 8); background = rounded(panel, 18f)
        }
        page.addView(search, LinearLayout.LayoutParams(-1, dp(48)).apply { topMargin = dp(14) })
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                query = s?.toString()?.trim()?.lowercase() ?: ""; renderChannels()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val categories = HorizontalScrollView(this).apply { isHorizontalScrollBarEnabled = false }
        val chips = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        listOf("All", "News", "Sports", "Entertainment").forEach { cat ->
            val b = TextView(this).apply {
                text = cat; textSize = 13f; setTextColor(Color.WHITE)
                setPadding(18, 10, 18, 10); background = rounded(if (selectedCategory == cat) accent else panel, 40f)
                setOnClickListener { selectedCategory = cat; renderChannels(); buildCategoryChips(chips) }
            }
            chips.addView(b, LinearLayout.LayoutParams(-2, -2).apply { rightMargin = dp(8) })
        }
        categories.addView(chips)
        page.addView(categories, LinearLayout.LayoutParams(-1, -2).apply { topMargin = dp(14) })
        page.addView(text("CHANNELS", 15f, Color.WHITE, true).apply {
            setPadding(2, 18, 2, 8)
        })
        val scroll = ScrollView(this)
        list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(list)
        page.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(page)
    }

    private fun buildCategoryChips(chips: LinearLayout) {
        chips.removeAllViews()
        listOf("All", "News", "Sports", "Entertainment").forEach { cat ->
            val b = TextView(this).apply {
                text = cat; textSize = 13f; setTextColor(Color.WHITE)
                setPadding(18, 10, 18, 10); background = rounded(if (selectedCategory == cat) accent else panel, 40f)
                setOnClickListener { selectedCategory = cat; buildCategoryChips(chips); renderChannels() }
            }
            chips.addView(b, LinearLayout.LayoutParams(-2, -2).apply { rightMargin = dp(8) })
        }
    }

    private fun renderChannels() {
        if (!::list.isInitialized) return
        list.removeAllViews()
        val filtered = channels.filter {
            (selectedCategory == "All" || it.category == selectedCategory) &&
            (it.name.lowercase().contains(query) || it.language.lowercase().contains(query))
        }
        filtered.forEach { channel ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL
                setPadding(14, 14, 14, 14); background = rounded(panel, 18f)
                setOnClickListener { playChannel(channel) }
            }
            val logo = TextView(this).apply {
                text = channel.name.take(2).uppercase(); textSize = 16f; setTypeface(null, Typeface.BOLD)
                setTextColor(Color.WHITE); gravity = Gravity.CENTER; background = rounded(0xFF343A58.toInt(), 16f)
            }
            row.addView(logo, LinearLayout.LayoutParams(dp(48), dp(48)))
            val labels = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(12, 0, 0, 0) }
            labels.addView(text(channel.name, 15f, Color.WHITE, true))
            labels.addView(text("${channel.language}  •  ${channel.category}", 12f, 0xFF9EA7BD.toInt()))
            row.addView(labels, LinearLayout.LayoutParams(0, -2, 1f))
            row.addView(text("▶", 18f, accent, true))
            list.addView(row, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = dp(10) })
        }
        if (filtered.isEmpty()) list.addView(text("No channels found", 14f, 0xFFB7C0D5.toInt()))
    }

    private fun playChannel(channel: Channel) {
        current = channel
        if (channel.url.isBlank()) {
            status.text = "${channel.name} selected • Official stream URL not added yet"
            Toast.makeText(this, "This channel needs an authorized live stream URL.", Toast.LENGTH_LONG).show()
            return
        }
        status.text = "Playing ${channel.name}"
        player.setMediaItem(MediaItem.fromUri(channel.url))
        player.prepare()
        player.playWhenReady = true
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    override fun onStop() { super.onStop(); player.pause() }
    override fun onDestroy() { player.release(); super.onDestroy() }
}
