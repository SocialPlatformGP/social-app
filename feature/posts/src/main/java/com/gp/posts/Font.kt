package com.gp.posts

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)
val fontName123 = GoogleFont("Arvo",true,)
val MontserratTitle = GoogleFont(name = "Montserrat")
val MerriweatherBody = GoogleFont(name = "Merriweather")

val KarlaFont = GoogleFont(name = "Karla")
val fontFamily = FontFamily(
    Font(googleFont = fontName123, fontProvider = provider)
)
val fontFamily2 = FontFamily(
    Font(googleFont = KarlaFont, fontProvider = provider)
)
val montserratFontFamily = FontFamily(
    Font(googleFont = KarlaFont, fontProvider = provider)
)
val merriweatherFamilyBody = FontFamily(
    Font(googleFont = KarlaFont, fontProvider = provider)
)