package io.nebula.xenogithub

import android.util.Log
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import io.nebula.xenogithub.biz.KVStorageImpl
import io.nebula.xenogithub.biz.network.XenoNet
import io.nebula.xenogithub.viewmodel.RepoIssuesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by nebula on 2025/3/7
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class RepoIssuesViewModelTest {

    private lateinit var viewModel: RepoIssuesViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        mockkObject(KVStorageImpl)
        mockkObject(XenoNet)
        mockkStatic(Log::class)

        every { Log.e(any(), any()) } answers {
            println("ERROR: ${secondArg<String>()}")
            0
        }

        viewModel = RepoIssuesViewModel(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun test_loadIssues() = runTest {
        every { KVStorageImpl.getAccessToken(any()) } returns "valid_token"
        coEvery {
            XenoNet.loadIssuesForRepo(
                "valid_token",
                "owner",
                "repo"
            )
        } returns Result.success(
            listOf(
                mockIssue
            )
        )

        viewModel.loadIssues(mockk(), "owner", "repo")

        assert(viewModel.uiState.value.data.isNotEmpty())
        assert(!viewModel.uiState.value.isError)
        assert(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun test_loadIssues_with_network_error() = runTest {
        every { KVStorageImpl.getAccessToken(any()) } returns "valid_token"
        coEvery { XenoNet.loadIssuesForRepo(any(), any(), any()) } returns Result.failure(
            RuntimeException("Timeout")
        )

        viewModel.loadIssues(mockk(), "owner", "repo")

        assert(viewModel.uiState.value.isError)
        assert(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun test_raiseIssue_success() = runTest {
        every { KVStorageImpl.getAccessToken(any()) } returns "valid_token"
        val mockIssue = mockIssue
        coEvery {
            XenoNet.raiseIssue("valid_token", "owner", "repo", "title", "body")
        } returns Result.success(mockIssue)

        viewModel.onIssueTitleChange("title")
        viewModel.onIssueBodyChange("body")

        viewModel.raiseIssue(mockk(), "owner", "repo")

        assert(viewModel.uiState.value.data.first().id == mockIssue.id)
    }

    @Test
    fun test_raiseIssue_error() = runTest {
        every { KVStorageImpl.getAccessToken(any()) } returns "valid_token"
        coEvery { XenoNet.raiseIssue(any(), any(), any(), any(), any()) } returns Result.failure(
            RuntimeException("Error")
        )

        viewModel.raiseIssue(mockk(), "owner", "repo")

        assert(viewModel.uiState.value.isError)
    }


    @Test
    fun test_onIssueTitleChange() {
        viewModel.onIssueTitleChange("New Title")
        assert(viewModel.uiState.value.issueTitle == "New Title")
    }

    @Test
    fun test_onIssueBodyChange() {
        viewModel.onIssueBodyChange("New Body")
        assert(viewModel.uiState.value.issueBody == "New Body")
    }
}