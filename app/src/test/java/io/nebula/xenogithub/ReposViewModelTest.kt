package io.nebula.xenogithub

/**
 * Created by nebula on 2025/3/6
 */
import android.util.Log
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.nebula.xenogithub.biz.KVStorageImpl
import io.nebula.xenogithub.biz.manager.IAccountManager
import io.nebula.xenogithub.biz.network.XenoNet
import io.nebula.xenogithub.viewmodel.ReposViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class ReposViewModelTest {
    @get:Rule val testRule = TestCoroutineRule()
    @MockK lateinit var mockAccountManager: IAccountManager
    private lateinit var viewModel: ReposViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        mockkObject(XenoNet)
        mockkObject(KVStorageImpl)
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        viewModel = ReposViewModel(testDispatcher, mockAccountManager)
    }

    @After fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test fun test_load_repos_success() = testRule.runTest {
        coEvery { XenoNet.retrieveTrendingRepos() } returns Result.success(listOf(mockRepo))
        viewModel.loadRepos()
        assert(viewModel.uiState.value.data.size == 1)
        assert(!viewModel.uiState.value.isError)
    }

    @Test fun test_load_repos_failure() = testRule.runTest {
        coEvery { XenoNet.retrieveTrendingRepos() } returns Result.failure(RuntimeException(""))
        viewModel.loadRepos()
        assert(viewModel.uiState.value.isError)
    }

    @Test fun test_is_signed_true() {
        every { mockAccountManager.currentAccount() } returns mockUser
        assert(viewModel.isSigned())
    }

    @Test fun test_is_signed_false() {
        every { mockAccountManager.currentAccount() } returns null
        assert(!viewModel.isSigned())
    }

    @ExperimentalCoroutinesApi
    class TestCoroutineRule : TestWatcher() {
        private val testDispatcher = StandardTestDispatcher()
        override fun starting(description: Description) { Dispatchers.setMain(testDispatcher) }
        override fun finished(description: Description) { Dispatchers.resetMain() }
        fun runTest(block: suspend TestScope.() -> Unit) = runTest(testDispatcher) { block() }
    }
}