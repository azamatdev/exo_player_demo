package uz.mymax.million.data


import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.google.android.exoplayer2.util.Util
import uz.mymax.million.R


data class Mp3Item(
    val title: String,
    val duration: String,
    val image: Int
)

object Data {


    fun getPlaylistMp3(): List<Mp3Item> {

        var images = intArrayOf(
            R.drawable.antiqa_aparat,
            R.drawable.avtobusdagi_noqulay_holat,
            R.drawable.bizga_haligindan_bervoring,
            R.drawable.davron_kabulov_maqollar,
            R.drawable.pok_pok_rio_hasanboy,
            R.drawable.qirol_artur_parodiya,
            R.drawable.sohibjamol_va_mahluq,
            R.drawable.vak_mak
        )

        val titles = listOf<String>(
            "Antiqa aparat",
            "Avtobusdagi holat",
            "Bizga xaligindan bervoring",
            "Davron Kobulov - Maqollar",
            "Pok Pok Rio Xasanboy",
            "Qirol artur parodiya",
            "Sohibjamol va Maxluq",
            "Vak Mak"
        )
        val duration =
            arrayOf("20 min", "19 min", "11 min", "6 min", "14 min", "30 min", "19 min", "7 min")
        val list = ArrayList<Mp3Item>()

        images.forEachIndexed { index, i ->
            val mp3 = Mp3Item(titles[index], duration[index], images[index])
            list.add(mp3)
        }

        return list
    }

    /**
     *  ##### 4 Steps #######
     *  1. Create URI of each Raw file via RawResourceDataSource Instance
     *  2. Create ConcatenatingMediaSource
     *  3. Add each New ProgressiveMediaSource to concatenatingMediaSource
     */
    fun getReadyPlaylistMediaSource(context: Context): ConcatenatingMediaSource {
        val rawList = listOf<Int>(
            R.raw.antiqa_aparat,
            R.raw.avtobusdagi_noqulay_holat,
            R.raw.bizga_haligindan_bervoring,
            R.raw.davron_kabulov_maqollar,
            R.raw.pok_pok_rio_hasanboy,
            R.raw.qirol_artur_parodiya,
            R.raw.sohibjamol_va_mahluq,
            R.raw.vak_mak
        )

        val playList = ArrayList<Uri>()

        rawList.forEach {
            //Build Data Source from Raw audio file, and add the uri to the Playlist
            val dataSpec = DataSpec(RawResourceDataSource.buildRawResourceUri(it))
            playList.add(dataSpec.uri)
        }

        // Produces DataSource instances through which media data is loaded.
        // This is like a container for dataSource media played
        val dataSourceFactory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, "exoPlayer")
        )

        //This is the whole object of Playlist dataSource
        val concatenatingMediaSource = ConcatenatingMediaSource()

        playList.forEachIndexed { index, it ->
            //ProgressiveMedia Data Source is for simple audio files
            // add the sourceMedia to ConcatenatingMediaSource
            val sourceMediaSource =
                ProgressiveMediaSource.Factory { dataSourceFactory.createDataSource() }
                    .setTag(index)
                    .createMediaSource(it)
            concatenatingMediaSource.addMediaSource(sourceMediaSource)
        }

        return concatenatingMediaSource
    }
}