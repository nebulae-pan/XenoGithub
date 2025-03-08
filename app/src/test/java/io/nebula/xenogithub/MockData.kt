package io.nebula.xenogithub

import io.nebula.xenogithub.biz.model.Issue
import io.nebula.xenogithub.biz.model.IssueUser
import io.nebula.xenogithub.biz.model.RepoOwner
import io.nebula.xenogithub.biz.model.Repository
import io.nebula.xenogithub.biz.model.User

/**
 * Created by nebula on 2025/3/7
 */
private val avatarUrl = "https://avatars.githubusercontent.com/u/202092369?v=4&size=64"
val mockUser = User(
    name = "mockUser",
    username = "mockUserName",
    avatarUrl = avatarUrl,
    following = 123,
    followers = 456,
)

val mockRepo = Repository(
    1,
    "mockName",
    "mockTest/test",
    "mockLong test text,long test text,long test text,long test text,long test text,long test text,long test text",
    "python",
    100,
    100,
    RepoOwner("123", ""),
    ""
)

val mockIssue = Issue(
    id = 0,
    number = 1,
    title = "mockTitle",
    body = "mockBody",
    user = IssueUser("mockUserName", "mockName", avatarUrl),
)