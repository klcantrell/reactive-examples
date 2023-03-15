package com.kalalau.kotlinflows

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kalalau.kotlinflows.ui.theme.KotlinFlowsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinFlowsTheme {
                val viewModel = viewModel<MainViewModel>()
                val peopleData = viewModel.peopleData.collectAsState()
                val peopleDataValue = peopleData.value
                val displayLoader = viewModel.displayLoader.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Text(
                        text = "Star Wars People",
                        fontSize = 21.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(top = 16.dp)
                    )
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val peopleListModifier = Modifier
                            .height(maxHeight)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(top = 40.dp)
                            .padding(horizontal = 16.dp)
                            .offset(y = (-80).dp)

                        when (peopleDataValue) {
                            is PeopleData.Loaded -> {
                                PeopleList(
                                    data = peopleDataValue.data, modifier = peopleListModifier
                                )
                            }
                            is PeopleData.Fetching -> {
                                PeopleList(
                                    data = peopleDataValue.data, modifier = peopleListModifier
                                )
                            }
                            is PeopleData.Loading -> {
                                if (displayLoader.value) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .offset(y = (-80).dp)
                                    )
                                }
                            }
                            is PeopleData.Error -> {
                                Text(
                                    text = "Yikes, we ran into some trouble. Try again, please!",
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .offset(y = (-80).dp)
                                )
                            }
                            else -> Unit
                        }

                        Button(
                            onClick = { viewModel.getPeople() },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 40.dp)
                        ) {
                            Text(text = "Fetch them")
                        }

                        Text(
                            text = "Status: ${
                                when (peopleDataValue) {
                                    is PeopleData.Initial -> "initial"
                                    is PeopleData.Loading -> "loading"
                                    is PeopleData.Loaded -> "loaded"
                                    is PeopleData.Fetching -> "fetching"
                                    is PeopleData.Error -> "error"
                                }
                            }",
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.BottomEnd),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PeopleList(data: List<StarWarsPerson>, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        data.forEachIndexed { index, character ->
            Text(
                text = character.name,
                fontSize = 18.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            if (index != data.lastIndex) {
                Divider(color = Color.LightGray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KotlinFlowsTheme {
        Text("Preview")
    }
}