package zoe.project.in2thewok

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class RegisterActivityTest{
    @get:Rule var activityScenarioRule = activityScenarioRule<RegisterActivity>()
    @Test
    fun checkActivityVisibility(){
        onView(withId(R.id.registerActivity))
            .check(matches(isDisplayed()))
    }
    @Test
    fun checkingBackgroundVisibility(){
        onView(withId(R.id.background_photo))
            .check(matches(isDisplayed()))
    }
    @Test
    fun checkingEditTextVisibility(){
        onView(withId(R.id.teEmail))
            .check(matches(isDisplayed()))

        onView(withId(R.id.tePassword))
            .check(matches(isDisplayed()))
    }
    @Test
    fun checkingTextVisibility(){
        onView(withId(R.id.in2thewok))
            .check(matches(isDisplayed()))

        onView(withId(R.id.tvImageCreditMain))
            .check(matches(isDisplayed()))

        onView(withId(R.id.tvEmail))
            .check(matches(isDisplayed()))

        onView(withId(R.id.tvPassword))
            .check(matches(isDisplayed()))
    }
    @Test
    fun checkingButtonVisibility(){
        onView(withId(R.id.btnRegister))
            .check(matches(isDisplayed()))
    }
    @Test
    fun testTextMatchesExpected(){
        onView(withId(R.id.in2thewok))
            .check(matches(withText(R.string.appName)))

        onView(withId(R.id.tvImageCreditMain))
            .check(matches(withText(R.string.backgroundCredit)))

        onView(withId(R.id.btnRegister))
            .check(matches(withText(R.string.register)))

        onView(withId(R.id.tvEmail))
            .check(matches(withText(R.string.emailAddress)))

        onView(withId(R.id.tvPassword))
            .check(matches(withText(R.string.choosePassword)))
    }
}