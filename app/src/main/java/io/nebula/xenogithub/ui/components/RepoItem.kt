package io.nebula.xenogithub.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.nebula.xenogithub.R
import io.nebula.xenogithub.biz.model.RepoOwner
import io.nebula.xenogithub.biz.model.Repository
import java.util.Locale

/**
 * Created by nebula on 2025/3/6
 */
@Composable
fun RepoItem(repo: Repository, modifier: Modifier = Modifier, itemOnClick: () -> Unit = {}) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { itemOnClick.invoke() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = modifier
                .padding(16.dp)
        ) {
            Row {
                Avatar(repo.owner.avatarUrl, Modifier.size(32.dp))
                Text(
                    text = repo.fullName,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            if (repo.description != "") {
                Text(
                    text = repo.description,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconText(R.drawable.ic_star) {
                    Text(
                        text = formatNumber(repo.stargazersCount),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                IconText(R.drawable.ic_fork) {
                    Text(
                        text = formatNumber(repo.forksCount),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                repo.language?.let {
                    val color = remember(it) { stringToColor(it) }
                    IconText(circlePainter(color)) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                }
            }
        }
    }
}

fun circlePainter(color: Color): Painter = object : Painter() {
    override val intrinsicSize get() = Size.Unspecified

    override fun applyAlpha(alpha: Float): Boolean = true

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean = true

    override fun DrawScope.onDraw() {
        drawCircle(
            brush = SolidColor(color),
            radius = size.minDimension / 2 - 4
        )
    }
}

fun stringToColor(input: String): Color {
    val hash = input.hashCode()

    var r = (hash shr 16) and 0xFF
    var g = (hash shr 8) and 0xFF
    var b = hash and 0xFF

    r = (r * 1.2).toInt().coerceAtMost(255)
    g = (g * 1.2).toInt().coerceAtMost(255)
    b = (b * 1.2).toInt().coerceAtMost(255)

    return Color(red = r, green = g, blue = b)
}

private fun formatNumber(number: Int): String {
    return when {
        number > 999_999 -> String.format(Locale.getDefault(), "%.1fM", number / 1_000_000.0)
        number > 999 -> String.format(Locale.getDefault(), "%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    RepoItem(
        Repository(
            1,
            "test",
            "test/test",
            "long test text,long test text,long test text,long test text,long test text,long test text,long test text",
            "python",
            100,
            100,
            RepoOwner("123", ""),
            ""
        )
    )
}