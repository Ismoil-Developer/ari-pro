package uz.mrx.aripro.data.local.shp

import android.content.Context
import android.content.SharedPreferences
import uz.mrx.aripro.utils.SharedPreference
import javax.inject.Inject

class MySharedPreference @Inject constructor(
    context: Context,
    sharedPreferences: SharedPreferences
) : SharedPreference(context, sharedPreferences) {

    var language: String by Strings("uz")

    var mode: String by Strings("light")

    var location: String by Strings("Tashkent")

    var token: String by Strings("")

}