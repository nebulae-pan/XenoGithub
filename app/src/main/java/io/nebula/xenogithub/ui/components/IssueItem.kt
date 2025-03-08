package io.nebula.xenogithub.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.nebula.xenogithub.biz.model.Issue
import io.nebula.xenogithub.biz.model.IssueUser

/**
 * Created by nebula on 2025/3/6
 */
@Composable
fun IssueItem(issue: Issue, modifier: Modifier = Modifier, itemOnClick: () -> Unit = {}) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = modifier
            .padding(16.dp)
            .clickable { itemOnClick.invoke() }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = issue.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.widthIn(max = 200.dp)
                )
                Text(
                    text = "#${issue.number}", Modifier.padding(start = 4.dp),
                    color = Color.Gray
                )
                Spacer(Modifier.weight(1f))
                Avatar(
                    issue.user.avatarUrl, Modifier
                        .size(32.dp)
                        .padding(end = 16.dp)
                )
            }

            Text(
                text = issue.body ?: "",
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    IssueItem(
        Issue(
            1,
            1,
            "title",
            "body",
            IssueUser("", "", ""),
        )
    )
}