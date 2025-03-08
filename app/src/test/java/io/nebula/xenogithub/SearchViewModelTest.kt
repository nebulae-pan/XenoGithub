package io.nebula.xenogithub

import android.util.Log
import io.mockk.*
import io.nebula.xenogithub.biz.network.XenoNet
import io.nebula.xenogithub.viewmodel.SearchViewModel
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

/**
 * Created by nebula on 2025/3/7
 */
@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class SearchViewModelTest {
    @get:Rule
    val testRule = TestCoroutineRule()
    private lateinit var viewModel: SearchViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
        mockkObject(XenoNet)
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        viewModel = SearchViewModel(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun test_update_search_content() {
        viewModel.onUpdateSearchContent("test")
        assert(viewModel.searchContent == "test")
    }

    @Test
    fun test_change_language() {
        viewModel.onLanguageChange("Java")
        assert(viewModel.uiState.value.language == "Java")
    }

    @Test
    fun test_start_search_empty_content() = testRule.runTest {
        viewModel.onUpdateSearchContent("")
        viewModel.startSearch()
        assert(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun test_start_search_success() = testRule.runTest {
        coEvery { XenoNet.reposCommonSearch(any(), any()) } returns Result.success(listOf(mockRepo))
        viewModel.onUpdateSearchContent("kotlin")
        viewModel.startSearch()
        assert(viewModel.uiState.value.repos.size == 1)
        assert(!viewModel.uiState.value.isError)
    }

    @Test
    fun test_start_search_failure() = testRule.runTest {
        coEvery { XenoNet.reposCommonSearch(any(), any()) } returns Result.failure(
            RuntimeException(
                ""
            )
        )
        viewModel.onUpdateSearchContent("android")
        viewModel.startSearch()
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

        fun runTest(block: suspend TestScope.() -> Unit) = runTest(testDispatcher) { block() }
    }
}