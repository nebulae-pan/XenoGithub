package io.nebula.xenogithub

import android.util.Log
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import io.nebula.xenogithub.biz.KVStorageImpl
import io.nebula.xenogithub.biz.manager.IAccountManager
import io.nebula.xenogithub.biz.network.XenoNet
import io.nebula.xenogithub.viewmodel.AccountViewModel
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
class AccountViewModelTest {
    @MockK
    lateinit var mockAccountManager: IAccountManager

    private lateinit var viewModel: AccountViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher) // 设置测试协程调度器

        // mock Object KVStorageImpl and XenoNet
        mockkObject(KVStorageImpl)
        mockkObject(XenoNet)
        mockkStatic(Log::class)

        every { mockAccountManager.update(any()) } just Runs
        every { Log.e(any(), any()) } answers {
            val tag = firstArg<String>()
            val msg = secondArg<String>()
            println("[TEST ERROR] $tag: $msg")
            0
        }

        viewModel = AccountViewModel(testDispatcher, mockAccountManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll() //dispose all
    }

    @Test
    fun test_oauth_refresh_user() = runTest {
        every { KVStorageImpl.getAccessToken(any()) } returns "valid_token"
        every { KVStorageImpl.getAccessCode(any()) } returns ""

        val mockUser = mockUser.copy(_token = "valid_token")
        coEvery { XenoNet.refresh("valid_token") } returns Result.success(mockUser)

        viewModel.oauth(mockk())

        assert(viewModel.uiState.value.oauth)
        assert(viewModel.uiState.value.user?.token == "valid_token")
        verify { mockAccountManager.update(mockUser) }
        assert(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun test_oauth_authenticate() = runTest {
        every { KVStorageImpl.getAccessToken(any()) } returns ""
        every { KVStorageImpl.getAccessCode(any()) } returns "valid_code"
        coEvery { KVStorageImpl.saveAccessToken(any(), any()) } just Runs

        // mock XenoNet.oauth success

        val mockUser = mockUser.copy(_token = "new_token")
        coEvery { XenoNet.oauth("valid_code") } returns Result.success(mockUser)

        viewModel.oauth(mockk())

        assert(viewModel.uiState.value.oauth)
        coVerify { KVStorageImpl.saveAccessToken(any(), "new_token") }
        verify { mockAccountManager.update(mockUser) }
        assert(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun test_oauth() = runTest {
        every { KVStorageImpl.getAccessToken(any()) } returns ""
        every { KVStorageImpl.getAccessCode(any()) } returns ""

        viewModel.oauth(mockk())

        assert(!viewModel.uiState.value.oauth)
        assert(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun test_oauth_with_network_error() = runTest {
        every { KVStorageImpl.getAccessToken(any()) } returns "invalid"
        every { KVStorageImpl.getAccessCode(any()) } returns ""
        coEvery { XenoNet.refresh(any()) } returns Result.failure(RuntimeException("Network error"))

        viewModel.oauth(mockk())

        assert(viewModel.uiState.value.isError)
        assert(!viewModel.uiState.value.isLoading)
    }

    @Test
    fun test_signOut() = runTest(testDispatcher.scheduler) {
        coEvery { KVStorageImpl.saveAccessCode(any(), "") } just Runs
        coEvery { KVStorageImpl.saveAccessToken(any(), "") } just Runs

        viewModel.signOut(mockk())

        coVerify(exactly = 1) {
            KVStorageImpl.saveAccessCode(any(), "")
            KVStorageImpl.saveAccessToken(any(), "")
            mockAccountManager.update(null)
        }
        assert(!viewModel.uiState.value.oauth)
        assert(!viewModel.uiState.value.isLoading)
    }
}