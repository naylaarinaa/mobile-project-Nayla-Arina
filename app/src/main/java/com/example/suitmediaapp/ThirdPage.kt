package com.example.suitmediaapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.suitmediaapp.ui.components.Header
import com.example.suitmediaapp.ui.theme.SuitmediaAppTheme
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val avatar: String
)
data class UserResponse(
    val page: Int,
    @SerializedName("per_page") val perPage: Int,
    val total: Int,
    @SerializedName("total_pages") val totalPages: Int,
    val data: List<User>
)

interface ApiService {
    @GET("users")
    suspend fun getUsers(@Query("page") page: Int, @Query("per_page") perPage: Int = 10): UserResponse
}

class ThirdPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuitmediaAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ThirdPageContent(Modifier.fillMaxSize().padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThirdPageContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val activity = context as? Activity

    var users by remember { mutableStateOf(emptyList<User>()) }
    var isLoading by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var currentPage by remember { mutableIntStateOf(1) }
    var totalPages by remember { mutableIntStateOf(1) }
    var hasError by remember { mutableStateOf(false) }
    var canLoadMore by remember { mutableStateOf(true) }

    val listState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()

    val apiService = remember {
        Retrofit.Builder()
            .baseUrl("https://reqres.in/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        chain.proceed(
                            chain.request().newBuilder()
                                .addHeader("x-api-key", "reqres-free-v1")
                                .build()
                        )
                    }
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
            )
            .build()
            .create(ApiService::class.java)
    }

    suspend fun loadUsers(page: Int, isRefresh: Boolean = false): Boolean = try {
        when {
            isRefresh -> { isRefreshing = true; hasError = false }
            page == 1 -> { isLoading = true; hasError = false }
            else -> isLoadingMore = true
        }
        val perPage = 10
        val response = apiService.getUsers(page, perPage)
        users = if (isRefresh || page == 1) response.data else users + response.data
        currentPage = response.page
        totalPages = response.totalPages
        canLoadMore = currentPage < totalPages
        true
    } catch (e: Exception) {
        hasError = true
        e.printStackTrace()
        false
    } finally {
        isLoading = false
        isRefreshing = false
        isLoadingMore = false
    }

    LaunchedEffect(Unit) { loadUsers(1) }

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            loadUsers(1, isRefresh = true)
            pullToRefreshState.endRefresh()
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            lastVisibleItemIndex >= totalItemsNumber - 1
        }.collect { shouldLoadMore ->
            if (shouldLoadMore && canLoadMore && !isLoading && !isRefreshing && !isLoadingMore && users.isNotEmpty()) {
                loadUsers(currentPage + 1)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Header(title = "Third Screen", onBack = { activity?.finish() })
            HorizontalDivider(color = Color(0xFFE2E3E4), thickness = 1.dp)
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading && users.isEmpty() -> LoadingState()
                    hasError && users.isEmpty() && !isRefreshing -> ErrorState { coroutineScope.launch { loadUsers(1, isRefresh = true) } }
                    users.isEmpty() && !hasError && !isRefreshing -> EmptyState { coroutineScope.launch { loadUsers(1, isRefresh = true) } }                    else -> UserList(users, listState, pullToRefreshState, isLoadingMore, canLoadMore, activity)
                }
            }
        }
    }
}

@Composable
fun LoadingState() = Box(Modifier.fillMaxSize(), Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = Color(0xFF2B637B))
        Spacer(Modifier.height(16.dp))
        Text("Loading users...", fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun ErrorState(onRetry: () -> Unit) = Box(Modifier.fillMaxSize(), Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Failed to load users", fontSize = 16.sp, color = Color.Red)
        Spacer(Modifier.height(8.dp))
        Text("Check your internet connection", fontSize = 12.sp, color = Color.Gray)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B637B))) {
            Text("Retry")
        }
    }
}

@Composable
fun EmptyState(onRefresh: () -> Unit) = Box(Modifier.fillMaxSize(), Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("No users found", fontSize = 16.sp, color = Color.Gray)
        Spacer(Modifier.height(8.dp))
        Text("Pull down to refresh", fontSize = 12.sp, color = Color.Gray)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRefresh, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B637B))) {
            Text("Refresh")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserList(
    users: List<User>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    pullToRefreshState: androidx.compose.material3.pulltorefresh.PullToRefreshState,
    isLoadingMore: Boolean,
    canLoadMore: Boolean,
    activity: Activity?
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().nestedScroll(pullToRefreshState.nestedScrollConnection),
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
    ) {
        if (pullToRefreshState.progress > 0f) {
            item {
                Box(Modifier.fillMaxWidth().height(80.dp), Alignment.BottomCenter) {
                    PullToRefreshContainer(state = pullToRefreshState)
                }
            }
        }
        items(users) { user ->
            UserItem(user = user, onClick = {
                val resultIntent = Intent().apply {
                    putExtra("selected_user_name", "${user.firstName} ${user.lastName}")
                }
                activity?.setResult(Activity.RESULT_OK, resultIntent)
                activity?.finish()
            })
            HorizontalDivider(color = Color(0xFFE2E3E4), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
        }
        if (isLoadingMore) {
            item {
                Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(Modifier.size(20.dp), color = Color(0xFF2B637B), strokeWidth = 2.dp)
                        Spacer(Modifier.width(12.dp))
                        Text("Loading more...", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }
        }
        if (!canLoadMore && users.isNotEmpty() && !isLoadingMore) {
            item {
                Box(Modifier.fillMaxWidth().padding(16.dp), Alignment.Center) {
                    Text("You've reached the end", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun UserItem(user: User, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val poppins = FontFamily(
        Font(R.font.poppins_regular, FontWeight.Normal),
        Font(R.font.poppins_medium, FontWeight.Medium),
        Font(R.font.poppins_semibold, FontWeight.SemiBold)
    )
    Row(
        modifier = modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(user.avatar),
            contentDescription = "User Avatar",
            modifier = Modifier.size(49.dp).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(20.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = "${user.firstName} ${user.lastName}",
                fontFamily = poppins,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = Color(0xFF04021D)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = user.email,
                fontFamily = poppins,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                color = Color(0xFF686777)
            )
        }
    }
}