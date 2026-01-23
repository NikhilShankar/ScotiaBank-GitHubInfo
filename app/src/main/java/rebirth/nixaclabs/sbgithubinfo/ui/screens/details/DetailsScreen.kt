package rebirth.nixaclabs.sbgithubinfo.ui.screens.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rebirth.nixaclabs.sbgithubinfo.R
import rebirth.nixaclabs.sbgithubinfo.ui.screens.main.MainScreenViewModel
import rebirth.nixaclabs.sbgithubinfo.ui.theme.Pink80
import rebirth.nixaclabs.sbgithubinfo.ui.theme.StarBadgeGold
import rebirth.nixaclabs.sbgithubinfo.ui.theme.TextBlack
import androidx.compose.ui.platform.testTag
import rebirth.nixaclabs.sbgithubinfo.domain.model.GithubRepoDetails
import rebirth.nixaclabs.sbgithubinfo.utils.formatDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


/**
 * Wrapper composable that holds the ViewModel reference.
 * The actual UI is in DetailsScreen composable which can be tested independently.
 */
@Composable
fun DetailsScreenUI(
    viewModel: MainScreenViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    DetailsScreen(
        repo = state.selectedRepo,
        totalForks = state.totalForks,
        hasStarBadge = state.hasStarBadge,
        onBackClick = onBackClick
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    repo: GithubRepoDetails?,
    totalForks: Int,
    hasStarBadge: Boolean,
    onBackClick: () -> Unit
) {
    // Local state to trigger enter animation on first composition
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text( "Repository Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            if (repo != null) {
                // Total Forks Card (Senior Developer Requirement)
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(1.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        DetailSectionHeader(
                            text = stringResource(R.string.details_screen_total_forks_label)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (hasStarBadge) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = stringResource(R.string.details_screen_star_badge),
                                    tint = StarBadgeGold,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .testTag("star_badge")
                                )
                            }
                            Text(
                                text = totalForks.toString(),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = if (hasStarBadge) StarBadgeGold else TextBlack,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp
                                ),
                                modifier = Modifier.testTag("total_forks_value")
                            )
                            if (hasStarBadge) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = stringResource(R.string.details_screen_star_badge),
                                    tint = StarBadgeGold,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .testTag("star_badge")
                                )
                            }
                        }

                        if (hasStarBadge) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.details_screen_star_badge),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = TextBlack,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.testTag("star_badge_label")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 1000)) +
                            slideInVertically(animationSpec = tween(durationMillis = 1000), initialOffsetY = { 100 }),
                    exit = fadeOut(animationSpec = tween(durationMillis = 0))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(1.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            if (repo.name.isNotBlank()) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = repo.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextBlack,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Color.LightGray, thickness = 2.dp)
                                Spacer(modifier = Modifier.height(24.dp))


                            }

                            DetailSectionHeader(text = stringResource(R.string.details_screen_description_label))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = repo.description.ifBlank {
                                    stringResource(R.string.details_screen_no_description)
                                },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextBlack,
                                    fontSize = 14.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(16.dp))

                            // Last Updated Section
                            repo.updatedAt.formatDate()?.let { formattedDate ->
                                DetailSectionHeader(text = stringResource(R.string.details_screen_updated_at_label))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = formattedDate,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = TextBlack,
                                        fontSize = 14.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(16.dp))

                            // Stats Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem(
                                    label = stringResource(R.string.details_screen_stargazers_label),
                                    value = repo.starGazersCount.toString()
                                )
                                StatItem(
                                    label = stringResource(R.string.details_screen_forks_label),
                                    value = repo.forks.toString()
                                )
                            }
                        }
                    }
                }



            } else {
                Text(
                    text = "No repository selected",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun DetailSectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(
            color = Pink80,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
    )
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    testTag: String = ""
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                color = TextBlack,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            ),
            modifier = if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = Pink80,
                fontWeight = FontWeight.Medium
            )
        )
    }
}


