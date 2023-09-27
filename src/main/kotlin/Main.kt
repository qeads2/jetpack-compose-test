import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.example.data.Summoner
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
@Preview
fun SubmitButton() {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        TextButton(
            onClick = {
                println("Button Clicked")
            },
            modifier = Modifier
                .size(100.dp, 50.dp),
            colors = ButtonDefaults.textButtonColors(
                backgroundColor = Color(0xff387fe1),
                contentColor = Color.White
            )
        ) {
            Text("검색")

        }
    }
}

@Composable
@Preview
fun SearchBox() {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .size(25.dp, 150.dp)
            .padding(15.dp)
    ) {
        val textState = remember { mutableStateOf("") }
        TextField(
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            label = { Text(
                text = "소환사명",
                style = MaterialTheme.typography.body1,
                color = Color(0xff828282)
            ) },
            value = textState.value,
            onValueChange = { textState.value = it },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color(0xeeffffff),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,

            ),
        )
        TextButton(
            onClick = {
                println(textState.value)
                CoroutineScope(Dispatchers.IO).launch {
                    val client = HttpClient(Java) {
                        install(ContentNegotiation) {
                            jackson()
                        }
                    }
                    val url = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/${textState.value}"
                    println(url)
                    val response = client.get(url) {
                        headers {
                            append(
                                "X-Riot-Token",
                                "<Riot API Key>"
                            )
                        }
                    }
                    val res = response.body<Summoner>()
                    println("summoner name: ${res.name}, level: ${res.summonerLevel}, puuid: ${res.puuid}")
                    val matchUrl = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/${res.puuid}/ids?start=0&count=1"
                    val matches = client.get(matchUrl) {
                        headers {
                            append(
                                "X-Riot-Token",
                                "<Riot API Key>"
                            )
                        }
                    }
                    println(matches.bodyAsText())
                    val res2 = matches.body<List<String>>()
                    println(res2)
                }






            },
            modifier = Modifier
                .size(100.dp, 40.dp),
            colors = ButtonDefaults.textButtonColors(
                backgroundColor = Color(0xff387fe1),
                contentColor = Color.White
            )
        ) {
            Text("검색")

        }
    }
}
@Composable
@Preview
fun ControllerImage() {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Image(

            painter = painterResource("controller.jpg"),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp, 100.dp)
                .padding(15.dp)
        )
    }
}
@Composable
@Preview
fun Home() {
    Box(modifier = Modifier
        .background(Color(0xee313131))
        .fillMaxSize()) {
        Column {
            ControllerImage()
            SearchBox()
//            SubmitButton()
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        state = WindowState(width = 400.dp, height = 500.dp),
        title = "Medium"
    ) {
        Home()
    }
}
