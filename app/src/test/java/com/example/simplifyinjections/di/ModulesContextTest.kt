package com.example.simplifyinjections.di

import androidx.activity.ComponentActivity
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.simplifyinjections.di.factory.ModuleFactory
import com.example.simplifyinjections.ui.main.MainViewModel
import org.robolectric.Robolectric.buildActivity
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class ModulesContextTest {

    private class TestActivity : ComponentActivity()

    // Mock de ModuleFactory
    private var activity: ComponentActivity? = null
    private val viewModel: MainViewModel = mockk()
    private val moduleFactory = mockk<ModuleFactory> {
        every { getInstance(MainViewModel::class.java) } returns viewModel
    }

    @After
    fun tearDown() {
        activity?.finish()
        ModulesContext.clearAll()
        unmockkAll()
    }

    @Test
    fun `addModule should add module to context`() {
        // Arrange
        ModulesContext.addModule(moduleFactory)

        // Act
        val result = ModulesContext.get<MainViewModel>()

        // Assert
        assertTrue(result== viewModel)
    }

    @Test(expected = NoClassDefFoundError::class)
    fun `get should throw when dependency not found`() {
        // Act
        ModulesContext.get<MainViewModel>()
        // Assert: exception expected
    }

    @Test
    fun `removeModule should remove module from context`() {
        // Arrange
        ModulesContext.addModule(moduleFactory)
        ModulesContext.removeModule(moduleFactory)

        // Act & Assert
        try {
            ModulesContext.get<MainViewModel>()
        } catch (e: NoClassDefFoundError) {
            assertTrue(e.message!!.contains("Definition not found"))
        }
    }

    @Test
    fun `clearAll should remove all modules`() {
        // Arrange
        ModulesContext.addModule(moduleFactory)
        ModulesContext.clearAll()

        // Act & Assert
        try {
            ModulesContext.get<MainViewModel>()
        } catch (e: NoClassDefFoundError) {
            assertTrue(e.message!!.contains("Definition not found"))
        }
    }

    @Test
    fun `inject should return lazy instance of dependency`() {
        // Arrange
        ModulesContext.addModule(moduleFactory)

        // Act
        val lazyDep = ModulesContext.inject<MainViewModel>()

        // Assert
        assertTrue(lazyDep.value == viewModel)
    }

    @Test
    fun `viewModel should return lazy ViewModel instance`() {
        ModulesContext.addModule(moduleFactory)
        ModulesInitializer().initialize()

        val controller = buildActivity(TestActivity::class.java).create().start().resume().visible()
        val activity = controller.get()
        this.activity = activity

        // Act
        val lazyVm = with(ModulesContext) { activity.viewModel<MainViewModel>() }

        // Assert
        assertTrue(lazyVm.value is MainViewModel)
    }
}
