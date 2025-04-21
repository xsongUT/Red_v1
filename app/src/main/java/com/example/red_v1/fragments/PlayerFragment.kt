package com.example.red_v1.fragments


import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.red_v1.MainViewModel
import com.example.red_v1.R
import com.example.red_v1.activities.MainActivity
import com.example.red_v1.adapters.RVDiffAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import com.example.red_v1.databinding.PlayerFragmentBinding
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.floor
import android.util.Log

class PlayerFragment : Fragment() {
    // When this is true, the displayTime coroutine should not modify the seek bar
    val userModifyingSeekBar = AtomicBoolean(false)
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: RVDiffAdapter

    private var _binding: PlayerFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayerFragmentBinding.inflate(inflater, container, false)
        Log.d("PlayerFragment", "Fragment created")
        return binding.root
    }

    private fun initRecyclerViewDividers(rv: RecyclerView) {
        // Let's have dividers between list items
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL
        )
        rv.addItemDecoration(dividerItemDecoration)
    }

    // Please put all display updates in this function
    // The exception is that
    //   displayTime updates the seek bar, time passed & time remaining
    private fun updateDisplay() {
        // If settings is active, we are in the background and do
        // not have a binding.  Return early.
        if (_binding == null) {
            return
        }
        //XXX Write me. Make sure all player UI elements are up to date
        // That includes all buttons, textViews, icons & the seek bar
        val currentSong = viewModel.getCurrentSongName()
        val nextSong = viewModel.getNextSongName()

        binding.playerCurrentSongText.text = currentSong
        binding.playerNextSongText.text = nextSong

        if (viewModel.isPlaying) {
            binding.playerPlayPauseButton.setImageResource(R.drawable.ic_pause_black_24dp)
            MainActivity.setBackgroundDrawable(binding.playerPlayPauseButton, R.drawable.ic_pause_black_24dp)  // 设置暂停背景
        } else {
            binding.playerPlayPauseButton.setImageResource(R.drawable.ic_play_arrow_black_24dp)
            MainActivity.setBackgroundDrawable(binding.playerPlayPauseButton,R.drawable.ic_play_arrow_black_24dp)
        }

        binding.playerSeekBar.max = viewModel.player.duration
        binding.playerSeekBar.progress = viewModel.player.currentPosition

        binding.playerTimePassedText.text = convertTime(viewModel.player.currentPosition)
        binding.playerTimeRemainingText.text = convertTime(viewModel.player.duration - viewModel.player.currentPosition)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Make the RVDiffAdapter and set it up
        //XXX Write me. Setup adapter.
        setupAdapter()
        //XXX Write me. Write callbacks for buttons
        setupButtons()
        //XXX Write me. binding.playerSeekBar.setOnSeekBarChangeListener
        setupSeekBar()
        updateDisplay()

        // Don't change this code.  We are launching a coroutine (user-level thread) that will
        // execute concurrently with our code, but will update the displayed time
        val millisec = 100L
        viewLifecycleOwner.lifecycleScope.launch {
            displayTime(millisec)
        }
    }

    // The suspend modifier marks this as a function called from a coroutine
    // Note, this whole function is somewhat reminiscent of the Timer class
    // from Fling and Peck.  We use an independent thread to manage one small
    // piece of our GUI.  This coroutine should not modify any data accessed
    // by the main thread (it can read property values)
    private suspend fun displayTime(misc: Long) {
        // This only runs while the display is active
        while (viewLifecycleOwner.lifecycleScope.coroutineContext.isActive) {
            val currentPosition = viewModel.player.currentPosition
            val maxTime = viewModel.player.duration
            // Update the seek bar (if the user isn't updating it)
            // and update the passed and remaining time
            //XXX Write me
            if (!userModifyingSeekBar.get()) {
                binding.playerSeekBar.progress = currentPosition
                binding.playerSeekBar.max = maxTime
                binding.playerTimePassedText.text = convertTime(currentPosition)
                binding.playerTimeRemainingText.text = convertTime(maxTime - currentPosition)
            }
            // Leave this code as is.  it inserts a delay so that this thread does
            // not consume too much CPU
            delay(misc)
        }
    }

    // This method converts time in milliseconds to minutes-second formatted string
    // with two digit minutes and two digit sections, e.g., 01:30
    private fun convertTime(milliseconds: Int): String {
        //XXX Write me
        val minutes = floor(milliseconds / 1000.0 / 60.0).toInt()
        val seconds = (milliseconds / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    // XXX Write me, handle player dynamics and currently playing/next song

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupAdapter() {
        adapter = RVDiffAdapter(viewModel) { songIndex ->
            viewModel.currentIndex = songIndex
            updateDisplay()
        }

        binding.playerRV.adapter = adapter

        initRecyclerViewDividers(binding.playerRV)

        binding.playerRV.layoutManager = LinearLayoutManager(requireContext())
        val songList = viewModel.getCopyOfSongInfo()
        adapter.submitList(songList)
        viewModel.updateSongList(songList)
        updateDisplay()
    }
    private fun setupButtons() {
        binding.playerPlayPauseButton.setOnClickListener {
            if (viewModel.isPlaying) {
                viewModel.player.pause()
                viewModel.isPlaying = false
            } else {
                viewModel.player.start()
                viewModel.isPlaying = true
            }
            updateDisplay()
        }

        binding.playerSkipForwardButton.setOnClickListener {
            viewModel.selectedIndex = true
            viewModel.nextSong()
            adapter.notifyDataSetChanged()
            updateDisplay()
        }

        binding.playerSkipBackButton.setOnClickListener {
            viewModel.prevSong()
            adapter.notifyDataSetChanged()
            updateDisplay()
        }

        binding.playerPermuteButton.setOnClickListener {
            val currentSong = viewModel.getCurrentSongName()
            val shuffledList = viewModel.shuffleAndReturnCopyOfSongInfo()
            viewModel.updateSongList(shuffledList)
            val currentIndexInShuffled = shuffledList.indexOfFirst { it.name == currentSong }
            if (currentIndexInShuffled != -1) {
                viewModel.currentIndex = currentIndexInShuffled
            }
            adapter.submitList(shuffledList)
            updateDisplay()
        }
        binding.loopIndicator.text = "L"
        binding.loopIndicator.setOnClickListener {
            viewModel.loop = !viewModel.loop
            if (viewModel.loop) {
                MainActivity.setBackgroundColor(binding.loopIndicator, Color.RED)
            } else {
                MainActivity.setBackgroundColor(binding.loopIndicator, Color.WHITE)
            }
        }

        if (viewModel.loop) {
            MainActivity.setBackgroundColor(binding.loopIndicator, Color.RED)
        } else {
            MainActivity.setBackgroundColor(binding.loopIndicator, Color.WHITE)
        }
        if (viewModel.loop) {
            viewModel.player.setOnCompletionListener {
                viewModel.player.isLooping = true
                viewModel.player.seekTo(0)
                viewModel.player.start()
                viewModel.songsPlayed++
            }
        } else {
            viewModel.player.setOnCompletionListener {
                viewModel.nextSong()
                adapter.notifyDataSetChanged()
                updateDisplay()
            }
        }
    }

    private fun setupSeekBar() {
        binding.playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    userModifyingSeekBar.set(true)
                    viewModel.player.seekTo(progress)
                    binding.playerTimePassedText.text = convertTime(progress)
                    binding.playerTimeRemainingText.text =
                        convertTime(viewModel.player.duration - progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                userModifyingSeekBar.set(false)
            }
        })
    }
}
