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
class GetStartedActivityTest{
    @get:Rule var activityScenarioRule = activityScenarioRule<GetStartedActivity>()
    @Test
    fun checkActivityVisibility(){
        onView(withId(R.id.getStartedActivity))
            .check(matches(isDisplayed()))
    }
    @Test
    fun checkingBackgroundVisibility(){
        onView(withId(R.id.background_photo))
            .check(matches(isDisplayed()))
    }
    @Test
    fun checkingTextVisibility(){
        onView(withId(R.id.appWelcome))
            .check(matches(isDisplayed()))

        onView(withId(R.id.get_started_label))
            .check(matches(isDisplayed()))
    }
    @Test
    fun checkingButtonVisibility(){
        onView(withId(R.id.btnGetStarted))
            .check(matches(isDisplayed()))
    }
    @Test
    fun checkingEditTextVisibility(){
        onView(withId(R.id.teUsername))
            .check(matches(isDisplayed()))
    }
    @Test
    fun testTextMatchesExpected(){
        onView(withId(R.id.appWelcome))
            .check(matches(withText(R.string.welcomeToApp)))
        
        onView(withId(R.id.get_started_label))
            .check(matches(withText(R.string.createUsername)))
        
        onView(withId(R.id.btnGetStarted))
            .check(matches(withText(R.string.getStarted)))
    }
}