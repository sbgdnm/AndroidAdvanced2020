package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.data.Task
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Test

class StatisticsUtilsTest {

    @Test
    fun `active and completed stats no completed returns hundred zero`() {
        val tasks = listOf<Task>(
                Task("title","desc",isCompleted = false)
        )
        val result = getActiveAndCompletedStats(tasks)
        /**
         * WITHOUT HAMCREST
        assertEquals(result.activeTasksPercent, 100f)
        assertEquals(result.completedTasksPercent, 0f)
        */

        /** WITH HAMCREST **/
        assertThat(result.activeTasksPercent, `is` (100f))
        assertThat(result.completedTasksPercent, `is` (0f))
    }

    @Test
    fun `active and completed stats no active return zero hundred`() {
        val tasks = listOf<Task>(
                Task("title","desc",isCompleted = true)
        )
        val result = getActiveAndCompletedStats(tasks)
        assertThat(result.activeTasksPercent, `is` (0f))
        assertThat(result.completedTasksPercent, `is` (100f))
    }

    @Test
    fun `active and completed stats both return sixty forty`() {
        val tasks = listOf(
                Task("title", "desc", isCompleted = true),
                Task("title", "desc", isCompleted = true),
                Task("title", "desc", isCompleted = true),
                Task("title", "desc", isCompleted = false),
                Task("title", "desc", isCompleted = false)
        )
        val result = getActiveAndCompletedStats(tasks)
        assertThat(result.completedTasksPercent, `is` (60f))
        assertThat(result.activeTasksPercent, `is` (40f))
    }

    @Test
    fun `active and completed stats returns error if tasks null`() {
        val result = getActiveAndCompletedStats(null)
        assertThat(result.activeTasksPercent, `is` (0f))
        assertThat(result.completedTasksPercent, `is` (0f))
    }

    @Test
    fun `active and completed stats returns error if tasks empty`() {
        val result = getActiveAndCompletedStats(emptyList())
        assertThat(result.activeTasksPercent, `is` (0f))
        assertThat(result.completedTasksPercent, `is` (0f))
    }
}