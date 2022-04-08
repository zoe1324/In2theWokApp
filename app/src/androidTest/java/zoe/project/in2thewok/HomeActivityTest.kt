package zoe.project.in2thewok

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class HomeActivityTest{
    @get:Rule var activityScenarioRule = activityScenarioRule<HomeActivity>()
    @Test
    fun checkActivityVisibility(){
        onView(withId(R.id.homeActivity))
            .check(matches(isDisplayed()))
    }
    @Test
    fun checkNavBarVisibility(){
        onView(withId(R.id.bottomNavigationView))
            .check(matches(isDisplayed()))
    }
    @Test
    fun checkFragContainerVisibility(){
        onView(withId(R.id.frag_layout))
            .check(matches(isDisplayed()))
    }
}
