package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("فضاء الرياضيات", appName)
  }

  @Test
  fun `verify planets exist for all trimesters`() {
    val t1 = com.example.data.repository.CurriculumRepository.getPlanetsForTrimester(com.example.data.model.Trimester.TRIMESTER_1)
    val t2 = com.example.data.repository.CurriculumRepository.getPlanetsForTrimester(com.example.data.model.Trimester.TRIMESTER_2)
    val t3 = com.example.data.repository.CurriculumRepository.getPlanetsForTrimester(com.example.data.model.Trimester.TRIMESTER_3)
    assertEquals(true, t1.isNotEmpty())
    assertEquals(true, t2.isNotEmpty())
    assertEquals(true, t3.isNotEmpty())
  }
}
