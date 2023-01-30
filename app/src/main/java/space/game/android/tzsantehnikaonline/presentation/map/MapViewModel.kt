package space.game.android.tzsantehnikaonline.presentation.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yandex.mapkit.geometry.Point

class MapViewModel : ViewModel() {

    private val _location: MutableLiveData<Point> = MutableLiveData()
    val location: LiveData<Point>
        get() = _location

    private val _placeMark: MutableLiveData<Point> = MutableLiveData()
    val placeMark: LiveData<Point>
        get() = _placeMark

    fun setLocation(coordinates: Point) {
        _location.value = coordinates
    }

    fun setPlaceMark(coordinates: Point) {
        _placeMark.value = coordinates
    }
}