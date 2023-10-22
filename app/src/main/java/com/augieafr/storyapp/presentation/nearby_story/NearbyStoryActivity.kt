package com.augieafr.storyapp.presentation.nearby_story

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.augieafr.storyapp.R
import com.augieafr.storyapp.data.utils.ResultState
import com.augieafr.storyapp.databinding.ActivityNearbyStoryBinding
import com.augieafr.storyapp.presentation.model.StoryUIModel
import com.augieafr.storyapp.presentation.utils.ViewModelProvider
import com.augieafr.storyapp.presentation.utils.setVisibility
import com.augieafr.storyapp.presentation.utils.showErrorAlert
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NearbyStoryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityNearbyStoryBinding
    private val viewModel by viewModels<NearbyStoryViewModel> {
        ViewModelProvider(this@NearbyStoryActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNearbyStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(binding.map.id) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle()
        initObserver()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.getNearbyStory().collectLatest {
                when (it) {
                    is ResultState.Error -> it.throwable.showErrorAlert(this@NearbyStoryActivity)
                    is ResultState.Loading -> {
                        binding.map.setVisibility(!it.isLoading)
                        binding.progressBar.setVisibility(it.isLoading)
                    }

                    is ResultState.Success -> {
                        addManyMarker(it.data)
                    }
                }
            }
        }
    }

    private fun addManyMarker(listStory: List<StoryUIModel>) {
        val boundsBuilder = LatLngBounds.Builder()
        listStory.forEach { story ->
            if (story.lat != null && story.lon != null) {
                val latLng = LatLng(story.lat, story.lon)
                mMap.addMarker(
                    MarkerOptions().position(latLng).title(story.userName)
                )
                boundsBuilder.include(latLng)
            }

        }
        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(this::class.simpleName, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(this::class.simpleName, "Can't find style. Error: ", exception)
        }
    }
}