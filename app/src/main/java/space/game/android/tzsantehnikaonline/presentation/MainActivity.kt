package space.game.android.tzsantehnikaonline.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.MapKitFactory
import space.game.android.tzsantehnikaonline.R
import space.game.android.tzsantehnikaonline.presentation.info.InfoFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MapKitFactory.setApiKey(API_KEY)
        MapKitFactory.initialize(this)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, InfoFragment.newInstance(null))
                .commit()
        }
    }

    companion object {
        private const val API_KEY = "dcf288d5-0d3f-43d1-9eb6-51a4d61f80a3"
    }
}