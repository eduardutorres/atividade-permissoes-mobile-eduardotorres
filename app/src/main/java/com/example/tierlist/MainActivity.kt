package com.example.tierlist

import android.app.AlertDialog
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {

    private lateinit var etArtist: EditText
    private lateinit var btnSearch: Button
    private lateinit var btnExport: Button
    private lateinit var tvStatus: TextView
    private lateinit var areaResultado: LinearLayout

    private lateinit var containerS: LinearLayout
    private lateinit var containerA: LinearLayout
    private lateinit var containerB: LinearLayout
    private lateinit var containerC: LinearLayout
    private lateinit var containerD: LinearLayout
    private lateinit var containerE: LinearLayout

    private val tierMap = mapOf(
        "S" to mutableListOf<Music>(),
        "A" to mutableListOf<Music>(),
        "B" to mutableListOf<Music>(),
        "C" to mutableListOf<Music>(),
        "D" to mutableListOf<Music>(),
        "E" to mutableListOf<Music>()
    )

    private val musicList = mutableListOf<Music>()
    private var currentArtist = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etArtist = findViewById(R.id.etArtist)
        btnSearch = findViewById(R.id.btnSearch)
        btnExport = findViewById(R.id.btnExport)
        tvStatus = findViewById(R.id.tvStatus)
        areaResultado = findViewById(R.id.areaResultado)

        containerS = findViewById(R.id.containerS)
        containerA = findViewById(R.id.containerA)
        containerB = findViewById(R.id.containerB)
        containerC = findViewById(R.id.containerC)
        containerD = findViewById(R.id.containerD)
        containerE = findViewById(R.id.containerE)

        btnSearch.setOnClickListener {
            val artist = etArtist.text.toString().trim()
            if (artist.isEmpty()) {
                Toast.makeText(this, "Digite o nome de um artista!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            currentArtist = artist
            searchMusic(artist)
        }

        btnExport.setOnClickListener {
            exportTierList()
        }
    }

    private fun searchMusic(artist: String) {
        tvStatus.text = "🔍 Buscando..."
        btnSearch.isEnabled = false
        areaResultado.visibility = View.GONE

        val encoded = URLEncoder.encode(artist, "UTF-8")
        val url = "https://itunes.apple.com/search?term=$encoded&media=music&entity=song&limit=10&country=br"

        val queue = Volley.newRequestQueue(this)

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val results = response.getJSONArray("results")

                    if (results.length() == 0) {
                        tvStatus.text = "Nenhuma música encontrada para \"$artist\"."
                        btnSearch.isEnabled = true
                        return@JsonObjectRequest
                    }

                    musicList.clear()
                    areaResultado.removeAllViews()

                    for (i in 0 until results.length()) {
                        val item = results.getJSONObject(i)
                        val track = item.optString("trackName", "Sem título")
                        val artistName = item.optString("artistName", "Desconhecido")
                        val album = item.optString("collectionName", "Álbum desconhecido")
                        val artwork = item.optString("artworkUrl100", "")
                        musicList.add(Music(track, artistName, album, artwork))
                    }

                    areaResultado.visibility = View.VISIBLE

                    for (music in musicList) {
                        val itemView = LayoutInflater.from(this)
                            .inflate(R.layout.item_music, areaResultado, false)

                        val ivArtwork = itemView.findViewById<ImageView>(R.id.ivArtwork)
                        val tvTrackName = itemView.findViewById<TextView>(R.id.tvTrackName)
                        val tvAlbum = itemView.findViewById<TextView>(R.id.tvAlbum)
                        val btnAddToTier = itemView.findViewById<Button>(R.id.btnAddToTier)

                        tvTrackName.text = music.trackName
                        tvAlbum.text = "📀 ${music.albumName}"

                        val artworkUrl = music.artworkUrl.replace("100x100", "300x300")
                        Glide.with(this).load(artworkUrl)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .into(ivArtwork)

                        btnAddToTier.setOnClickListener {
                            showTierDialog(music)
                        }

                        areaResultado.addView(itemView)
                    }

                    tvStatus.text = "${musicList.size} músicas encontradas. Adicione aos tiers!"

                } catch (e: Exception) {
                    tvStatus.text = "Erro ao processar os dados."
                }

                btnSearch.isEnabled = true
            },
            { _ ->
                tvStatus.text = "Erro na conexão. Verifique sua internet."
                btnSearch.isEnabled = true
            }
        )

        queue.add(request)
    }

    private fun showTierDialog(music: Music) {
        val tiers = arrayOf("S", "A", "B", "C", "D", "E")
        AlertDialog.Builder(this)
            .setTitle("Adicionar \"${music.trackName}\" ao tier:")
            .setItems(tiers) { _, which ->
                addMusicToTier(music, tiers[which])
            }
            .show()
    }

    private fun addMusicToTier(music: Music, tier: String) {
        val container = when (tier) {
            "S" -> containerS
            "A" -> containerA
            "B" -> containerB
            "C" -> containerC
            "D" -> containerD
            "E" -> containerE
            else -> return
        }

        tierMap[tier]?.add(music)

        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(65, 65).apply {
                setMargins(4, 4, 4, 4)
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        val artworkUrl = music.artworkUrl.replace("100x100", "300x300")
        Glide.with(this).load(artworkUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(imageView)

        imageView.setOnClickListener {
            Toast.makeText(this, "${music.trackName} — ${music.artistName}", Toast.LENGTH_SHORT).show()
        }

        container.addView(imageView)
        Toast.makeText(this, "\"${music.trackName}\" adicionada ao tier $tier!", Toast.LENGTH_SHORT).show()
    }

    private fun exportTierList() {
        AlertDialog.Builder(this)
            .setTitle("📸 Salvar Tier List")
            .setMessage("Para salvar a imagem da sua Tier List na galeria, o app precisa de permissão de acesso ao armazenamento do dispositivo.")
            .setPositiveButton("Continuar") { _, _ ->
                requestPermissionAndExport()
            }
            .setNegativeButton("Cancelar") { _, _ ->
                Toast.makeText(this, "Exportação cancelada.", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun requestPermissionAndExport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES), 100)
                return
            }
        } else {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 100)
                return
            }
        }
        buildAndSaveStory()
    }

    private fun buildAndSaveStory() {
        tvStatus.text = "⏳ Gerando Story..."

        val storyView = LayoutInflater.from(this)
            .inflate(R.layout.layout_story_export, null, false)

        val storyContainers = mapOf(
            "S" to storyView.findViewById<LinearLayout>(R.id.storyContainerS),
            "A" to storyView.findViewById<LinearLayout>(R.id.storyContainerA),
            "B" to storyView.findViewById<LinearLayout>(R.id.storyContainerB),
            "C" to storyView.findViewById<LinearLayout>(R.id.storyContainerC),
            "D" to storyView.findViewById<LinearLayout>(R.id.storyContainerD),
            "E" to storyView.findViewById<LinearLayout>(R.id.storyContainerE)
        )

        val totalImages = tierMap.values.sumOf { it.size }
        if (totalImages == 0) {
            Toast.makeText(this, "Adicione músicas aos tiers primeiro!", Toast.LENGTH_SHORT).show()
            tvStatus.text = ""
            return
        }

        val loadedCount = AtomicInteger(0)

        for ((tier, musics) in tierMap) {
            val container = storyContainers[tier] ?: continue
            for (music in musics) {
                val iv = ImageView(this).apply {
                    layoutParams = LinearLayout.LayoutParams(140, 140).apply {
                        setMargins(6, 6, 6, 6)
                    }
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
                container.addView(iv)

                val url = music.artworkUrl.replace("100x100", "300x300")
                Glide.with(this)
                    .asBitmap()
                    .load(url)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            iv.setImageBitmap(resource)
                            if (loadedCount.incrementAndGet() >= totalImages) {
                                renderAndSave(storyView)
                            }
                        }
                        override fun onLoadCleared(placeholder: Drawable?) {}
                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            if (loadedCount.incrementAndGet() >= totalImages) {
                                renderAndSave(storyView)
                            }
                        }
                    })
            }
        }
    }

    private fun renderAndSave(storyView: View) {
        storyView.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY)
        )
        storyView.layout(0, 0, 1080, 1920)

        val bitmap = Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        storyView.draw(canvas)

        saveImage(bitmap)
    }

    private fun saveImage(bitmap: Bitmap) {
        val filename = "tierlist_story_${System.currentTimeMillis()}.png"

        runOnUiThread {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/TierList")
                }
                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                uri?.let {
                    contentResolver.openOutputStream(it)?.use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                    tvStatus.text = "Story salvo na Galeria!"
                    Toast.makeText(this, "Story salvo na Galeria!", Toast.LENGTH_LONG).show()
                }
            } else {
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val file = File(dir, filename)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                tvStatus.text = "Story salvo!"
                Toast.makeText(this, "Story salvo em ${file.absolutePath}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            buildAndSaveStory()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Permissão necessária")
                .setMessage("Sem permissão de armazenamento, não é possível salvar a Tier List na galeria. Você pode continuar usando o app normalmente.")
                .setPositiveButton("OK", null)
                .show()
        }
    }
}