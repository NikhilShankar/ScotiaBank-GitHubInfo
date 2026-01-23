package rebirth.nixaclabs.sbgithubinfo.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import rebirth.nixaclabs.sbgithubinfo.R
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubRepoDetails
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubUser
import rebirth.nixaclabs.sbgithubinfo.ui.theme.Pink80
import rebirth.nixaclabs.sbgithubinfo.ui.theme.TextBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
    onNavigateToDetail: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is MainScreenSideEffect.NavigateToDetail -> {
                    onNavigateToDetail()
                }
                is MainScreenSideEffect.ShowError -> {
                    // Handle error toast if needed
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Take Home") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            SearchSection(
                query = state.searchQuery,
                onQueryChange = { viewModel.onEvent(MainScreenEvent.OnSearchQueryChanged(it)) },
                onSearchClick = { viewModel.onEvent(MainScreenEvent.OnSearchClicked) },
                isLoading = state.isLoadingUser || state.isLoadingRepos
            )

            if (state.userError != null) {
                ErrorText(message = stringResource(R.string.main_screen_user_info_failed))
            }

            if (state.isLoadingUser) {
                LoadingIndicator()
            }

            state.user?.let { user ->
                UserInfoSection(user = user)
            }

            if (state.reposError != null) {
                ErrorText(message = stringResource(R.string.main_screen_repo_details_failed))
            }

            if (state.isLoadingRepos && state.repos.isEmpty()) {
                LoadingIndicator()
            }

            if (state.repos.isNotEmpty()) {
                RepoList(
                    repos = state.repos,
                    onRepoClick = { repo ->
                        viewModel.onEvent(MainScreenEvent.OnRepoSelected(repo))
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchSection(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier
            .heightIn(min = 64.dp)
            .fillMaxWidth().padding(horizontal = 12.dp, vertical = 2.dp),
        verticalAlignment = Alignment.Bottom
    ) {

        val interactionSource = remember { MutableInteractionSource() }

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f).wrapContentHeight(),
            interactionSource = interactionSource,
            enabled = !isLoading,
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp)
        ) { innerTextField ->
            TextFieldDefaults.DecorationBox(

                value = query,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = interactionSource,
                label = { Text(
                    stringResource(R.string.main_screen_text_field_label),
                    modifier = Modifier.padding(0.dp)) },
                contentPadding = PaddingValues(start = 0.dp, end = 0.dp, top = 4.dp, bottom = 4.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedLabelColor = Pink80,
                    unfocusedLabelColor = if(query.isEmpty()) Color.Gray else Pink80,
                    focusedIndicatorColor = Pink80,
                    unfocusedIndicatorColor =if(query.isEmpty()) Color.Gray else Pink80,

                )
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = onSearchClick,
            modifier = Modifier
                .wrapContentSize(),// Matches the full-width look in the screenshot , // Space between TextField and Button
            shape = RoundedCornerShape(4.dp), // Material 3 defaults to round; 4dp matches the "sharper" look in PDF
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray, // Replace with your exact theme purple
                contentColor = Color.Black
            )
        ) {
            Text(
                text = stringResource(R.string.main_screen_search_button_text),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun UserInfoSection(user: GithubUser) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = "User avatar",
            modifier = Modifier
                .size(100.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = user.name,
            style = MaterialTheme.typography.titleMedium.copy(color = TextBlack, fontSize = 14.sp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RepoList(
    repos: List<GithubRepoDetails>,
    onRepoClick: (GithubRepoDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 6.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(repos, key = { it.id }) { repo ->
            RepoItem(repo = repo, onClick = { onRepoClick(repo) })
        }
    }
}

@Composable
private fun RepoItem(
    repo: GithubRepoDetails,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(1.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = repo.name,
                style = MaterialTheme.typography.titleMedium.copy(color = TextBlack, fontSize = 16.sp),
                fontWeight = FontWeight.Bold
            )
            if (repo.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = repo.description,
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextBlack, fontSize = 12.sp),
                )
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorText(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 64.dp, vertical = 16.dp)
    )
}
