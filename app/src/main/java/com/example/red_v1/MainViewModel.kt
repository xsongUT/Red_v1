package com.example.red_v1


import android.app.Application
import android.media.MediaPlayer
import androidx.lifecycle.AndroidViewModel
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {
    // A repository can be a local database or the network
    //  or a combination of both
    private val context = getApplication<Application>().applicationContext
    private val repository = Repository()
    private var songResources = repository.fetchData()

    // Public properties, mostly accessed by PlayerFragment, but useful elsewhere

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> get() = _address

    fun setAddress(newAddress: String) {
        _address.value = newAddress
        Log.d("ProfileActivity3", "Setting new address: $newAddress")
    }
    // This variable controls what song is playing
    var currentIndex = 0
    // It is convenient to have the player never be null, so proactively
    // create it, but you should not create MediaPlayer instances
    // in the view model
    var player: MediaPlayer = MediaPlayer.create(
        application.applicationContext,
        getCurrentSongResourceId()
    )
    // Should I loop the current song?
    var loop = false
    // How many songs have played?
    var songsPlayed = 0
    // Is the player playing?
    var isPlaying = false

    // Creating a mutable list also creates a copy
    fun getCopyOfSongInfo(): MutableList<SongInfo> {
        return songResources.toMutableList()
    }
    fun updateSongList(newSongList: List<SongInfo>) {
        songResources = newSongList.toMutableList()
    }

    fun shuffleAndReturnCopyOfSongInfo(): MutableList<SongInfo> {
        // XXX Write me
        val shuffledList = songResources.toMutableList()
        val currentSong = songResources[currentIndex]
        shuffledList.shuffle()
        currentIndex = shuffledList.indexOf(currentSong)
        return shuffledList
    }

    fun getCurrentSongName() : String {
        // XXX Write me
        return songResources[currentIndex].name
    }
    // Private function
    private fun nextIndex() : Int {
        // XXX Write me
        return if (currentIndex == songResources.size - 1) {
            0
        } else {
            currentIndex + 1
        }
    }
    var selectedIndex = false
    fun nextSong() {
        // XXX Write me
        if (!loop) {
            currentIndex = if (currentIndex + 1 < songResources.size) currentIndex + 1 else 0
        } else {
            if (selectedIndex){
                currentIndex = if (currentIndex + 1 < songResources.size) currentIndex + 1 else 0
            }
        }
        player.reset()
        val songUri = Uri.parse("android.resource://${context.packageName}/${getCurrentSongResourceId()}")
        player.setDataSource(context, songUri)
        player.prepare()
        if (isPlaying) player.start()
        songsPlayed++
        selectedIndex = false
    }
    fun getNextSongName() : String {
        // XXX Write me
        return songResources[nextIndex()].name
    }

    fun prevSong() {
        // XXX Write me
        currentIndex = if (currentIndex == 0) {
            songResources.size - 1
        } else {
            currentIndex - 1
        }
        player.reset()
        val songUri = Uri.parse("android.resource://${context.packageName}/${getCurrentSongResourceId()}")
        player.setDataSource(context, songUri)
        player.prepare()
        if (isPlaying) player.start()
        songsPlayed++
    }

    fun getCurrentSongResourceId(): Int {
        // XXX Write me
        return songResources.getOrNull(currentIndex)?.rawId ?: 0
    }

    fun playSelectedSong(position: Int) {
        val songUri = Uri.parse("android.resource://${context.packageName}/${getCurrentSongResourceId()}")

        player.reset()
        player.setDataSource(context, songUri)

        player.prepare()
        player.start()

        isPlaying = true
        songsPlayed++

    }
}