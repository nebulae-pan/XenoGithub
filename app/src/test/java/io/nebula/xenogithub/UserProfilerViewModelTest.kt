package io.nebula.xenogithub

import android.util.Log
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.nebula.xenogithub.biz.network.XenoNet
import io.nebula.xenogithub.viewmodel.UserProfilerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by nebula on 2025/3/7
 */

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class UserProfilerViewModelTest {
    @get:Rule
    val testRule = TestCoroutineRule()
    private lateinit var viewModel: UserProfilerViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        mockkObject(XenoNet)
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        viewModel = UserProfilerViewModel(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun test_load_repos_null_user() = testRule.runTest {
        viewModel.loadUserRepos(null)
        assert(viewModel.uiState.value.data.isEmpty())
        assert(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun test_load_repos_success() = testRule.runTest {
        val mockUser = mockUser.copy(_token = "valid_token")
        val mockRepo = mockRepo.copy(id = 1)
        coEvery { XenoNet.loadReposFromUser("valid_token") } returns Result.success(listOf(mockRepo))
        viewModel.loadUserRepos(mockUser)
        assert(viewModel.uiState.value.data.size == 1)
        assert(viewModel.uiState.value.data.first().id == 1)
        assert(!viewModel.uiState.value.isError)
    }

    @Test
    fun test_load_repos_failure() = testRule.runTest {
        val mockUser = mockUser.copy(_token = "invalid_token")
        coEvery { XenoNet.loadReposFromUser(any()) } returns Result.failure(RuntimeException(""))
        viewModel.loadUserRepos(mockUser)
        assert(viewModel.uiState.value.isError)
    }

    @ExperimentalCoroutinesApi
    class TestCoroutineRule : TestWatcher() {
        private val testDispatcher = StandardTestDispatcher()
        override fun starting(description: Description) {
            Dispatchers.setMain(testDispatcher)
        }

        override fun finished(description: Description) {
            Dispatchers.resetMain()
        }

        fun runTest(block: suspend TestScope.() -> Unit) =
            kotlinx.coroutines.test.runTest(testDispatcher) { block() }
    }
}