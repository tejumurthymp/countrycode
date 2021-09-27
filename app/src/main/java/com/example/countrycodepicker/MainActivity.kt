package com.example.countrycodepicker

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.countrycodepicker.ui.theme.CountryCodePickerTheme
import com.example.countrycodepicker.ui.theme.Purple200
import com.example.countrycodepicker.ui.theme.Purple500
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CountryCodePickerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Log.e(TAG, "onCreate: country size: ${Locale.getISOCountries().size}")
                    Scaffold(topBar = { TopBar() }) { CountryNavigation() }
                }
            }
        }
    }
}


@Composable
private fun TopBar() {

    TopAppBar(
        title = { Text(text = "CountryList", fontSize = 20.sp) },
        backgroundColor = Purple500,
        contentColor = Color.White
    )
}

@Composable
fun CountryNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "CountryList") {
        composable("CountryList") {
            CountryListScreen(navController)
        }
    }
}

@Composable
fun CountryListScreen(navController: NavHostController) {
    val textVal = remember { mutableStateOf(TextFieldValue("")) }
    Column {
        SearchCountryList(textVal)
        CountryList(textVal)
    }

}

@Composable
fun CountryList(textVal: MutableState<TextFieldValue>) {
    val context = LocalContext.current
    val countries = getListOfCountries()
    var filteredCountries: ArrayList<String>

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        val searchText = textVal.value.text

        filteredCountries = if (searchText.isEmpty()) {
            countries
        } else {
            val resultList = arrayListOf<String>()
            for (country in countries) {
                if (country.lowercase(Locale.getDefault())
                        .contains(searchText.lowercase(Locale.getDefault()))
                ) {
                    resultList.add(country)
                }
            }
            resultList
        }

        items(filteredCountries.size) {
            CountryListItem(
                countryText = filteredCountries[it]
            ) { selectedCountry ->
                Toast.makeText(context, "country: $selectedCountry", Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
fun CountryListItem(countryText: String, onItemCLick: (String) -> Unit) {
    Row(modifier = Modifier
        .clickable {
            onItemCLick(countryText)
        }
        .background(Purple200)
        .height(60.dp)
        .fillMaxWidth()

        .padding(5.dp)) {

        Text(
            text = countryText,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(start = 10.dp)
        )
    }
}

fun getListOfCountries(): ArrayList<String> {
    val isoCountryCodes = Locale.getISOCountries()
    val countryListWithEmojis = ArrayList<String>()

    for (countryCode in isoCountryCodes) {
        val locale = Locale("", countryCode)
        val countryName = locale.displayName
        val flagOffset = 0x1F1E6
        val asciiOffset = 0x41
        val firstChar = Character.codePointAt(countryCode, 0) - asciiOffset + flagOffset
        val secondChar = Character.codePointAt(countryCode, 1) - asciiOffset + flagOffset
        val flag = (String(Character.toChars(firstChar)) + String(Character.toChars(secondChar)))

        countryListWithEmojis.add("$countryName (${locale.country}) $flag")
    }

    return countryListWithEmojis
}

@Composable
fun SearchCountryList(textVal: MutableState<TextFieldValue>) {

    TextField(
        value = textVal.value,
        onValueChange = { textVal.value = it },
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(Color.White, fontSize = 18.sp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                modifier = Modifier
                    .padding(15.dp)
                    .size(24.dp)
            )
        },
        trailingIcon = {
            if (textVal.value != TextFieldValue("")) {
                IconButton(onClick = {
                    textVal.value = TextFieldValue("")
                }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        modifier = Modifier
                            .padding(15.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RectangleShape,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.White,
            cursorColor = Color.White,
            leadingIconColor = Color.White,
            trailingIconColor = Color.White,
            backgroundColor = Purple200,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

