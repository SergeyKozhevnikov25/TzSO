package space.game.android.tzsantehnikaonline.presentation.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yandex.mapkit.geometry.Point

class InfoViewModel : ViewModel() {

    private val _point: MutableLiveData<Point> = MutableLiveData()
    val point: LiveData<Point>
        get() = _point

    fun setPoint(coordinates: Point) {
        _point.value = coordinates
    }

    fun getLatitude() = point.value?.latitude
    fun getLongitude() = point.value?.longitude

}