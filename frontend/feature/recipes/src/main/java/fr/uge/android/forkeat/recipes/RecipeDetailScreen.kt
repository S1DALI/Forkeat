package fr.uge.android.forkeat.recipes

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import fr.uge.android.forkeat.network.ForkEatApi
import fr.uge.android.forkeat.designsystem.theme.Gray500
import fr.uge.android.forkeat.designsystem.theme.Primary500
import fr.uge.android.forkeat.designsystem.theme.Secondary700
import fr.uge.android.forkeat.designsystem.theme.SurfaceCream
import fr.uge.android.forkeat.designsystem.theme.Typography
import fr.uge.android.forkeat.recipes.data.dto.AllergenDTO
import fr.uge.android.forkeat.recipes.data.dto.RecipeDTO
import fr.uge.android.forkeat.recipes.data.dto.RecipeDetailsDTO
import fr.uge.android.forkeat.recipes.data.dto.RecipeDiffDTO
import fr.uge.android.forkeat.recipes.data.dto.RecipeIngredientDTO
import fr.uge.android.forkeat.recipes.data.dto.RecipeStepDTO
import java.util.UUID
import kotlin.time.Instant

// ══════════════════════════════════════════════════════════════════════════════
// ÉCRAN PRINCIPAL
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun RecipeDetailScreen(
    recipe: RecipeDetailsDTO,
    parent: RecipeDTO? = null,
    diff: RecipeDiffDTO? = null,
    isOwner: Boolean = false,
    isAuthenticated: Boolean = false,
    onBack: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onCreateVariant: () -> Unit = {}
) {
    val recipesViewModel: RecipesViewModel = viewModel()
    var diffModeActive by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var isLiked by remember { mutableStateOf(recipe.hasLiked) }
    var likeCount by remember { mutableIntStateOf(recipe.nbLike.toInt()) }
    LaunchedEffect(recipe.hasLiked) { isLiked = recipe.hasLiked }
    LaunchedEffect(recipe.nbLike) { likeCount = recipe.nbLike.toInt() }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Supprimer la recette") },
            text = { Text("Voulez-vous vraiment supprimer « ${recipe.title} » ?\nCette action est irréversible.") },
            confirmButton = {
                Button(
                    onClick = { showDeleteDialog = false; onDelete() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) { Text("Supprimer", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Annuler") }
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            when {
                isOwner -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onEdit,
                            modifier = Modifier.weight(1f).height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Secondary700),
                            shape = RoundedCornerShape(14.dp)
                        ) { Text("Modifier", color = Color.White, fontWeight = FontWeight.SemiBold) }
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f).height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(14.dp)
                        ) { Text("Supprimer", color = Color.White, fontWeight = FontWeight.SemiBold) }
                    }
                }
                isAuthenticated -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Button(
                            onClick = onCreateVariant,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary500),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                "Créer une variante",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

                // ── Hero image ────────────────────────────────────────────────
                Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                    if (!recipe.imageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = recipe.imageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF7C3AED), Primary500)
                                    )
                                )
                        )
                    }
                    // Scrim bas de l'image
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0xBB000000)),
                                    startY = 120f
                                )
                            )
                    )
                    // Badge image modifiée (diff)
                    if (diff?.imageChanged == true) {
                        Surface(
                            color = Color(0xFFF59E0B),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 60.dp, end = 16.dp)
                        ) {
                            Text(
                                "Image modifiée",
                                color = Color.White,
                                style = Typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // ── Carte blanche (overlap -28dp sur l'image) ─────────────────
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(constraints)
                            val overlap = 28.dp.roundToPx()
                            layout(placeable.width, placeable.height - overlap) {
                                placeable.placeRelative(0, -overlap)
                            }
                        },
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    shadowElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier.padding(
                            start = 20.dp,
                            end = 20.dp,
                            top = 24.dp,
                            bottom = 16.dp
                        )
                    ) {
                        // ── Titre ─────────────────────────────────────────────
                        Text(
                            text = recipe.title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            lineHeight = 28.sp
                        )
                        if (diff?.titleChanged == true && diff.originalTitle != null) {
                            Spacer(Modifier.height(4.dp))
                            Surface(
                                color = Color(0xFFFFFBEB),
                                shape = RoundedCornerShape(50.dp),
                                border = BorderStroke(1.dp, Color(0xFFFCD34D))
                            ) {
                                Text(
                                    text = "Titre modifié · Original : ${diff.originalTitle}",
                                    style = Typography.labelSmall,
                                    color = Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(4.dp))

                        // ── Auteur ─────────────────────────────────────────────
                        Text(
                            text = "Par ${recipe.username}",
                            style = Typography.labelSmall,
                            color = Gray500
                        )

                        Spacer(Modifier.height(14.dp))

                        // ── Pills (temps + likes + delta diff) ─────────────────
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Temps
                            Surface(
                                color = Color(0xFF22C55E),
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = formatDuration(recipe.preparationMinutes),
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            // Likes
                            Surface(
                                color = if (isLiked) Color(0xFFFFE4E6) else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = null,
                                        tint = if (isLiked) Color(0xFFFF4D6D) else Color(0xFF94A3B8),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = formatCount(likeCount),
                                        color = if (isLiked) Color(0xFFFF4D6D) else Color(0xFF94A3B8),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            // Delta temps (diff)
                            if (diff != null && diff.timeDelta != 0) {
                                val isPositive = diff.timeDelta > 0
                                Surface(
                                    color = if (isPositive) Color(0xFFFFF7ED) else Color(0xFFECFDF5),
                                    shape = RoundedCornerShape(50),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isPositive) Color(0xFFFBD38D) else Color(0xFF6EE7B7)
                                    )
                                ) {
                                    Text(
                                        text = "${if (isPositive) "+" else ""}${diff.timeDelta} min",
                                        style = Typography.labelSmall,
                                        color = if (isPositive) Color(0xFFB45309) else Color(0xFF059669),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp)
                                    )
                                }
                            }
                        }

                        // ── Résumé ─────────────────────────────────────────────
                        if (recipe.summary.isNotBlank()) {
                            Spacer(Modifier.height(14.dp))
                            Text(
                                text = recipe.summary,
                                style = Typography.bodyMedium,
                                color = Color(0xFF64748B)
                            )
                            val originalSummary = diff?.originalSummary
                            if (diff?.summaryChanged == true && originalSummary != null) {
                                Spacer(Modifier.height(8.dp))
                                Surface(
                                    color = Color(0xFFFFFBEB),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFFCD34D)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            "Description originale",
                                            style = Typography.labelSmall,
                                            color = Color(0xFFB45309),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = originalSummary,
                                            style = Typography.bodySmall,
                                            color = Color(0xFFB45309),
                                            textDecoration = TextDecoration.LineThrough,
                                            fontStyle = FontStyle.Italic
                                        )
                                    }
                                }
                            }
                        }

                        // ── Allergènes ─────────────────────────────────────────
                        val hasAllergens = (diff != null && diff.allergens.isNotEmpty()) ||
                                recipe.allergens.isNotEmpty()
                        if (hasAllergens) {
                            Spacer(Modifier.height(12.dp))
                            if (diff != null && diff.allergens.isNotEmpty()) {
                                DiffAllergenBadges(diff.allergens, diffModeActive)
                            } else {
                                AllergenBadges(recipe.allergens)
                            }
                        }

                        // ── Régimes alimentaires ───────────────────────────────
                        val activeDietaryFlags = recipe.dietaryFlags.filter { it.value }
                        val hasDietary = (diff != null && diff.dietaryFlags.isNotEmpty()) ||
                                activeDietaryFlags.isNotEmpty()
                        if (hasDietary) {
                            Spacer(Modifier.height(8.dp))
                            if (diff != null && diff.dietaryFlags.isNotEmpty()) {
                                DiffDietaryFlagBadges(diff.dietaryFlags, diffModeActive)
                            } else {
                                DietaryFlagBadges(activeDietaryFlags)
                            }
                        }

                        // ── Bannière variante ──────────────────────────────────
                        if (parent != null) {
                            Spacer(Modifier.height(14.dp))
                            VariantBanner(
                                parentTitle = parent.title,
                                diffModeActive = diffModeActive,
                                onToggleDiffMode = { diffModeActive = !diffModeActive }
                            )
                        }

                        Spacer(Modifier.height(20.dp))

                        // ── Tabs ───────────────────────────────────────────────
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = Color.Transparent,
                            contentColor = Primary500,
                            divider = { HorizontalDivider(color = Color(0xFFE5E7EB)) }
                        ) {
                            listOf("Ingrédients", "Préparation").forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = {
                                        Text(
                                            title,
                                            fontWeight = if (selectedTab == index) FontWeight.SemiBold
                                            else FontWeight.Normal,
                                            color = if (selectedTab == index) Primary500
                                            else Color(0xFF9CA3AF)
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // ── Contenu onglets ────────────────────────────────────
                        when (selectedTab) {
                            0 -> {
                                if (diff != null && diff.ingredients.isNotEmpty()) {
                                    diff.ingredients.forEach { DiffIngredientCard(it, diffModeActive) }
                                } else if (recipe.ingredients.isNotEmpty()) {
                                    HorizontalDivider(color = Color(0xFFF1F5F9))
                                    recipe.ingredients.forEach { IngredientCheckRow(it) }
                                } else {
                                    Text(
                                        "Aucun ingrédient renseigné",
                                        color = Gray500,
                                        style = Typography.bodySmall,
                                        modifier = Modifier.padding(vertical = 16.dp)
                                    )
                                }
                            }
                            1 -> {
                                if (diff != null && diff.steps.isNotEmpty()) {
                                    Spacer(Modifier.height(8.dp))
                                    diff.steps.forEach { DiffStepCard(it, diffModeActive) }
                                } else if (recipe.steps.isNotEmpty()) {
                                    Spacer(Modifier.height(8.dp))
                                    recipe.steps.forEach { StepCard(it) }
                                } else {
                                    Text(
                                        "Aucune étape renseignée",
                                        color = Gray500,
                                        style = Typography.bodySmall,
                                        modifier = Modifier.padding(vertical = 16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                    }
                }
            }

            // ── Boutons flottants sur l'image ─────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Bouton retour
                Surface(
                    onClick = onBack,
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color(0xFF1E293B),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                // Bouton like circulaire (authentifié + email confirmé)
                if (isAuthenticated && ForkEatApi.isEmailVerified()) {
                    LikeButtonCircle(
                        isLiked = isLiked,
                        onToggle = {
                            isLiked = !isLiked
                            likeCount += if (isLiked) 1 else -1
                            if (isLiked) recipesViewModel.likeRecipe(recipe.id)
                            else recipesViewModel.unlikeRecipe(recipe.id)
                        }
                    )
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// INGRÉDIENT AVEC CASE À COCHER
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun IngredientCheckRow(ingredient: RecipeIngredientDTO) {
    var checked by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { checked = !checked }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = CheckboxDefaults.colors(checkedColor = Primary500)
        )
        Spacer(Modifier.width(4.dp))
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${formatQty(ingredient.quantity)} ${ingredient.unit}",
                color = if (checked) Color(0xFFCBD5E1) else Primary500,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.width(72.dp),
                textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
            )
            Text(
                text = ingredient.name,
                style = Typography.bodyMedium,
                color = if (checked) Color(0xFFCBD5E1) else Color(0xFF1E293B),
                textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None,
                modifier = Modifier.weight(1f)
            )
        }
    }
    HorizontalDivider(color = Color(0xFFF1F5F9))
}

// ══════════════════════════════════════════════════════════════════════════════
// BOUTON LIKE CIRCULAIRE (OVERLAY)
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun LikeButtonCircle(
    isLiked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heartColor by animateColorAsState(
        targetValue = if (isLiked) Color(0xFFFF4D6D) else Color(0xFF64748B),
        animationSpec = tween(300),
        label = "heartColor"
    )
    Surface(
        onClick = onToggle,
        modifier = modifier.size(42.dp),
        shape = CircleShape,
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isLiked) "Unlike" else "Like",
                tint = heartColor,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// BANNIÈRE VARIANTE
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun VariantBanner(
    parentTitle: String,
    diffModeActive: Boolean,
    onToggleDiffMode: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = Color(0xFFEEF2FF),
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text("⎇", color = Primary500, fontSize = 16.sp)
                    }
                }
                Column {
                    Text("Variante de", style = Typography.labelSmall, color = Gray500)
                    Text(
                        text = parentTitle,
                        style = Typography.labelMedium,
                        color = Secondary700,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = if (diffModeActive) "Diff" else "Complet",
                    style = Typography.labelSmall,
                    color = if (diffModeActive) Secondary700 else Gray500,
                    fontWeight = if (diffModeActive) FontWeight.SemiBold else FontWeight.Normal
                )
                Switch(
                    checked = diffModeActive,
                    onCheckedChange = { onToggleDiffMode() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Primary500,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD1D5DB)
                    )
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// BADGE DIFF (AJOUTÉ / SUPPRIMÉ / MODIFIÉ)
// ══════════════════════════════════════════════════════════════════════════════

@Composable
private fun DiffBadge(type: RecipeDiffDTO.DiffType) {
    if (type == RecipeDiffDTO.DiffType.UNCHANGED) return
    val (label, textColor, bgColor) = when (type) {
        RecipeDiffDTO.DiffType.ADDED    -> Triple("Ajouté",   Color(0xFF166534), Color(0xFFDCFCE7))
        RecipeDiffDTO.DiffType.REMOVED  -> Triple("Supprimé", Color(0xFF9F1239), Color(0xFFFFE4E6))
        RecipeDiffDTO.DiffType.MODIFIED -> Triple("Modifié",  Color(0xFFB45309), Color(0xFFFFFBEB))
        RecipeDiffDTO.DiffType.UNCHANGED -> return
    }
    Surface(color = bgColor, shape = RoundedCornerShape(50.dp)) {
        Text(
            text = label,
            style = Typography.labelSmall,
            color = textColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// ALLERGÈNES
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun AllergenBadges(allergens: List<AllergenDTO>) {
    Row(
        Modifier
            .padding(vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        allergens.forEach { allergen ->
            Surface(
                color = Color(0xFFFFF3CD),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFFC107))
            ) {
                Text(
                    text = allergen.name.lowercase(),
                    color = Color(0xFFFFA000),
                    style = Typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun DiffAllergenBadges(
    allergens: List<RecipeDiffDTO.AllergenDiffDTO>,
    diffModeActive: Boolean
) {
    Row(
        Modifier
            .padding(vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        allergens.forEach { allergen ->
            if (diffModeActive && allergen.type == RecipeDiffDTO.DiffType.UNCHANGED) return@forEach
            val isRemoved = allergen.type == RecipeDiffDTO.DiffType.REMOVED
            val isAdded   = allergen.type == RecipeDiffDTO.DiffType.ADDED
            val bgColor = when (allergen.type) {
                RecipeDiffDTO.DiffType.ADDED    -> Color(0xFFDCFCE7)
                RecipeDiffDTO.DiffType.REMOVED  -> Color(0xFFFFE4E6)
                else                            -> Color(0xFFFFF3CD)
            }
            val textColor = when (allergen.type) {
                RecipeDiffDTO.DiffType.ADDED    -> Color(0xFF166534)
                RecipeDiffDTO.DiffType.REMOVED  -> Color(0xFF9F1239)
                else                            -> Color(0xFFFFA000)
            }
            val borderColor = when (allergen.type) {
                RecipeDiffDTO.DiffType.ADDED    -> Color(0xFF86EFAC)
                RecipeDiffDTO.DiffType.REMOVED  -> Color(0xFFFCA5A5)
                else                            -> Color(0xFFFFC107)
            }
            Surface(
                color = bgColor,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = allergen.name.lowercase(),
                        color = textColor,
                        style = Typography.labelMedium,
                        textDecoration = if (isRemoved) TextDecoration.LineThrough else TextDecoration.None
                    )
                    if (allergen.type != RecipeDiffDTO.DiffType.UNCHANGED) {
                        Text(
                            text = if (isAdded) "Ajouté" else "Supprimé",
                            color = textColor,
                            style = Typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// RÉGIMES ALIMENTAIRES
// ══════════════════════════════════════════════════════════════════════════════

private fun dietaryFlagLabel(key: String) = when (key) {
    "vegan"       -> "Vegan"
    "vegetarian"  -> "Végétarien"
    "gluten-free" -> "Sans gluten"
    "dairy-free"  -> "Sans lactose"
    "halal"       -> "Halal"
    "kosher"      -> "Casher"
    "nut-free"    -> "Sans noix"
    "low-carb"    -> "Low-carb"
    else          -> key
}

@Composable
private fun DietaryFlagBadges(flags: Map<String, Boolean>) {
    Row(
        Modifier
            .padding(vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        flags.filter { it.value }.forEach { (key, _) ->
            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFA5D6A7))
            ) {
                Text(
                    text = dietaryFlagLabel(key),
                    color = Color(0xFF2E7D32),
                    style = Typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun DiffDietaryFlagBadges(
    flags: List<RecipeDiffDTO.DietaryFlagDiffDTO>,
    diffModeActive: Boolean
) {
    Row(
        Modifier
            .padding(vertical = 4.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        flags.forEach { flag ->
            if (diffModeActive && flag.type == RecipeDiffDTO.DiffType.UNCHANGED) return@forEach
            val isRemoved = flag.type == RecipeDiffDTO.DiffType.REMOVED
            val isAdded   = flag.type == RecipeDiffDTO.DiffType.ADDED
            val bgColor = when (flag.type) {
                RecipeDiffDTO.DiffType.ADDED    -> Color(0xFFDCFCE7)
                RecipeDiffDTO.DiffType.REMOVED  -> Color(0xFFFFE4E6)
                else                            -> Color(0xFFE8F5E9)
            }
            val textColor = when (flag.type) {
                RecipeDiffDTO.DiffType.ADDED    -> Color(0xFF166534)
                RecipeDiffDTO.DiffType.REMOVED  -> Color(0xFF9F1239)
                else                            -> Color(0xFF2E7D32)
            }
            val borderColor = when (flag.type) {
                RecipeDiffDTO.DiffType.ADDED    -> Color(0xFF86EFAC)
                RecipeDiffDTO.DiffType.REMOVED  -> Color(0xFFFCA5A5)
                else                            -> Color(0xFFA5D6A7)
            }
            Surface(
                color = bgColor,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = dietaryFlagLabel(flag.flagName),
                        color = textColor,
                        style = Typography.labelMedium,
                        textDecoration = if (isRemoved) TextDecoration.LineThrough else TextDecoration.None
                    )
                    if (flag.type != RecipeDiffDTO.DiffType.UNCHANGED) {
                        Text(
                            text = if (isAdded) "Ajouté" else "Retiré",
                            color = textColor,
                            style = Typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// INGRÉDIENTS (version sans case à cocher, utilisée par le mode diff)
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun IngredientCard(ingredient: RecipeIngredientDTO) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${formatQty(ingredient.quantity)} ${ingredient.unit}",
                color = Primary500,
                style = Typography.labelMedium,
                modifier = Modifier.width(80.dp)
            )
            Text(text = ingredient.name, style = Typography.bodyMedium, color = Secondary700)
        }
    }
}

@Composable
private fun DiffIngredientCard(
    ingredient: RecipeDiffDTO.IngredientDiffDTO,
    diffModeActive: Boolean
) {
    if (diffModeActive && ingredient.type == RecipeDiffDTO.DiffType.UNCHANGED) return
    val isRemoved  = ingredient.type == RecipeDiffDTO.DiffType.REMOVED
    val isModified = ingredient.type == RecipeDiffDTO.DiffType.MODIFIED
    val isAdded    = ingredient.type == RecipeDiffDTO.DiffType.ADDED
    val bgColor = when (ingredient.type) {
        RecipeDiffDTO.DiffType.ADDED    -> Color(0xFFDCFCE7)
        RecipeDiffDTO.DiffType.REMOVED  -> Color(0xFFFFE4E6)
        RecipeDiffDTO.DiffType.MODIFIED -> Color(0xFFFFF8E1)
        RecipeDiffDTO.DiffType.UNCHANGED -> Color.White
    }
    val borderColor = when (ingredient.type) {
        RecipeDiffDTO.DiffType.ADDED    -> Color(0xFF86EFAC)
        RecipeDiffDTO.DiffType.REMOVED  -> Color(0xFFFCA5A5)
        RecipeDiffDTO.DiffType.MODIFIED -> Color(0xFFFCD34D)
        RecipeDiffDTO.DiffType.UNCHANGED -> Color(0xFFE5E7EB)
    }
    val nameColor = when {
        isRemoved  -> Color(0xFF9F1239)
        isModified -> Color(0xFFB45309)
        isAdded    -> Color(0xFF166534)
        else       -> Secondary700
    }
    val qtyColor = when {
        isRemoved  -> Color(0xFF9F1239)
        isModified -> Color(0xFFB45309)
        isAdded    -> Color(0xFF166534)
        else       -> Primary500
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.width(90.dp)) {
                    if (isModified) {
                        Text(
                            text = "${formatQty(ingredient.originalQuantity)} ${ingredient.originalUnit}",
                            style = Typography.labelSmall,
                            color = Color(0xFFF59E0B),
                            textDecoration = TextDecoration.LineThrough
                        )
                    }
                    Text(
                        text = "${formatQty(ingredient.quantity)} ${ingredient.unit}",
                        color = qtyColor,
                        style = Typography.labelMedium,
                        textDecoration = if (isRemoved) TextDecoration.LineThrough else TextDecoration.None
                    )
                }
                Text(
                    text = ingredient.name,
                    style = Typography.bodyMedium,
                    color = nameColor,
                    textDecoration = if (isRemoved) TextDecoration.LineThrough else TextDecoration.None
                )
            }
            DiffBadge(ingredient.type)
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// ÉTAPES
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun StepCard(step: RecipeStepDTO) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(28.dp)) {
                Surface(color = Primary500, shape = CircleShape, modifier = Modifier.matchParentSize()) {}
                Text(text = step.stepNumber.toString(), color = Color.White, style = Typography.labelMedium)
            }
            Spacer(Modifier.width(12.dp))
            Text(text = step.instruction, style = Typography.bodyMedium, color = Secondary700)
        }
    }
}

@Composable
private fun DiffStepCard(
    step: RecipeDiffDTO.StepDiffDTO,
    diffModeActive: Boolean
) {
    if (diffModeActive && step.type == RecipeDiffDTO.DiffType.UNCHANGED) return
    val isRemoved  = step.type == RecipeDiffDTO.DiffType.REMOVED
    val isModified = step.type == RecipeDiffDTO.DiffType.MODIFIED
    val isAdded    = step.type == RecipeDiffDTO.DiffType.ADDED
    val bgColor = when (step.type) {
        RecipeDiffDTO.DiffType.ADDED    -> Color(0xFFDCFCE7)
        RecipeDiffDTO.DiffType.REMOVED  -> Color(0xFFFFE4E6)
        RecipeDiffDTO.DiffType.MODIFIED -> Color(0xFFFFF8E1)
        RecipeDiffDTO.DiffType.UNCHANGED -> Color(0xFFF8FAFC)
    }
    val borderColor = when (step.type) {
        RecipeDiffDTO.DiffType.ADDED    -> Color(0xFF86EFAC)
        RecipeDiffDTO.DiffType.REMOVED  -> Color(0xFFFCA5A5)
        RecipeDiffDTO.DiffType.MODIFIED -> Color(0xFFFCD34D)
        RecipeDiffDTO.DiffType.UNCHANGED -> Color(0xFFE5E7EB)
    }
    val numberColor = when (step.type) {
        RecipeDiffDTO.DiffType.ADDED    -> Color(0xFF22C55E)
        RecipeDiffDTO.DiffType.REMOVED  -> Color(0xFFF87171)
        RecipeDiffDTO.DiffType.MODIFIED -> Color(0xFFF59E0B)
        RecipeDiffDTO.DiffType.UNCHANGED -> Color(0xFF9CA3AF)
    }
    val textColor = when {
        isRemoved  -> Color(0xFF9F1239)
        isModified -> Color(0xFFB45309)
        isAdded    -> Color(0xFF166534)
        else       -> Secondary700
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(28.dp)) {
                Surface(color = numberColor, shape = CircleShape, modifier = Modifier.matchParentSize()) {}
                Text(text = step.stepNumber.toString(), color = Color.White, style = Typography.labelMedium)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = step.instruction,
                        style = Typography.bodyMedium,
                        color = textColor,
                        textDecoration = if (isRemoved) TextDecoration.LineThrough else TextDecoration.None,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    DiffBadge(step.type)
                }
                val originalInstruction = step.originalInstruction
                if (isModified && !originalInstruction.isNullOrEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    HorizontalDivider(color = Color(0xFFFCD34D), thickness = 1.dp)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "Original",
                            style = Typography.labelSmall,
                            color = Color(0xFFF59E0B),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = originalInstruction,
                            style = Typography.bodySmall,
                            color = Color(0xFFF59E0B),
                            textDecoration = TextDecoration.LineThrough,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// BOUTON LIKE (version complète conservée pour compatibilité)
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun LikeButton(
    initialCount: Int = 0,
    isLike: Boolean = false,
    recipeId: UUID,
    modifier: Modifier = Modifier
) {
    val recipesViewModel: RecipesViewModel = viewModel()
    var isLiked by remember { mutableStateOf(isLike) }
    var count by remember { mutableIntStateOf(initialCount) }

    LaunchedEffect(isLike) { isLiked = isLike }
    LaunchedEffect(initialCount) { count = initialCount }

    val heartColor by animateColorAsState(
        targetValue = if (isLiked) Color(0xFFFF4D6D) else Color(0xFF9E9E9E),
        animationSpec = tween(durationMillis = 300),
        label = "heartColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isLiked) Color(0xFFFF4D6D) else Color(0xFFE0E0E0),
        animationSpec = tween(durationMillis = 300),
        label = "borderColor"
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (isLiked) Color(0xFFFF4D6D).copy(alpha = 0.1f) else Color.White,
        animationSpec = tween(durationMillis = 300),
        label = "backgroundColor"
    )

    Surface(
        onClick = {
            isLiked = !isLiked
            count = if (isLiked) count + 1 else count - 1
            if (isLiked) recipesViewModel.likeRecipe(recipeId)
            else recipesViewModel.unlikeRecipe(recipeId)
        },
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = if (isLiked) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val heartScale by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "heartScale"
            )
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isLiked) "Unlike" else "Like",
                tint = heartColor,
                modifier = Modifier.size(22.dp).scale(heartScale)
            )
            Text(
                text = formatCount(count),
                color = if (isLiked) Color(0xFFFF4D6D) else Color(0xFF616161),
                fontSize = 14.sp,
                fontWeight = if (isLiked) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// UTILITAIRES
// ══════════════════════════════════════════════════════════════════════════════

private fun formatQty(qty: Double): String =
    if (qty == qty.toLong().toDouble()) qty.toLong().toString() else qty.toString()

fun formatCount(count: Int): String = when {
    count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000f)
    count >= 1_000     -> String.format("%.1fk", count / 1_000f)
    else               -> count.toString()
}

private fun formatDuration(minutes: Int): String = when {
    minutes >= 60 -> {
        val h = minutes / 60
        val m = minutes % 60
        if (m == 0) "${h}h" else "${h}h${m}min"
    }
    else -> "${minutes} min"
}

// ══════════════════════════════════════════════════════════════════════════════
// PREVIEW
// ══════════════════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
fun PreviewRecipeDetailScreen() {
    val exampleRecipe = RecipeDetailsDTO(
        id = UUID.randomUUID(),
        title = "Lasagnes Végétariennes Maison",
        summary = "Une lasagne savoureuse aux épinards et ricotta, dorée à la perfection.",
        parentId = null,
        username = "chef_mario",
        preparationMinutes = 45,
        imageUrl = "https://images.unsplash.com/photo-1504674900247-0877df9cc836",
        status = "PUBLISHED",
        steps = listOf(
            RecipeStepDTO(1, "Préchauffez le four à 180°C."),
            RecipeStepDTO(2, "Faites revenir les épinards avec l'ail dans une poêle.")
        ),
        ingredients = listOf(
            RecipeIngredientDTO("Feuilles de lasagne", 12.0, "feuilles"),
            RecipeIngredientDTO("Ricotta fraîche", 250.0, "g"),
            RecipeIngredientDTO("Épinards frais", 500.0, "g"),
            RecipeIngredientDTO("Sauce tomate", 400.0, "ml")
        ),
        allergens = listOf(
            AllergenDTO("1", "Gluten", "élevé"),
            AllergenDTO("2", "Lait", "moyen")
        ),
        dietaryFlags = mapOf("vegetarian" to true, "vegan" to false),
        createdAt = Instant.parse("2026-02-10T12:00:00Z"),
        updatedAt = Instant.parse("2026-02-10T12:00:00Z"),
        nbLike = 120,
        hasLiked = false
    )
    RecipeDetailScreen(recipe = exampleRecipe, onBack = {}, isAuthenticated = true)
}

@Preview(showBackground = true, name = "Variante avec diff")
@Composable
fun PreviewRecipeDetailScreenWithDiff() {
    val parentRecipe = RecipeDTO(
        id = UUID.randomUUID(),
        title = "Tarte aux pommes classique",
        summary = "Recette originale",
        parentId = null,
        username = "mamie_jeanne",
        preparationMinutes = 60,
        imageUrl = "",
        status = "PUBLISHED",
        steps = emptyList(),
        ingredients = emptyList(),
        allergens = emptyList(),
        dietaryFlags = emptyMap(),
        createdAt = Instant.parse("2026-02-10T12:00:00Z"),
        updatedAt = Instant.parse("2026-02-10T12:00:00Z"),
        nbLike = 5,
        hasLiked = false
    )
    val diff = RecipeDiffDTO(
        titleChanged = true,
        originalTitle = "Tarte aux pommes classique",
        timeDelta = 15,
        ingredients = listOf(
            RecipeDiffDTO.IngredientDiffDTO(RecipeDiffDTO.DiffType.UNCHANGED, "Farine", 250.0, "g"),
            RecipeDiffDTO.IngredientDiffDTO(RecipeDiffDTO.DiffType.ADDED, "Cannelle", 1.0, "c. à café"),
            RecipeDiffDTO.IngredientDiffDTO(RecipeDiffDTO.DiffType.REMOVED, "Vanille", 1.0, "gousse"),
            RecipeDiffDTO.IngredientDiffDTO(RecipeDiffDTO.DiffType.MODIFIED, "Beurre", 150.0, "g", 125.0, "g")
        ),
        steps = listOf(
            RecipeDiffDTO.StepDiffDTO(RecipeDiffDTO.DiffType.UNCHANGED, 1, "Préparez la pâte."),
            RecipeDiffDTO.StepDiffDTO(RecipeDiffDTO.DiffType.MODIFIED, 2, "Ajoutez la cannelle.", "Ajoutez la vanille."),
            RecipeDiffDTO.StepDiffDTO(RecipeDiffDTO.DiffType.ADDED, 3, "Saupoudrez de cassonade.")
        ),
        allergens = listOf(
            RecipeDiffDTO.AllergenDiffDTO(RecipeDiffDTO.DiffType.UNCHANGED, "Gluten"),
            RecipeDiffDTO.AllergenDiffDTO(RecipeDiffDTO.DiffType.ADDED, "Noix")
        )
    )
    val recipe = RecipeDetailsDTO(
        id = UUID.randomUUID(),
        title = "Tarte aux pommes maison croustillante",
        summary = "Ma version améliorée de la tarte.",
        parentId = parentRecipe.id,
        username = "chef_alex",
        preparationMinutes = 75,
        imageUrl = "",
        status = "PUBLISHED",
        steps = listOf(RecipeStepDTO(1, "Préparez la pâte."), RecipeStepDTO(2, "Ajoutez la cannelle.")),
        ingredients = listOf(RecipeIngredientDTO("Farine", 250.0, "g")),
        allergens = listOf(AllergenDTO("1", "Gluten", "élevé")),
        dietaryFlags = mapOf("vegetarian" to true),
        createdAt = Instant.parse("2026-02-10T12:00:00Z"),
        updatedAt = Instant.parse("2026-02-10T12:00:00Z"),
        nbLike = 3,
        hasLiked = false
    )
    RecipeDetailScreen(
        recipe = recipe,
        parent = parentRecipe,
        diff = diff,
        isOwner = true,
        isAuthenticated = true,
        onBack = {}
    )
}
